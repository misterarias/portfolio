package com.ariasfreire.gdelt

import com.ariasfreire.gdelt.mllib.MLlibLDA
import com.ariasfreire.gdelt.models.es.ScrapeResults
import com.ariasfreire.gdelt.models.lda.{TopicData, TopicModel}
import com.ariasfreire.gdelt.processors.Processor
import com.ariasfreire.gdelt.processors.exporters.MalletExporter
import com.ariasfreire.gdelt.processors.extractors.LargestContentExtractor
import com.ariasfreire.gdelt.processors.matchers.SimpleMatcher
import com.ariasfreire.gdelt.processors.parsers.BigQueryParser
import com.ariasfreire.gdelt.utils.ContextUtils
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.source.Indexable

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by juanito on 10/07/15.
 */
object Main {


  def main(args: Array[String]) {

    if (args.length < 3)
      throw new RuntimeException("Expected params: <csv file> <stopwords file> <output dir> [<overwrite>]")

    implicit object TopicModelIndexable extends Indexable[TopicModel] {
      override def json(t: TopicModel): String =
        t.toJson.stripMargin
    }

    val csvFile = args(0)
    val stopWordsFile = args(1)
    val outputDir = args(2)
    ContextUtils.overwrite = "1".equals(args(3))

    val client = ElasticClient.remote("127.0.0.1", 9300)

    // Configure a Processor for my needs
    val parser = new Processor with BigQueryParser with LargestContentExtractor with MalletExporter
    parser.matcher = SimpleMatcher.get

    val scrapeResults: ScrapeResults = parser.process(outputDir, csvFile)
    if (scrapeResults.totalRows == 0) {

      val results: Array[TopicModel] = client.execute {
        search in "results" / "topics" query scrapeResults.name.replace("/", "//") fields(
          "topics.weight", "topics.term", "topicName")
      } map { response =>
        val topicModel = new Array[TopicModel](response.getHits.hits.length)

        var index = 0
        for (x <- response.getHits.hits()) {
          val fields = x.getFields

          val topicName = fields.get("topicName").getValues.get(0).asInstanceOf[String]

          val weights = fields.get("topics.weight").getValues
          val terms = fields.get("topics.term").getValues
          val totalTopics = weights.size()

          val topicData: Array[TopicData] = new Array[TopicData](totalTopics)
          for (i <- 0 to totalTopics - 1) {
            val weight = weights.get(i).asInstanceOf[Double]
            val term = terms.get(i).asInstanceOf[String]
            topicData(i) = new TopicData(term = term, weight = weight)
          }

          topicModel(index) = new TopicModel(scrapeResults.name, topicName, topicData)
          index += 1
        }
        topicModel
      } await

      print(results)


    } else {

      // Run Distributed LDA
      val topics: Array[Array[TopicData]] = new MLlibLDA(
        input = Seq(outputDir),
        stopWordsFile = stopWordsFile
      ).run

      // Show parsing metrics
      scrapeResults.topicData = topics
      scrapeResults.summary()

      topics.zipWithIndex.foreach { case (topics: Array[TopicData], id) =>
        val topicModel = new TopicModel(scrapeResults.name, s"Topic $id", topics)
        // Store corpus-inferred topics into ES
        client.execute {
          index into "results" / "topics" source topicModel
        }
      }

      printf("Topic Modelling finished")
    }
  }
}