package com.ariasfreire.gdelt.utils

/**
 * Created by juanito on 8/07/15.
 */
object StringUtils {
  /**
   * Returns true if both strings are equal, ignoring case, but only if they have
   * something to compare
   * @param s1
   * @param s2
   * @return
   */
  def gdeltCompare(s1: String, s2: String): Boolean = {

    if (s1 != null && !"".equals(s1) && s2 != null && !"".equals(s2)) {
      return s1.equalsIgnoreCase(s2)
    }
    false

  }
}
