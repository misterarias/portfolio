package com.ariasfreire.gdelt.models.lda

/**
 * Created by juanito on 15/07/15.
 */
class TopicData(val term: String, val weight: Double) {

  def toJson: String = {
    s"""{"term": "$term", "weight":$weight}"""
  }
}
