package com.ariasfreire.gdelt.extractors

import java.net.URL

import de.l3s.boilerpipe.extractors.CommonExtractors

/**
 * Using the Boilerplate Java library, we use a predefined extractor that seeks the container with the most text, to avoid
 * getting non-critical information in.
 *
 * Created by juanito on 26/06/15.
 */
class LargestContentExtractor(sourceUrl: String) extends BaseExtractor(sourceUrl) {

  override def text: String = {
    val extractor = CommonExtractors.LARGEST_CONTENT_EXTRACTOR
    try {
      extractor.getText(new URL(sourceUrl))
    } catch {
      case _: Throwable =>
        // Log this event somewhere
        ""
    }
  }
}
