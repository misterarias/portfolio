package com.ariasfreire.gdelt.models.lda

import java.util

import com.ariasfreire.gdelt.utils.ContextUtils
import com.sksamuel.elastic4s.ElasticDsl.{index, _}
import com.sksamuel.elastic4s.source.Indexable
import org.elasticsearch.indices.IndexMissingException
import org.elasticsearch.search.SearchHitField

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * For a given topic, represents the terms associated to it, with a given weight,
 * as discovered by LDA
 * Created by juanito on 16/07/15.
 */
class TopicTermsDataModel(val topicName: String,
                          var termsData: Array[TopicTermModel] = Array.empty,
                          var chancesForDate: Array[TopicChanceForDateModel] = Array.empty
                           ) extends Serializable {

  implicit object TopicModelIndexable extends Indexable[TopicTermsDataModel] {
    override def json(t: TopicTermsDataModel): String =
      t.toJson.stripMargin
  }

  /**
   * @todo This should be a storage-agnostic trait function
   */
  def indexData = {
    ContextUtils.esClient.execute {
      index into TopicTermsDataModel.fullIndexName source this
    }
  }

  def toJson: String = {
    s"""{"topicName": "$topicName", "terms": [""" +
      termsData.map {
        _.toJson
      }.mkString(",") +
      "], \"dates\" : [" +
      chancesForDate.map(_.toJson).mkString(",") +
      "]}"
  }

/*  /**
   * Populate this object from an Elastic Search client result
   * @param fields  Fields coming from the ES Client
   */
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
  }*/
}

object TopicTermsDataModel {

  val indexType = "topics"
  val fullIndexName: (String, String) = ContextUtils.indexName -> indexType

  /*def fromQuery(dataSetName: String): Array[TopicTermsDataModel] = {

    try {
      ContextUtils.esClient.execute {
        search in fullIndexName query dataSetName.replace("/", "//") fields(
          "topics.weight", "topics.term", "topicName")
      } map { response =>
        val topicModel = new TopicTermsDataModel(dataSetName)
        val queryTopicModels = new Array[TopicTermsDataModel](response.getHits.hits.length)

        var index = 0
        for (x <- response.getHits.hits()) {
          topicModel.fromElasticSearch(x.getFields)
          queryTopicModels(index) = topicModel
          index += 1
        }
        queryTopicModels
      } await()
    } catch {
      case x: IndexMissingException =>
        println(s"Index ${ContextUtils.indexName} does not exist")
        Array.empty
      case undefined: Throwable =>
        println(s"Undefined error querying Index: ${undefined.getLocalizedMessage}")
        Array.empty
    }
  }*/
}
