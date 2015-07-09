package com.ariasfreire.gdelt.test

import com.ariasfreire.gdelt.processors.matchers.ConditionsMatcher
import com.ariasfreire.gdelt.models.{Actor, Geography, Row}
import org.scalatest.FunSuite

/**
 * Tests for the ConditionsMatcher
 *
 * Created by juanito on 7/07/15.
 */
class ConditionsMatcherTest extends FunSuite {
  test("Row passed cannot be null") {
    val nullMatcher = new ConditionsMatcher()
    try {
      nullMatcher.checkConditions(null)
      fail("It should have crashed")
    } catch {
      case _: Throwable => // Nothing needed, it should have thrown an exception
    }
  }

  test("On null matcher, everything is always true") {
    val nullMatcher = new ConditionsMatcher()
    val naiveRow = new Row(Array(""))
    assert(nullMatcher.checkConditions(naiveRow))
  }

  test("Filter by location works") {
    val fakeLocation = new Geography(Array("1", "Fake Country", "FK", "FKC", "0.0", "0.0", "FAKE"))
    val conditionLocation = new Geography()
    conditionLocation.geoType = 1
    conditionLocation.geoCountryCode = "FK"

    val matcher = new ConditionsMatcher(location = conditionLocation)
    val row = new Row(Array(""))
    row.actionGeography = fakeLocation
    assert(matcher.checkConditions(row))

    row.actionGeography = null
    row.actor1Geography = fakeLocation
    assert(matcher.checkConditions(row))

    row.actor1Geography = null
    row.actor2Geography = fakeLocation
    assert(matcher.checkConditions(row))
  }

  test("Filter by event works") {
    val eventCodes = Seq("100", "01")
    val matcher = new ConditionsMatcher(eventCodes = eventCodes)

    val row = new Row(Array(""))
    val goodCode = "100"
    row.rootEventCode = goodCode
    assert(matcher.checkConditions(row))

    val badCode = "123"
    row.rootEventCode = badCode
    assert(!matcher.checkConditions(row))
    row.rootEventCode = null

    row.baseEventCode = goodCode
    assert(matcher.checkConditions(row))
    row.baseEventCode = badCode
    assert(!matcher.checkConditions(row))
    row.baseEventCode = null

    row.eventCode = goodCode
    assert(matcher.checkConditions(row))
    row.eventCode = badCode
    assert(!matcher.checkConditions(row))
  }

  test("Filter by actor works - just by country") {
    val fakeActor = new Actor(Array("POL", "Fake man", "ESP", "", "", "", "", "", "", ""))
    val conditionActor = new Actor()
    conditionActor.countryCode = "ESP"

    val matcher = new ConditionsMatcher(actor = conditionActor)
    val row = new Row(Array(""))
    row.actor1Data = fakeActor
    assert(matcher.checkConditions(row))

    row.actor1Data = null
    row.actor2Data = fakeActor
    assert(matcher.checkConditions(row))
  }

  test("Multiple condition matches") {
    val eventCodes = Seq("100", "01")
    val fakeActor = new Actor(Array("POL", "Fake man", "ESP", "", "", "", "", "", "", ""))
    val fakeLocation = new Geography(Array("1", "Fake Country", "FK", "FKC", "0.0", "0.0", "FAKE"))

    val conditionLocation = new Geography()
    conditionLocation.geoType = 1
    conditionLocation.geoCountryCode = "FK"

    val conditionActor = new Actor()
    conditionActor.countryCode = "ESP"

    val matcher = new ConditionsMatcher(
      actor = conditionActor,
      location = conditionLocation,
      eventCodes = eventCodes)

    val row = new Row(Array(""))

    // All good
    val goodCode = "100"
    row.rootEventCode = goodCode
    assert(!matcher.checkConditions(row))
    row.actor1Data = fakeActor
    assert(!matcher.checkConditions(row))
    row.actionGeography = fakeLocation
    assert(matcher.checkConditions(row))

    // BAd event code
    val badCode = "123"
    row.rootEventCode = badCode
    assert(!matcher.checkConditions(row))

    // Bad actor


    // Bad location
  }

}