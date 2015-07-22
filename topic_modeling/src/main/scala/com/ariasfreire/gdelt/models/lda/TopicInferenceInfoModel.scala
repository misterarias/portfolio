package com.ariasfreire.gdelt.models.lda

import com.ariasfreire.gdelt.utils.ContextUtils
import com.sksamuel.elastic4s.ElasticDsl.{index, _}
import com.sksamuel.elastic4s.source.Indexable
import org.elasticsearch.indices.IndexMissingException

/**
 * Created by juanito on 17/07/15.
 */
class TopicInferenceInfoModel(val dataSetName: String,
                              val date: String,
                              val topicInference: Array[TopicInferenceModel]) {

  implicit object DateTopicInfoIndexable extends Indexable[TopicInferenceInfoModel] {
    override def json(t: TopicInferenceInfoModel): String = t.toJson.stripMargin
  }

  /**
   * XXX This should be a storage-agnostic trait function
   */
  def indexData = {
    ContextUtils.esClient.execute {
      index into TopicInferenceInfoModel.fullIndexName source this
    }
  }

  def toJson: String =
    s"""{"dataSetName": "$dataSetName", "date": "$date" , "topics_inferred" : [""" +
      topicInference.map(_.toJson).mkString(",") +
      "]}"
}

object TopicInferenceInfoModel {

  val indexType = "inferred"
  val fullIndexName: (String, String) = ContextUtils.indexName -> indexType
}


