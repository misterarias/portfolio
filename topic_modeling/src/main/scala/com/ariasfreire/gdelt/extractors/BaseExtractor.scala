package com.ariasfreire.gdelt.extractors

/**
 * Create a common interface for all the extractors to be defined
 *
 * Created by juanito on 26/06/15.
 */
abstract class BaseExtractor(
                              val sourceUrl: String) {
  /**
   * Applies the appropriate logic to extract text from this URL
   * @return  String
   */
  def text: String
}
