package com.ariasfreire.gdelt.models.lda

/**
 * Created by juanito on 16/07/15.
 */
class TopicModel(
                  val dataSetName: String,
                  val topicName: String,
                  val termsData: Array[TopicData]) extends Serializable {

  def toJson: String = {
    s"""{"dataSetName": "$dataSetName", "topicName": "$topicName", "topics": [""" +
      termsData.map {
        _.toJson
      }.mkString(",") + "]}"
  }
}
