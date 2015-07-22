package com.ariasfreire.gdelt.models.lda

/**
 * This model represents the chance of speaking about a certain topic (not modelled here) for a given date
 * Created by juanito on 15/07/15.
 */
class TopicChanceForDateModel(val date: String, val chance: Double) extends Serializable {

  def toJson: String = {
    s"""{"date": "$date", "chance":$chance}"""
  }
}
