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
                             maxIterations: Int = 10,
                             vocabSize: Int = 10000,
                             stopwordFile: String = "",
                             algorithm: String = "em") extends AbstractParams[Params]

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
      opt[Boolean]("overwrite")
        .text(s"Wether to overwrite output directory on new run" +
        s"  default: ${defaultParams.overwrite}")
        .action((x, c) => c.copy(overwrite = x))
      arg[String]("<input>")
        .text("input path to file containting GDELT data")
        .required()
        .action((x, c) => c.copy(inputDir = x))
      opt[String]("<outputDir>")
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

      val objectName: String = Array(params.indexName, params.algorithm, params.k).mkString("_")
      if (params.outputDir == null) {
        params.outputDir = objectName
      }
      ContextUtils.overwrite = params.overwrite
      ContextUtils.indexName = params.indexName

      // The topic data I need
      var topicTermsDataArray: Array[TopicTermsDataModel] = Array[TopicTermsDataModel]()

      // Configure a Processor for my needs
      val dataProcessor = new DataProcessor with BigQueryParser with LargestContentExtractor with MalletExporter
      dataProcessor.matcher = SimpleMatcher.get

      val scrapeResults: ScrapeResults = dataProcessor.process(objectName, params.inputDir)
      if (scrapeResults.totalRows == 0) {
        topicTermsDataArray = TopicTermsDataModel.fromQuery(scrapeResults.name)
      } else {

        // Delete indexed data the first time
        TopicTermsDataModel.dropIndex

        // Run Distributed LDA
        topicTermsDataArray = new MLlibLDA(
          params.outputDir,
          stopWordsFile = params.stopwordFile,
          k = params.k,
          maxIterations = params.maxIterations,
          algorithm = params.algorithm
        ).run

        // Show parsing metrics and store results
        scrapeResults.summary()
        topicTermsDataArray.foreach { topicModel =>
          topicModel.indexData
        }
      }

      val topicProcessor = new TopicProcessor(params.outputDir, topicTermsDataArray)

      val collectedData: Array[(String, Iterable[TopicInferenceModel])] = topicProcessor.run
      collectedData.foreach { (data: (String, Iterable[TopicInferenceModel])) =>
        val topicInferenceEntries: Array[TopicInferenceModel] = new Array[TopicInferenceModel](data._2.size)
        data._2.zipWithIndex.foreach { case (topicInference: TopicInferenceModel, zipIndex: Int) =>
          topicInferenceEntries(zipIndex) = topicInference
        }
        val dateTopicInfo = new TopicInferenceInfoModel(scrapeResults.name, data._1, topicInferenceEntries)
        dateTopicInfo.indexData
      }
    }
  }

}