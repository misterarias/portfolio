package com.ariasfreire.gdelt

import com.ariasfreire.gdelt.mllib.MLlibLDA
import com.ariasfreire.gdelt.models.es.ScrapeResults
import com.ariasfreire.gdelt.models.lda._
import com.ariasfreire.gdelt.processors.exporters.MalletExporter
import com.ariasfreire.gdelt.processors.extractors.LargestContentExtractor
import com.ariasfreire.gdelt.processors.matchers.SimpleMatcher
import com.ariasfreire.gdelt.processors.parsers.BigQueryParser
import com.ariasfreire.gdelt.processors.{DataProcessor, TopicProcessor}
import com.ariasfreire.gdelt.utils.ContextUtils


/**
 * Created by juanito on 10/07/15.
 */
object Main {

  def main(args: Array[String]) {

    if (args.length < 3)
      throw new RuntimeException("Expected params: <csv file> <stopwords file> <output dir> [<overwrite>]")


    val csvFile = args(0)
    val stopWordsFile = args(1)
    val outputDir = args(2)
    ContextUtils.overwrite = "1".equals(args(3))

    // The topic data I need
    var topicTermsDataArray: Array[TopicTermsDataModel] = Array[TopicTermsDataModel]()

    // Configure a Processor for my needs
    val parser = new DataProcessor with BigQueryParser with LargestContentExtractor with MalletExporter
    parser.matcher = SimpleMatcher.get

    val scrapeResults: ScrapeResults = parser.process(outputDir, csvFile)
    if (scrapeResults.totalRows == 0) {
      topicTermsDataArray = TopicTermsDataModel.fromQuery(scrapeResults.name)
    } else {

      // Delete indexed data the first time
      TopicTermsDataModel.dropIndex

      // Run Distributed LDA
      topicTermsDataArray = new MLlibLDA(outputDir,
        stopWordsFile = stopWordsFile
      ).run

      // Show parsing metrics and store results
      scrapeResults.summary()
      topicTermsDataArray.foreach { topicModel =>
        topicModel.indexData
      }
    }

    val topicProcessor = new TopicProcessor(outputDir, topicTermsDataArray)

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