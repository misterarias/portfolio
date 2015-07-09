package com.ariasfreire.gdelt.processors.matchers

import com.ariasfreire.gdelt.models.Row

/**
 * Created by juanito on 12/07/15.
 */
class NaiveMatcher extends Serializable {
  def checkConditions(row: Row): Boolean = true
}
