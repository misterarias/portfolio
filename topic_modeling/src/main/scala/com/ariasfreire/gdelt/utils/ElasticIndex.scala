package com.ariasfreire.gdelt.utils

import com.ariasfreire.gdelt.models.es.ScrapeResults
import com.ariasfreire.gdelt.models.lda.TopicData
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.source.Indexable

/**
 * Create ElasticSearcj indexes to play with
 * Created by juanito on 13/07/15.
 */
object ElasticIndex {

  def main(args: Array[String]): Unit = {

    val client = ElasticClient.remote("master.devarias.com", 9300)


    implicit object ScrapeResultsIndexable extends Indexable[ScrapeResults] {
      override def json(t: ScrapeResults): String =
        t.toJson.stripMargin
    }

    val scrapeResults = new ScrapeResults("testing")
    scrapeResults.totalRows = 100

    val topicData = Array(
      Array(new TopicData("term1", 0.9), new TopicData("term2", 0.1)),
      Array(new TopicData("term1", 0.4), new TopicData("term2", 0.6))
    )

    scrapeResults.topicData = topicData
    print(scrapeResults.toJson)
    val resp = client.execute {
      index into "results" / "other_type" source scrapeResults
      //   index into "results" / "type" source scrapeResults
    }.await


    client execute {
      delete index "topics"
    }
    client.close()
  }
}
