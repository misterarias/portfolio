package com.ariasfreire.gdelt.models.lda

/**
 * Models the weight of a given term
 * @author juanito
 */
class TopicTermModel(val term: String, val weight: Double) extends Serializable {

  def toJson: String = {
    s"""{"term": "$term", "weight":$weight}"""
  }
}
