package com.ariasfreire.gdelt

import com.ariasfreire.gdelt.mllib.{AbstractParams, MLlibLDA}
import com.ariasfreire.gdelt.models.es.ScrapeResults
import com.ariasfreire.gdelt.models.lda._
import com.ariasfreire.gdelt.processors.exporters.MalletExporter
import com.ariasfreire.gdelt.processors.extractors.LargestContentExtractor
import com.ariasfreire.gdelt.processors.matchers.SimpleMatcher
import com.ariasfreire.gdelt.processors.parsers.BigQueryParser
import com.ariasfreire.gdelt.processors.{DataProcessor, TopicProcessor}
import com.ariasfreire.gdelt.utils.ContextUtils
import scopt.OptionParser


/**
 * Created by juanito on 10/07/15.
 */
object Main {

  private case class Params(
                             inputDir: String = "",
                             var outputDir: String = null,
                             indexName: String = "results",
                             overwrite: Boolean = false,
                             k: Int = 20,
                             maxIterations: Int = 100,
                             vocabSize: Int = 10000,
                             stopwordFile: String = "",
                             algorithm: String = "em",
                             checkpointDir: Option[String] = None,
                             checkpointInterval: Int = 10) extends AbstractParams[Params]

  def main(args: Array[String]) {
    val defaultParams = Params()

    val parser = new OptionParser[Params]("Topic Modeling") {
      head("Application of Spark's LDA to research topics on GDELT data.")
      opt[Int]("k")
        .text(s"number of topics. default: ${defaultParams.k}")
        .action((x, c) => c.copy(k = x))
      opt[Int]("maxIterations")
        .text(s"number of iterations of learning. default: ${defaultParams.maxIterations}")
        .action((x, c) => c.copy(maxIterations = x))
      opt[Int]("vocabSize")
        .text(s"number of distinct word types to use, chosen by frequency. (-1=all)" +
        s"  default: ${defaultParams.vocabSize}")
        .action((x, c) => c.copy(vocabSize = x))
      opt[Int]("checkpointInterval")
        .text(s"If checkpointDir is set, set the number of intervals between checkpoints" +
        s"  default: ${defaultParams.checkpointInterval}")
        .action((x, c) => c.copy(checkpointInterval = x))
      opt[String]("algorithm")
        .text(s"inference algorithm to use. em and online are supported." +
        s" default: ${defaultParams.algorithm}")
        .action((x, c) => c.copy(algorithm = x))
      opt[String]("stopwordFile")
        .text(s"filepath for a list of stopwords. Note: This must fit on a single machine.")
        .required()
        .action((x, c) => c.copy(stopwordFile = x))
      opt[String]("indexName")
        .text(s"Name for the default index in Elastic Search to store data in" +
        s"  default: ${defaultParams.indexName}")
        .action((x, c) => c.copy(indexName = x))
      opt[String]("checkpointDir")
        .text("Path to directory where checkpointing will be stored" +
        s"  Checkpointing helps with recovery and eliminates temporary shuffle files on disk." +
        s"  default: ${defaultParams.checkpointDir}")
        .action((x, c) => c.copy(checkpointDir = Some(x)))
      opt[Boolean]("overwrite")
        .text(s"Wether to overwrite output directory on new run" +
        s"  default: ${defaultParams.overwrite}")
        .action((x, c) => c.copy(overwrite = x))
      arg[String]("<input>")
        .text("input path to file containting GDELT data")
        .required()
        .action((x, c) => c.copy(inputDir = x))
      opt[String]("outputDir")
        .text("Path to directory where resulting files will be left")
        .action((x, c) => c.copy(outputDir = x))
    }

    parser.parse(args, defaultParams).map { params =>
      run(params)
    }.getOrElse {
      parser.showUsageAsError
      sys.exit(1)
    }

    def run(params: Params): Unit = {

      val dataSetName: String = Array(params.indexName, params.algorithm, params.k).mkString("_")
      if (params.outputDir == null) {
        params.outputDir = dataSetName
      }
      ContextUtils.overwrite = params.overwrite
      ContextUtils.indexName = params.indexName
      if (ContextUtils.overwrite) {
        // Delete indexed data when overwriting
        TopicTermsDataModel.dropIndex
      }

      // The topic data I need might already be indexed
      var scrapeResults: ScrapeResults = new ScrapeResults(dataSetName)
      var topicTermsDataArray: Array[TopicTermsDataModel] = TopicTermsDataModel.fromQuery(dataSetName)
      var processingEnd = 0.0
      var trainingEnd = 0.0
      if (topicTermsDataArray.length == 0) {

        // Configure a Processor for my needs
        val processingStart = System.nanoTime()
        val dataProcessor = new DataProcessor with BigQueryParser with LargestContentExtractor with MalletExporter
        dataProcessor.matcher = SimpleMatcher.get

        scrapeResults = dataProcessor.process(params.outputDir , params.inputDir)
        if (scrapeResults.totalRows == 0) {
          println("No results found, try with a different name of file.")
          return
        }
        val trainingStart = System.nanoTime()
        processingEnd = (trainingStart - processingStart) / 1e9

        // Run Distributed LDA
        topicTermsDataArray = new MLlibLDA(
          inputDir = params.outputDir,
          dataSetName = dataSetName,
          stopWordsFile = params.stopwordFile,
          k = params.k,
          maxIterations = params.maxIterations,
          algorithm = params.algorithm
        ).run

        trainingEnd = (System.nanoTime()- trainingEnd) / 1e9

        // Show parsing metrics and store results
        scrapeResults.summary()
        topicTermsDataArray.foreach { topicModel =>
          topicModel.indexData
        }
      }

      val inferingStart= System.nanoTime()

      val topicProcessor = new TopicProcessor(params.outputDir, topicTermsDataArray)

      val collectedData: Array[(String, Iterable[TopicInferenceModel])] = topicProcessor.run
      collectedData.foreach { (data: (String, Iterable[TopicInferenceModel])) =>
        val topicInferenceEntries: Array[TopicInferenceModel] = new Array[TopicInferenceModel](data._2.size)
        data._2.zipWithIndex.foreach { case (topicInference: TopicInferenceModel, zipIndex: Int) =>
          topicInferenceEntries(zipIndex) = topicInference
        }
        val dateTopicInfo = new TopicInferenceInfoModel(dataSetName, data._1, topicInferenceEntries)
        dateTopicInfo.indexData
      }
      val inferingEnd = (System.nanoTime()- inferingStart) / 1e9

      println(s"Finished!! Created data for index ${params.indexName} in dataset $dataSetName")
      println(s"Processing took: $processingEnd")
      println(s"Training took: $trainingEnd")
      println(s"Inferring took: $inferingEnd")
    }
  }
}

