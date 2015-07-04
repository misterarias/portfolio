package com.ariasfreire.gdelt.models

/**
 * The raw CAMEO code for each actor contains an array of coded attributes indicating
 * geographic, ethnic, and  religious affiliation and the actor’s role in
 * the environment (political elite, military officer, rebel, etc).
 * These 3-character codes may be combined in any order and are concatenated together
 * to form the final raw actor CAMEO code
 *
 * NOTE: all attributes in this section other than CountryCode are derived from
 * the TABARI ACTORS dictionary and are NOT supplemented from information in the text.
 * Thus, if the text refers to a group as “Radicalized terrorists,” but the TABARI ACTORS
 * dictionary labels that group as “Insurgents,” the latter label will be  used.
 *
 * Created by juanito on 19/06/15.
 */
class Actor(actorData: Array[String]) extends Serializable {

  var code: String = actorData(0)
  var name: String = actorData(1)
  var countryCode: String = actorData(2)
  var knownGroupCode: String = actorData(3)
  var ethnicCode: String = actorData(4)
  var religion1Code: String = actorData(5)
  var religion2Code: String = actorData(6)
  var type1Code: String = actorData(7)
  var type2Code: String = actorData(8)
  var type3Code: String = actorData(9)

  def toArray: Array[String] = {
    Array(code, name, countryCode, knownGroupCode, ethnicCode, religion1Code, religion2Code,
      type1Code, type2Code, type3Code)
  }

  def this() {
    this(Array("", "", "", "", "", "", "", "", "", ""))
  }
}
