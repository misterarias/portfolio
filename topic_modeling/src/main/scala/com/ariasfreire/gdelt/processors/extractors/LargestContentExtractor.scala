package com.ariasfreire.gdelt.processors.extractors

import de.l3s.boilerpipe.extractors.CommonExtractors

/**
 * Using the Boilerplate Java library, we use a predefined extractor that seeks the container with the most text, to avoid
 * getting non-critical information in.
 *
 * Created by juanito on 26/06/15.
 */
trait LargestContentExtractor extends BaseExtractor {

  override def extract(sourceUrl: String): String = {

    val extractor = CommonExtractors.LARGEST_CONTENT_EXTRACTOR

    try {
      val content: String = getData(sourceUrl)
      extractor.getText(content)
    } catch {
      case npe: NullPointerException =>
        null
      case undefined: Throwable =>
        // Log this event somewhere
        val e = undefined.getStackTrace
        ""
    }
  }

}
