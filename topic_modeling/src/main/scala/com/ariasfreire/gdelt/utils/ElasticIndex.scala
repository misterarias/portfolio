package com.ariasfreire.gdelt.utils

import com.ariasfreire.gdelt.models.lda.{TopicTermsDataModel, TopicTermModel, TopicTermsDataModel$}
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

    implicit object TopicModelIndexable extends Indexable[TopicTermsDataModel] {
      override def json(t: TopicTermsDataModel): String =
        t.toJson.stripMargin
    }

    val topicData = Array(
      Array(new TopicTermModel("term1", 0.9), new TopicTermModel("term2", 0.1)),
      Array(new TopicTermModel("term3", 0.4), new TopicTermModel("term2", 0.6)),
      Array(new TopicTermModel("term1", 0.1), new TopicTermModel("term4", 0.2), new TopicTermModel("term5", 0.7))
    )

    val topicModels = new Array[TopicTermsDataModel](topicData.length)
    topicData.zipWithIndex.foreach { case (topics: Array[TopicTermModel], id) =>
      topicModels(id) = new TopicTermsDataModel("test_dataset", s"Topic $id", topics)
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
