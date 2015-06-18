package com.ariasfreire.gdelt.web

import org.apache.spark.{SparkContext, SparkConf}
import org.jsoup.Jsoup
import org.jsoup.nodes._

/**
 * Given a date, or date range, scrape the gdelt archive for files that have
 * yet to be parsed and add them to our db
 *
 * Created by juanito on 23/06/15.
 */
object Scraper {

  def main(args: Array[String]) {

    // 0. setup a common context
    // get valid context to read given file
    val conf = new SparkConf().setAppName(s"Spark GDELT Project").setMaster("local[*]")
    val sc = new SparkContext(conf)

    // 1. check input parameters

    // 2. Check against db, to see if we've already parsed this data/country/topic combo

    // 3. Download data

    // 4. Parse downloaded data
    val downloadedFile = args(0)
    val gdeltDataFile = sc.textFile(downloadedFile, 4)
    val rowsRDD = RowParser.parse(gdeltDataFile)

    // 5. insert stats on db

    // 6. extract text from parsed URLs and store in HDFS
    rowsRDD.map( row =>{
      val doc = Jsoup.connect(row.sourceURL).get
      val body = doc.select("body").text
      println(s"Text for document ${row.globalEventId} and URL ${row.sourceURL}:\n${body}")
    })

    // 7. Configure Topic Modeling

    // 8. Run algorithm

    // 9. Store results in db
  }
}
