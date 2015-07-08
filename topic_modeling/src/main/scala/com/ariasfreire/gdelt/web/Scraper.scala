package com.ariasfreire.gdelt.web

import com.ariasfreire.gdelt.extractors.LargestContentExtractor
import com.ariasfreire.gdelt.matchers.{ConditionsMatcher, SimpleMatcher}
import com.ariasfreire.gdelt.models.{Actor, Geography, Row}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Given a date, or date range, scrape the gdelt archive for files that have
 * yet to be parsed and add them to our db
 *
 * Created by juanito on 23/06/15.
 */
object Scraper {

  def getConditionsMatcher = {

    val relevantEventCodes = Seq(
      "091", // Investigate crime, corruption
      "1041", // Demand leadership change
      "1231", // Reject request to change leadership -> might include presidential dimissions
      "1121", // Accuse of crime, corruption
      "1322", // Threaten to ban political parties or politicians
      "1722", // Ban political parties or politicians - idem
      "015", // Acknowledge or claim responsibility
      "016", // Deny responsibility
      "061" // Cooperate economically
    )
    val relevantLocation = new Geography()
    relevantLocation.geoCountryCode = "SP"

    val relevantActor = new Actor()
    relevantActor.countryCode = "ESP"

    new ConditionsMatcher(
      actor = relevantActor,
      location = relevantLocation,
      eventCodes = relevantEventCodes)
  }

  def getSimpleMatcher = {

    val relevantEventCodes = Seq(
      "091", // Investigate crime, corruption
      "1041", // Demand leadership change
      "1231", // Reject request to change leadership -> might include presidential dimissions
      "1121", // Accuse of crime, corruption
      "1322", // Threaten to ban political parties or politicians
      "1722", // Ban political parties or politicians - idem
      "015", // Acknowledge or claim responsibility
      "016", // Deny responsibility
      "061" // Cooperate economically
    )
 //   val relevantCountryCodes = Seq("ESP", "SP")
    val relevantCountryCodes = Seq("USA", "US")
    val relevantActorCodes = Seq("GOV", "PTY", "JUD", "OPP")
    new SimpleMatcher(
      countryCodes = relevantCountryCodes,
      actorCodes = relevantActorCodes,
      eventCodes = relevantEventCodes)
  }

  def main(args: Array[String]) {

    // 0. setup a common context
    // get valid context to read given file
    val conf = new SparkConf()
      .setAppName(s"Spark GDELT Project")
      .setMaster("local[*]")
      // So that output directory gets overwritten
      .set("spark.hadoop.validateOutputSpecs", "false")
      // Supposedly up to 10x faster serialization (Note: Holy **** it's fast)
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    conf.registerKryoClasses(Array(classOf[ConditionsMatcher], classOf[Row], classOf[Actor], classOf[Geography]))
    val sc = new SparkContext(conf)

    // 1. check input parameters
    /** Name of the ES index where we'll store parsing info. It's also the key for finding files in HDFS */
    val indexName = "allfilters"
    val scrapedDate = "20131201" // random
    val dirName = indexName + "/" + scrapedDate

    //val matcher = getConditionsMatcher
    val matcher = getSimpleMatcher

    // 2. Check against db, to see if we've already parsed this data/country/topic combo

    // 3. Download data

    // 4. Parse downloaded data
    val downloadedFile = args(0)
    val okRowCount = sc.accumulator(0)
    val failedRowCount = sc.accumulator(0)
    val invalidUrlCount = sc.accumulator(0)

    val gdeltDataFile: RDD[String] = sc.textFile(downloadedFile, 10)
    val matchesRDD: RDD[Row] = gdeltDataFile.map(data => {

      val info = data.split("\t")
      val row = new Row(info).parse()
      row
    }).filter(row => {
      matcher.checkConditions(row) match {
        case true =>
          // stats for good case
          okRowCount += 1
          true
        case false =>
          // stats for bad case
          failedRowCount += 1
          false
      }
    })

    // Filter duplicated URLs, there are many
    val uniqueRowsRDD: RDD[(Int, Row)] =
      matchesRDD.map(row => (row.sourceURL.hashCode, row))
        .reduceByKey((a, b) => a)

    uniqueRowsRDD.map((item: (Int, Row)) => {
      val row = item._2
      val extractor = new LargestContentExtractor(row.sourceURL)
      val urlText = extractor.text
      (row.globalEventId, urlText)
    }).filter(x => {
      x._2.length > 0 match {
        case false =>
          okRowCount += -1
          invalidUrlCount += 1
          false
        case _ => true
      }
    }).saveAsSequenceFile(dirName)

    val totalCount = okRowCount.value + failedRowCount.value + invalidUrlCount.value
    printf("Finished parsing file for date %s\n" +
      "Successful rows:\t%d (%.2f %%)\n" +
      "Failed rows:\t%d (%.2f %%)\n" +
      "404 errors:\t%d (%.2f %%)\n",
      scrapedDate,
      okRowCount.value, 100.0 * okRowCount.value / totalCount,
      failedRowCount.value, 100.0 * failedRowCount.value / totalCount,
      invalidUrlCount.value, 100.0 * invalidUrlCount.value / totalCount
    )
  }
}
