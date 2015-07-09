package com.ariasfreire.gdelt.models.utils

/**
 * Created by juanito on 19/06/15.
 */
object ModelUtils {

  def getInt(s: String): Int = s.length match {
    case _ =>
      try {
        s.toInt
      } catch {
        case x: NumberFormatException =>
         // printf("Trying to decode %s as an Integer\n", s) // Log this....
          0
        case undefined: Throwable =>
          throw undefined
      }
  }

  def getFloat(s: String): Float = s.length match {
    case 0 => 0f
    case _ =>
      try {
        s.toFloat
      } catch {
        case x: NumberFormatException =>
        //  printf("Trying to decode %s as a Float\n", s) // Log this....
          0f
        case undefined: Throwable =>
          throw undefined
      }
  }
}
