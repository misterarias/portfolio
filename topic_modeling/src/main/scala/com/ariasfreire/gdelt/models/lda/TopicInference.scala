package com.ariasfreire.gdelt.models.lda

/**
 * Created by juanito on 15/07/15.
 */
class TopicInference(val topic: String, val chance: Double) extends Serializable {

  def toJson: String = {
    s"""{"topic": "$topic", "chance":$chance}"""
  }
}
