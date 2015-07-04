package com.ariasfreire.gdelt.web

import com.ariasfreire.gdelt.extractors.LargestContentExtractor
import com.ariasfreire.gdelt.models.{Actor, Geography, Row}
import org.apache.spark.{SparkConf, SparkContext}

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
    val conf = new SparkConf()
      .setAppName(s"Spark GDELT Project")
      //.setMaster("local[*]")
      // So that output directory gets overwritten
      .set("spark.hadoop.validateOutputSpecs", "false")
      // Supposedly up to 10x faster serialization
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    conf.registerKryoClasses(Array(classOf[ConditionsMatcher], classOf[Row], classOf[Actor], classOf[Geography]))
    val sc = new SparkContext(conf)

    // 1. check input parameters
    /** Name of the ES index where we'll store parsing info. It's also the key for finding files in HDFS */
    val indexName = "default"
    val scrapedDate = "20131201" // random
    val dirName = indexName + "/" + scrapedDate

    // 2. Check against db, to see if we've already parsed this data/country/topic combo
    val conditionsMatcher = new ConditionsMatcher(location = null, actor = null, eventCodes = null)

    // 3. Download data

    // 4. Parse downloaded data
    val downloadedFile = args(0)
    val gdeltDataFile = sc.textFile(downloadedFile)
    gdeltDataFile.map(data => {

      val info = data.split("\t")
      val row = new Row(info).parse()
      row
    }).filter(row => {
      conditionsMatcher.checkConditions(row) match {
        case true =>
          // stats for good case
          true
        case false =>
          // stats for bad case
          false
      }
    }).map(row => {
      val extractor = new LargestContentExtractor(row.sourceURL)
      (row.globalEventId, extractor.text)
    }).filter(x => {
      x._2.length > 0
    }).saveAsSequenceFile(dirName)
  }
}
