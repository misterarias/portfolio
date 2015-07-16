package com.ariasfreire.gdelt.models.lda

/**
 * Created by juanito on 16/07/15.
 */
class TopicModel(dataSetName: String, topicName: String, topicData: Array[TopicData]) {

  def toJson: String = {
    s"""{"dataSetName": "$dataSetName", "topicName": "$topicName", "topics": [""" +
      topicData.map {
        _.toJson
      }.mkString(",") + "]}"
  }
}
