package com.ariasfreire.gdelt.models.lda

import com.ariasfreire.gdelt.utils.ContextUtils
import com.sksamuel.elastic4s.ElasticDsl.{index, _}
import com.sksamuel.elastic4s.source.Indexable

/**
 * Created by juanito on 17/07/15.
 */
class TopicInferenceInfoModel(val date: String, val topicInference: Array[TopicInferenceModel]) {
  val topicInferredIndex = "results" / "inferred"

  implicit object DateTopicInfoIndexable extends Indexable[TopicInferenceInfoModel] {
    override def json(t: TopicInferenceInfoModel): String = t.toJson.stripMargin
  }

  /**
   * XXX This should be a storage-agnostice trait function
   */
  def indexData = {
    ContextUtils.esClient.execute {
      index into topicInferredIndex source this
    }
  }

  def toJson: String =
    s"""{"date": "${date}" , "topics_inferred" : [""" +
      topicInference.map(_.toJson).mkString(",") +
      "]}"
}


