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

  /**
   * The complete raw CAMEO code for Actor1 (includes geographic, class, ethnic, religious, and type classes).
   * May be blank if the system was unable to identify an Actor
   */
  var code: String = actorData(0)
  /**
   * The actual name of the Actor.
   * In the case of a political leader or organization, this will be the leader’s formal name (GEORGE W BUSH, UNITED NATIONS)
   * for a geographic match it will be either the country or capital/major city name (UNITED STATES / PARIS),
   * and for ethnic, religious, and type matches it will reflect the root match class
   */
  var name: String = actorData(1)
  /**
   * The 3-character CAMEO code for the country affiliation.
   * May be blank if the system was unable to identify an Actor or determine its country affiliation
   */
  var countryCode: String = actorData(2)
  /**
   * If Actor is a known IGO/NGO/rebel organization (United Nations, World Bank, al-Qaeda, etc)
   * with its own CAMEO code, this field will contain that code
   */
  var knownGroupCode: String = actorData(3)
  /**
   * If the source document specifies the ethnic affiliation of Actor and that ethnic group has a CAMEO entry,
   * the CAMEO code is entered her
   */
  var ethnicCode: String = actorData(4)
  /**
   * If the source document specifies the religious affiliation of Actor and
   * that religious group has a CAMEO entry, the CAMEO code is entered here.
   */
  var religion1Code: String = actorData(5)
  /**
   * If multiple religious codes are specified for Actor, this contains the secondary code.
   * Some religion entries automatically use two codes, such as Catholic,
   * which invokes Christianity as Code1 and Catholicism as Code2
   */
  var religion2Code: String = actorData(6)
  /**
   * The 3-character CAMEO code of the CAMEO “type” or “role” of Actor, if specified.
   * This can be a specific role such as Police Forces, Government, Military, Political Opposition,
   * Rebels, etc, a broad role class such as Education, Elites, Media, Refugees, or
   * organizational classes like Non-Governmental Movement. Special codes such as Moderate and Radical
   * may refer to the operational strategy of a group.
   */
  var type1Code: String = actorData(7)
  /**
   * If multiple type/role codes are specified for Actor, this returns the second code.
   */
  var type2Code: String = actorData(8)
  /**
   * If multiple type/role codes are specified for Actor, this returns the third code.
   */
  var type3Code: String = actorData(9)

  def toArray: Array[String] = {
    Array(code, name, countryCode, knownGroupCode, ethnicCode, religion1Code, religion2Code,
      type1Code, type2Code, type3Code)
  }

  def this() {
    this(Array("", "", "", "", "", "", "", "", "", ""))
  }
}
