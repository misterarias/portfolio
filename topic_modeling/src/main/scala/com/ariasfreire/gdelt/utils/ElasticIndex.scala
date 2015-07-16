package com.ariasfreire.gdelt.utils

import com.ariasfreire.gdelt.models.lda.{TopicData, TopicModel}
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

    implicit object TopicModelIndexable extends Indexable[TopicModel] {
      override def json(t: TopicModel): String =
        t.toJson.stripMargin
    }

    val topicData = Array(
      Array(new TopicData("term1", 0.9), new TopicData("term2", 0.1)),
      Array(new TopicData("term3", 0.4), new TopicData("term2", 0.6)),
      Array(new TopicData("term1", 0.1), new TopicData("term4", 0.2), new TopicData("term5", 0.7))
    )

    val topicModels = new Array[TopicModel](topicData.length)
    topicData.zipWithIndex.foreach { case (topics: Array[TopicData], id) =>
      topicModels(id) = new TopicModel("test_dataset", s"Topic $id", topics)
    }

    // Store corpus-inferred topics into ES
    topicModels.foreach { topicModel =>
      client.execute {
        index into "test" -> "topics" source topicModel
      }
    }

    client.close()
  }
}
