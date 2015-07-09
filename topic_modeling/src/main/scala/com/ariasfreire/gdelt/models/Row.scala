package com.ariasfreire.gdelt.models

import com.ariasfreire.gdelt.models.utils.ModelUtils

/**
 * Created by juanito on 18/06/15.
 */
class Row(tsvRowData: Array[String]) extends Serializable {

  /**
   * Globally unique identifier assigned to each event record that uniquely
   * identifies it in the master dataset
   *
   * If duplicates are to be found, it's considered safe to ignore them
   */
  var globalEventId: Int = -1
  var day: String = ""
  var monthYear: String = ""
  var year: String = ""
  /**
   * Rough measure of the % this year is in
   */
  var fractionDate: Float = 0f // (MONTH * 30 + DAY) / 365

  var actor1Data: Actor = null
  var actor2Data: Actor = null

  /**
   * IF the event described takes place at the beginning of a document, it's assumed more relevant
   */
  var isRootEvent: Boolean = false
  /**
   * CAMEO event codes are defined in a three-level taxonomy.
   * For events at level three in the taxonomy, this yields its level two leaf root node
   */
  var eventCode = ""
  /**
   * CAMEO level one leaf code (Set to EventCode for level 2 events)
   */
  var baseEventCode = ""
  /**
   * CAMEO root level category this event falls under
   */
  var rootEventCode = ""
  /**
   * Value between -10 and +10 to rate the theoretical potential impact that type of event
   * will have on the stability of a country.
   */
  var goldsteinScale: Float = 0
  /**
   * Number of mentions across documents, or within this document
   * Can be used as measure of "importance"
   */
  var numMentions: Int = 0
  /**
   * The entire CAMEO event taxonomy is ultimately organized under four primary classifications:
   * 1=Verbal Cooperation,
   * 2=Material Cooperation,
   * 3=Verbal Conflict,
   * 4=Material Conflict.
   */
  var quadClass: Int = -1
  /**
   * This is the total number of information sources containing one or more
   * mentions of this event.
   * This can be used as a method of assessing the “importance” of an event:
   * the more discussion of that event, the more likely it is to be significant.
   */
  var numSources: Int = 0
  /**
   * This is the total number of source documents containing one or more
   * mentions of this event.
   * This can be used as a method of assessing the “importance” of an event:
   * the more discussion of that event, the more likely it is to be significant.
   */
  var numArticles: Int = 0
  /**
   * This is the average “tone” of all documents containing one or more mentions of this event.
   *
   * The score ranges from -100 (extremely negative) to +100 (extremely positive).
   * Common values range between -10 and +10, with 0 indicating neutral.
   * This can be used as a method of filtering the “context” of events as a subtle
   * measure of the importance of an event and as a proxy for the “impact” of that event
   */
  var avgTone: Float = 0f

  var actor1Geography: Geography = null
  var actor2Geography: Geography = null
  var actionGeography: Geography = null

  /**
   * Date the event was added to the database
   */
  var dateAdded: Int = 0

  /**
   * This field is only present in the daily event stream files beginning April 1, 2013
   * and lists the URL of the news article the event was found in.
   * If the event was found in an article from the BBC Monitoring service, this field will
   * contain “BBC Monitoring.”
   * If an event was mentioned in multiple articles, only one of the URLs is provided.
   *
   * NOTE: This field is not present in event files prior to April 1, 2013.
   */
  var sourceURL = ""

  var index: Int = 0

  def next: String = {
    val something: String = tsvRowData(index)
    index += 1
    something
  }

  def nextWindow(length: Int): Array[String] = {
    val something = tsvRowData.slice(index, index + length + 1) // it reads up to the element before that!
    index += length
    something
  }

  def parse(fromGoogle: Boolean): Row = {

    globalEventId = ModelUtils.getInt(next)
    day = next
    monthYear = next
    year = next
    fractionDate = ModelUtils.getFloat(next)

    actor1Data = new Actor(nextWindow(10))
    actor2Data = new Actor(nextWindow(10))

    isRootEvent = "1".equalsIgnoreCase(next)
    eventCode = next
    baseEventCode = next
    rootEventCode = next
    quadClass = ModelUtils.getInt(next)
    goldsteinScale = ModelUtils.getFloat(next)
    numMentions = ModelUtils.getInt(next)
    numSources = ModelUtils.getInt(next)
    numArticles = ModelUtils.getInt(next)
    avgTone = ModelUtils.getFloat(next)

    actor1Geography = new Geography(nextWindow(7))
    actor2Geography = new Geography(nextWindow(7))
    actionGeography = new Geography(nextWindow(7))

    dateAdded = ModelUtils.getInt(next)

    // Careful with this, ideally I should check the date but this seems faster
    if (tsvRowData.length == 58) {
      sourceURL = next
    }

    this
  }
}