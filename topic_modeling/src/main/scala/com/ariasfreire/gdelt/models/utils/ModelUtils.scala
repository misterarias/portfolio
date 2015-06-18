package com.ariasfreire.gdelt.models.utils

/**
 * Created by juanito on 19/06/15.
 */
object ModelUtils {

  def getInt(s: String): Int = s.length match {
    case 0 => 0
    case _ => s.toInt
  }

  def getFloat(s: String): Float = s.length match {
    case 0 => 0f
    case _ => s.toFloat
  }
}
