package com.ariasfreire.gdelt.models.lda

import java.util

import com.ariasfreire.gdelt.utils.ContextUtils
import com.sksamuel.elastic4s.ElasticDsl.{index, _}
import com.sksamuel.elastic4s.source.Indexable
import org.elasticsearch.search.SearchHitField

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by juanito on 16/07/15.
 */
class TopicTermsDataModel(
                           val dataSetName: String,
                           var topicName: String = "",
                           var termsData: Array[TopicTermModel] = Array.empty) extends Serializable {


  implicit object TopicModelIndexable extends Indexable[TopicTermsDataModel] {
    override def json(t: TopicTermsDataModel): String =
      t.toJson.stripMargin
  }

  /**
   * XXX This should be a storage-agnostic trait function
   */
  def indexData = {
    ContextUtils.esClient.execute {
      index into TopicTermsDataModel.indexName source this
    }
  }

  def toJson: String = {
    s"""{"dataSetName": "$dataSetName", "topicName": "$topicName", "topics": [""" +
      termsData.map {
        _.toJson
      }.mkString(",") + "]}"
  }

  def fromElasticSearch(fields: util.Map[String, SearchHitField]): Unit = {
    topicName = fields.get("topicName").getValues.get(0).asInstanceOf[String]

    val weights = fields.get("topics.weight").getValues
    val terms = fields.get("topics.term").getValues
    val totalTopics = weights.size()

    termsData = new Array[TopicTermModel](totalTopics)
    for (i <- 0 to totalTopics - 1) {
      val weight = weights.get(i).asInstanceOf[Double]
      val term = terms.get(i).asInstanceOf[String]
      termsData(i) = new TopicTermModel(term = term, weight = weight)
    }
  }
}

object TopicTermsDataModel {
  val indexName: String = "results"
  val indexType: (String, String) = indexName -> "topics"

  def dropIndex = {
    ContextUtils.esClient.execute {
      deleteIndex(indexName)
    } await
  }

  def fromQuery(dataSetName: String): Array[TopicTermsDataModel] = {
    val topicModel = new TopicTermsDataModel(dataSetName)

    ContextUtils.esClient.execute {
      search in indexType query dataSetName.replace("/", "//") fields(
        "topics.weight", "topics.term", "topicName")
    } map { response =>
      val queryTopicModels = new Array[TopicTermsDataModel](response.getHits.hits.length)

      var index = 0
      for (x <- response.getHits.hits()) {
        topicModel.fromElasticSearch(x.getFields)
        queryTopicModels(index) = topicModel
        index += 1
      }
      queryTopicModels
    } await()
  }
}