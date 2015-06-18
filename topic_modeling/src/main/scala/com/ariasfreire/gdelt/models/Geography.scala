package com.ariasfreire.gdelt.models

import com.ariasfreire.gdelt.models.utils.ModelUtils

/**
 * The georeferenced location for an actor may not always match the Actor1_CountryCode or
 * Actor2_CountryCode field, such as in a case where the President of Russia is
 * visiting Washington, DC in the United States, in which case the Actor1_CountryCode would contain the
 * code for Russia, while the georeferencing fields below would contain a match for Washington, DC
 *
 * Created by juanito on 19/06/15.
 */
class Geography(actorData: Array[String]) {

  /**
   * This field specifies the geographic resolution of the match type and holds one of the following
   * 1=COUNTRY (match was at the country level),
   * 2=USSTATE (match was to a US state),
   * 3=USCITY (match was to a US city or landmark),
   * 4=WORLDCITY (match was to a city or landmark outside the US),
   * 5=WORLDSTATE (match was to an  Administrative Division 1 outside the US – roughly equivalent to a US state).
   */
  var geoType: Int = ModelUtils.getInt(actorData(0))

  /**
   * This is the full human-readable name of the matched location.
   */
  var geoFullname = actorData(1)
  /**
   * 2-character FIPS10-4 country code for the location
   */
  var geoCountryCode = actorData(2)
  /**
   * The 2-character FIPS10-4 country code followed by 2-character FIPS10-4
   * administrative division housing the landmark.
   */
  var geoADM1Code = actorData(3)
  var geoLatitude: Float = ModelUtils.getFloat(actorData(4))
  var geoLongitude: Float = ModelUtils.getFloat(actorData(5))
  /**
   * The GNS or GNIS FeatureID for this location
   */
  var geoFeatureID: String = actorData(6)
}
