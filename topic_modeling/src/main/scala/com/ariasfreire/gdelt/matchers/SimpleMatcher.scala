package com.ariasfreire.gdelt.matchers

import com.ariasfreire.gdelt.models.Row
import com.ariasfreire.gdelt.utils.StringUtils

/**
 * Given a set of codes for certain parts of a Row, look for any match.
 * At least a match is needed for defined set of items to return a positive
 *
 * Created by juanito on 5/07/15.
 */
class SimpleMatcher(countryCodes: Seq[String] = null,
                    eventCodes: Seq[String] = null,
                    actorCodes: Seq[String] = null
                     ) extends Serializable {


  private def eventCodesCheck(currentEventCodes: Seq[String]): Boolean = {
    if (eventCodes == null) {
      // if no condition is sent, assume true
      return true
    }
    currentEventCodes.foreach(code => {
      if (eventCodes.contains(code)) {
        return true
      }
    })
    false
  }

  def geoCheck(row: Row): Boolean = {

    if (countryCodes == null) {
      return true
    }

    countryCodes.foreach(code => {
      if (StringUtils.gdeltCompare(code, row.actor1Data.countryCode) ||
        StringUtils.gdeltCompare(code, row.actor2Data.countryCode) ||
        StringUtils.gdeltCompare(code, row.actionGeography.geoCountryCode) ||
        StringUtils.gdeltCompare(code, row.actor1Geography.geoCountryCode) ||
        StringUtils.gdeltCompare(code, row.actor2Geography.geoCountryCode)
      )
        return true
    })
    false

  }

  def actorCheck(row: Row): Boolean = {

    if (actorCodes == null) {
      return true
    }

    actorCodes.foreach(code => {
      if (row.actor1Data.code.contains(code) ||
        row.actor2Data.code.contains(code) ||
        StringUtils.gdeltCompare(code, row.actor1Data.type1Code) ||
        StringUtils.gdeltCompare(code, row.actor2Data.type1Code)
      )
        return true
    })
    false
  }

  /**
   * Match given row against this object's conditions
   * @param row Input row to compare, if it's null the comparison is false
   * @return True if input row matches against conditions; Returns false if Row is null
   */
  def checkConditions(row: Row): Boolean = {

    if (row == null) {
      return false
    }

    if (!actorCheck(row))
      return false

    if (!geoCheck(row)) {
      return false
    }

    if (!eventCodesCheck(Seq(row.rootEventCode, row.baseEventCode, row.eventCode))) {
      return false
    }

    true
  }
}
