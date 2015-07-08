package com.ariasfreire.gdelt.matchers

import com.ariasfreire.gdelt.models.{Actor, Geography, Row}
import com.ariasfreire.gdelt.utils.StringUtils

/**
 * Given a set of conditions, a matcher checks if a Row object has to be processed
 *
 * Created by juanito on 5/07/15.
 */
class ConditionsMatcher(
                         location: Geography = null,
                         eventCodes: Seq[String] = null,
                         actor: Actor = null
                         ) extends Serializable {

  private def actorCheck(row: Row): Boolean = {
    if (actor == null) {
      // if no condition is sent, assume true
      return true
    }
    actorCheck(row.actor1Data) || actorCheck(row.actor2Data)
  }

  private def actorCheck(currentActor: Actor): Boolean = {
    if (currentActor == null) {
      return false
    }

    StringUtils.gdeltCompare(currentActor.countryCode, actor.countryCode)
  }

  private def geoCheck(row: Row): Boolean = {
    // if no condition is sent, assume true
    if (location == null) {
      return true
    }

    geoCheck(row.actor1Geography) ||
      geoCheck(row.actor2Geography) ||
      geoCheck(row.actionGeography)
  }

  private def geoCheck(currentGeography: Geography): Boolean = {
    if (currentGeography == null) {
      return false
    }

    // geoType limits too much
    // currentGeography.geoType == location.geoType ||
    StringUtils.gdeltCompare(location.geoADM1Code, currentGeography.geoADM1Code) ||
      StringUtils.gdeltCompare(location.geoCountryCode, currentGeography.geoCountryCode)
  }

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
