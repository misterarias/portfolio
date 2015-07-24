package com.ariasfreire.gdelt.test

import com.ariasfreire.gdelt.processors.matchers.{SimpleMatcher, ConditionsMatcher}
import com.ariasfreire.gdelt.models.{Actor, Geography, Row}
import org.scalatest.FunSuite

/**
 * Tests for the ConditionsMatcher
 *
 * Created by juanito on 7/07/15.
 */
class SimpleMatcherTest extends FunSuite {
  test("Row passed cannot be null") {
    val nullMatcher = new SimpleMatcher()
    try {
      nullMatcher.checkConditions(null)
      fail("It should have crashed")
    } catch {
      case _: Throwable => // Nothing needed, it should have thrown an exception
    }
  }

  test("On null matcher, everything is always true") {
    val nullMatcher = new SimpleMatcher()
    val naiveRow = new Row(Array(""))
    assert(nullMatcher.checkConditions(naiveRow))
  }

  test("Filter by location works") {
    val fakeLocation = new Geography(Array("1", "Fake Country", "FK", "FKC", "0.0", "0.0", "FAKE"))
    val fakeActor = new Actor(Array("POL", "Fake man", "ESP", "", "", "", "", "", "", ""))

    val matcher = new SimpleMatcher(countryCodes=Seq("FK", "ESP"))
    val row = new Row(Array(""))
    assert(!matcher.checkConditions(row))

    // Both the action and the actor must be in the region
    row.actionGeography = fakeLocation
    assert(!matcher.checkConditions(row))

    row.actionGeography = null
    row.actor1Geography = fakeLocation
    assert(!matcher.checkConditions(row))

    row.actor1Geography = null
    row.actor2Geography = fakeLocation
    assert(!matcher.checkConditions(row))

    row.actor2Geography = null
    row.actor1Data = fakeActor
    assert(!matcher.checkConditions(row))

    row.actor1Data = null
    row.actor2Data = fakeActor
    assert(!matcher.checkConditions(row))

    row.actor2Geography = fakeLocation
    assert(matcher.checkConditions(row))
  }

  test("Filter by event works") {
    val eventCodes = Seq("100", "01")
    val matcher = new SimpleMatcher(eventCodes = eventCodes)

    val row = new Row(Array(""))
    assert(!matcher.checkConditions(row))

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

  test("Filter by actor works") {
    val fakeActor = new Actor(Array("POL", "Fake man", "ESP", "", "", "", "", "", "", ""))

    val matcher = new SimpleMatcher(actorCodes = Seq("POL", "JUD"))
    val row = new Row(Array(""))
    assert(!matcher.checkConditions(row))

    row.actor1Data = fakeActor
    assert(matcher.checkConditions(row))

    row.actor1Data = null
    row.actor2Data = fakeActor
    assert(matcher.checkConditions(row))
  }

}
