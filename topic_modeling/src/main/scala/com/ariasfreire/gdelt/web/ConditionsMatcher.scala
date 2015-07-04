package com.ariasfreire.gdelt.web

import com.ariasfreire.gdelt.models.{Actor, Geography, Row}

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
    // TODO - filter by actor
    true
  }

  private def geoCheck(row: Row): Boolean = {
    // if no condition is sent, assume true
    if (location == null) {
      return true
    }

    val geoFlag: Boolean = row.actor1Geography != null && geoCheck(row.actor1Geography) ||
      row.actor2Geography != null && geoCheck(row.actor2Geography) ||
      row.actionGeography != null && geoCheck(row.actionGeography)
    if (!geoFlag)
      return false
    true
  }

  private def geoCheck(currentGeography: Geography): Boolean = {

    (currentGeography.geoType == location.geoType) &&
      (
        location.geoADM1Code.equalsIgnoreCase(currentGeography.geoADM1Code) ||
          location.geoCountryCode.equalsIgnoreCase(currentGeography.geoCountryCode) ||
          location.geoFullname.equalsIgnoreCase(currentGeography.geoFullname)
        )
  }

  private def eventCodesCheck(currentEventCodes: Seq[String]): Boolean = {
    if (eventCodes == null) {
      // if no condition is sent, assume true
      return true;
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
   * @param row
   * @return
   */
  def checkConditions(row: Row): Boolean = {
    if (!actorCheck(row))
      return false

    if (!geoCheck(row)) {
      return false
    }

    if (!eventCodesCheck(Seq(row.rootEventCode, row.baseEventCode, row.eventCode)))
      return false

    true
  }
}
