package com.ariasfreire.gdelt.extractors

import org.jsoup.Jsoup

/**
 * Use JSoup library to extract text.
 * By default, this extractor does not care about content, just strips tags
 *
 * Created by juanito on 26/06/15.
 */
class JsonExtractor(sourceUrl: String) extends BaseExtractor(sourceUrl) {

  override def text(): String = {
    val doc = Jsoup.connect(sourceUrl).get
    val body = doc.select("body").text
    //println(s"Text for URL ${sourceUrl}:\n${body}")

    body
  }
}
