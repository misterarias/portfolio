package com.ariasfreire.gdelt

import com.ariasfreire.gdelt.mllib.MLlibLDA
import com.ariasfreire.gdelt.models.es.ScrapeResults
import com.ariasfreire.gdelt.models.lda.{DateTopicInfo, TopicData, TopicInference, TopicModel}
import com.ariasfreire.gdelt.processors.Processor
import com.ariasfreire.gdelt.processors.exporters.MalletExporter
import com.ariasfreire.gdelt.processors.extractors.LargestContentExtractor
import com.ariasfreire.gdelt.processors.matchers.SimpleMatcher
import com.ariasfreire.gdelt.processors.parsers.BigQueryParser
import com.ariasfreire.gdelt.utils.ContextUtils
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.source.Indexable
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

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

    // ES Client data
    val client = ElasticClient.remote("127.0.0.1", 9300)
    val topicIndex = "results" / "topics"
    val topicInferredIndex = "results" / "inferred"

    // The topic data I need
    var topicData: Array[TopicModel] = Array[TopicModel]()

    // Configure a Processor for my needs
    val parser = new Processor with BigQueryParser with LargestContentExtractor with MalletExporter
    parser.matcher = SimpleMatcher.get


    val scrapeResults: ScrapeResults = parser.process(outputDir, csvFile)
    if (scrapeResults.totalRows == 0) {

      topicData = client.execute {
        search in topicIndex query scrapeResults.name.replace("/", "//") fields(
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

      print(topicData)
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
        topicData(id) = topicModel
        // Store corpus-inferred topics into ES
        client.execute {
          index into "results" / "topics" source topicModel
        }
      }

      printf("Topic Modelling finished")
    }

    // Voilà! I have filled topic Data
    val conf = ContextUtils.conf
    conf.registerKryoClasses(Array(classOf[TopicData], classOf[TopicModel]))
    val sc = new SparkContext(conf)

    // Prepare a data structure
    val dateTopicToText: Array[(String, String)] =
      sc.wholeTextFiles(scrapeResults.name).map { (files: (String, String)) =>

        // Next time maybe use SequenceFiles?
        val date: String = files._1.split("/").last.replace(".txt", "")
        val content: String = files._2
        (date, content)
      }.collect()
    val combinedData = new Array[(TopicModel, String, String)](dateTopicToText.length * topicData.length)
    var i = 0
    for (t <- topicData) {
      for (dtt <- dateTopicToText) {
        combinedData(i) = (t, dtt._1, dtt._2)
        i += 1
      }
    }

    // Returns the chance that texts found for date D speak about topic X
    val topicPerDateRDD: RDD[(String, TopicInference)] =
      sc.parallelize(wrapRefArray(combinedData), Math.min(combinedData.length, 60))
        .map(data => {
        val topicData = data._1
        val date = data._2
        val text = data._3

        // TODO: Logic to infer topics from text
        val chance: Double = Math.random()

        (date, new TopicInference(topicData.topicName, chance))
      })

    val collectedData: Array[(String, Iterable[TopicInference])] =
      topicPerDateRDD.groupByKey().collect()

    implicit object DateTopicInfoIndexable extends Indexable[DateTopicInfo] {
      override def json(t: DateTopicInfo): String = t.toJson.stripMargin
    }

    collectedData.foreach { (data: (String, Iterable[TopicInference])) =>
      val topicInferenceEntries: Array[TopicInference] = new Array[TopicInference](data._2.size)
      var i = 0
      data._2.foreach { topicInference =>
        topicInferenceEntries(i) = topicInference
        i += 1
      }
      val dateTopicInfo = new DateTopicInfo(data._1, topicInferenceEntries)

      client.execute {
        index into topicInferredIndex source dateTopicInfo
      }
    }
    println(collectedData)

  }

}