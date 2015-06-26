package com.ariasfreire.gdelt.web

import com.ariasfreire.gdelt.extractors.LargestContentExtractor
import org.apache.spark.{SparkContext, SparkConf}

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
    println(gdeltDataFile.collect().toString)
    val rowsRDD = RowParser.parse(gdeltDataFile)

    // 5. insert stats on db

    // 6. extract text from parsed URLs and store in HDFS
    rowsRDD.map( row =>{
      val extractor = new LargestContentExtractor(row.sourceURL)
      println(extractor.text)
    }).collect()
  }
}
