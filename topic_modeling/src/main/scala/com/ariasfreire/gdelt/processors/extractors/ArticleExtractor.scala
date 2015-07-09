package com.ariasfreire.gdelt.processors.extractors

import java.io.FileNotFoundException
import java.net.URL

import de.l3s.boilerpipe.extractors.CommonExtractors
import de.l3s.boilerpipe.sax.HtmlArticleExtractor

/**
 * Using the Boilerplate Java library, we use an article-optimized extractor to return the HTML text
 * relevant to the article
 *
 * Created by juanito on 26/06/15.
 */
trait ArticleExtractor extends BaseExtractor {

  override def extract(sourceUrl: String): String = {

    try {
      val extractor = CommonExtractors.ARTICLE_EXTRACTOR
      val htmlArticleExtractor = HtmlArticleExtractor.INSTANCE
      val url = new URL(sourceUrl)

      htmlArticleExtractor.process(extractor, url)
    } catch {
      case e: FileNotFoundException => {
        // XXX Log this?
        null
      }
      case undefined: Throwable =>
        // XXX Too noisy:undefined.printStackTrace()
        null
    }
  }
}