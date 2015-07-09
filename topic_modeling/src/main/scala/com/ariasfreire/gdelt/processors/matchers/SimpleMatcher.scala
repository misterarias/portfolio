package com.ariasfreire.gdelt.processors.matchers

import com.ariasfreire.gdelt.models.Row
import com.ariasfreire.gdelt.utils.StringUtils

/**
 * Given a set of codes for certain parts of a Row, look for any match.
 * At least a match is needed for defined set of items to return a positive
 *
 * Created by juanito on 5/07/15.
 */
class SimpleMatcher(var countryCodes: Seq[String] = null,
                    var eventCodes: Seq[String] = null,
                    var actorCodes: Seq[String] = null
                     ) extends NaiveMatcher {


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

  private def geoCheck(row: Row): Boolean = {

    if (countryCodes == null) {
      return true
    }

    // Actor MUST be in the region, and action as well
    if (!(row.actor1Data != null && StringUtils.gdeltCompare(countryCodes, row.actor1Data.countryCode) ||
      row.actor2Data != null && StringUtils.gdeltCompare(countryCodes, row.actor2Data.countryCode)))
      return false

    if (!(row.actionGeography != null && StringUtils.gdeltCompare(countryCodes, row.actionGeography.geoCountryCode) ||
      row.actor1Geography != null && StringUtils.gdeltCompare(countryCodes, row.actor1Geography.geoCountryCode) ||
      row.actor2Geography != null && StringUtils.gdeltCompare(countryCodes, row.actor2Geography.geoCountryCode)))
      return false

    true
  }

  private def actorCheck(row: Row): Boolean = {

    if (actorCodes == null) {
      return true
    }

    actorCodes.foreach(code => {
      if (row.actor1Data != null &&
        (StringUtils.gdeltCompare(code, row.actor1Data.type1Code) || row.actor1Data.code.contains(code)) ||
        row.actor2Data != null &&
          (row.actor2Data.code.contains(code) || StringUtils.gdeltCompare(code, row.actor2Data.type1Code))
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
  override def checkConditions(row: Row): Boolean = {

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

object SimpleMatcher {
  def get: SimpleMatcher = {
    /* val relevantEventCodes = Seq(
       //      "091", // Investigate crime, corruption
       //      "1041", // Demand leadership change
       //      "1231", // Reject request to change leadership -> might include presidential dimissions
       //      "1121", // Accuse of crime, corruption
       //      // From here on, not really sure
       //      "1322", // Threaten to ban political parties or politicians
       //      "1722" // Ban political parties or politicians
       //"015", // Acknowledge or claim responsibility
       //"016", // Deny responsibility
       //"061" // Cooperate economically
     )*/
    val relevantCountryCodes = Seq("ESP", "SP")
    new SimpleMatcher(
      countryCodes = relevantCountryCodes)
  }
}
