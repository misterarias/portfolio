package com.ariasfreire.gdelt.extractors

import java.net.URL

import de.l3s.boilerpipe.extractors.CommonExtractors
import de.l3s.boilerpipe.sax.HtmlArticleExtractor

/**
 * Using the Boilerplate Java library, we use an article-optimized extractor to return the HTML text
 * relevant to the article
 *
 * Created by juanito on 26/06/15.
 */
class ArticleExtractor(sourceUrl: String) extends BaseExtractor(sourceUrl) {

  override def text: String = {
    val extractor = CommonExtractors.ARTICLE_EXTRACTOR
    val htmlArticleExtractor = HtmlArticleExtractor.INSTANCE;
    val url = new URL(sourceUrl)

    htmlArticleExtractor.process(extractor, url)
  }
}