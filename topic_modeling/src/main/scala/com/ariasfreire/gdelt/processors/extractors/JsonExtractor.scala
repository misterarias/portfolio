package com.ariasfreire.gdelt.processors.extractors

import org.jsoup.Jsoup

/**
 * Use JSoup library to extract text.
 * By default, this extractor does not care about content, just strips tags
 *
 * Created by juanito on 26/06/15.
 */
trait JsonExtractor extends BaseExtractor {

  override def extract(sourceUrl: String): String = {
    val doc = Jsoup.connect(sourceUrl).get
    val body = doc.select("body").text

    body
  }
}
