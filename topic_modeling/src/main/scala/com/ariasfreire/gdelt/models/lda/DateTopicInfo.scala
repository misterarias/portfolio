package com.ariasfreire.gdelt.models.lda

/**
 * Created by juanito on 17/07/15.
 */
class DateTopicInfo(val date: String, val topicInference: Array[TopicInference]) {

  def toJson: String =
    s"""{"date": "${date}" , "topics_inferred" : [""" +
      topicInference.map(_.toJson).mkString(",") +
      "]}"
}


