package com.ariasfreire.gdelt.processors.extractors

import java.io.StringWriter
import java.net.{URL, URLConnection}
import java.util.concurrent.TimeUnit

import org.apache.commons.io.IOUtils

/**
 * Create a common interface for all the extractors to be defined
 *
 * Created by juanito on 26/06/15.
 */
trait BaseExtractor extends Serializable {
  /**
   * Applies the appropriate logic to extract text from this URL
   * @return  String
   */
  def extract(sourceUrl: String): String

  def getData(sourceUrl: String): String = {
    val connection: URLConnection = new URL(sourceUrl).openConnection()
    connection.setReadTimeout(TimeUnit.MILLISECONDS.convert(2, TimeUnit.SECONDS).toInt)
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:38.0) Gecko/20100101 Firefox/38.0")
    connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")

    val writer = new StringWriter()
    IOUtils.copy(connection.getInputStream, writer)

    writer.toString
  }
}
