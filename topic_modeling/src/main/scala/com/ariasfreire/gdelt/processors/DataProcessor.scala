package com.ariasfreire.gdelt.processors

import com.ariasfreire.gdelt.models.es.ScrapeResults
import com.ariasfreire.gdelt.models.{Actor, Geography, Row}
import com.ariasfreire.gdelt.processors.matchers.{NaiveMatcher, SimpleMatcher}
import com.ariasfreire.gdelt.utils.ContextUtils
import org.apache.hadoop.fs.Path
import org.apache.spark.rdd.RDD
import org.apache.spark.{Logging, SparkContext}


/**
 * Given a date, or date range, scrape the gdelt archive for files that have
 * yet to be parsed and add them to our db
 *
 * Created by juanito on 23/06/15.
 */
abstract class DataProcessor extends Serializable with Logging {
  /**
   * Method used to parse input rows into data
   * @param inputRow a Row from a CSV File
   * @return
   */
  def parse(inputRow: String): Row

  /**
   * Used to extract text from the URL pointed by sourceUrl
   * @param sourceUrl URL
   * @return
   */
  def extract(sourceUrl: String): String

  /**
   * Function that specified how to persist a RDD of tuples (ID, text)
   * @param texts RDD of ID to parsed text pairs
   * @param path  output path to write to
   */
  def export(texts: RDD[(String, String)], path: String): Unit

  /**
   * Swappable row matcher
   */
  var matcher = new NaiveMatcher()

  def process(outputDir: String, scrapedFileName: String): ScrapeResults = {

    // If output dir exists and we cannot overwrite, return !
    val outputModel = new ScrapeResults(outputDir)
    if (ContextUtils.hasFiles(outputDir) && !ContextUtils.overwrite) {
      logError("Output dir has files AND overwrite disabled\n")
      return outputModel
    }

    val conf = ContextUtils.conf
    conf.registerKryoClasses(
      Array(classOf[SimpleMatcher],
        classOf[Row], classOf[Actor], classOf[Geography]))
    val sc = new SparkContext(conf)

    // Prepare some flags to update output model
    val okRowCount = sc.accumulator(0)
    val failedRowCount = sc.accumulator(0)
    val duplicatedRowCount = sc.accumulator(0)
    val invalidUrlCount = sc.accumulator(0)
    val tooFewContent = sc.accumulator(0)

    // Parse input files with mixed in parser, according to a set of conditions
    val gdeltDataFile: RDD[String] = sc.textFile(scrapedFileName)
    val totalRows = gdeltDataFile.count()

    val matchesRDD: RDD[Row] =
      gdeltDataFile.map(parse)
        .filter(row => {
        matcher.checkConditions(row) match {
          case true =>
            okRowCount += 1
            true
          case false =>
            // stats for bad case
            failedRowCount += 1
            false
        }
      })

    // Filter duplicated URLs, there are many
    val uniqueRowsRDD: Array[(Int, Row)] =
      matchesRDD.map(row => (row.sourceURL.hashCode(), row))
        .reduceByKey((a, b) => {
        okRowCount += -1
        duplicatedRowCount += 1
        a
      }).collect()

    // Extract text from URLs, removing those that 404'd or returned no data
    val numPartitions = Math.min(100, totalRows / 100).toInt
    val dayToTextsRDD: RDD[(String, String)] = sc.parallelize(uniqueRowsRDD, numPartitions)
      .map((item: (Int, Row)) => {
      val row = item._2
      val urlText = extract(row.sourceURL)
      (row.day, urlText)
    }).filter {
      case x if x._2 == null =>
        okRowCount += -1
        invalidUrlCount += 1
        false

      /** XXX Harcoded value here! **/
      case x if x._2.length < 20 =>
        okRowCount += -1
        tooFewContent += 1
        false
      case _ =>
        okRowCount += 1
        true
    } reduceByKey ((textA, textB) => textA + Path.SEPARATOR + textB)

    export(dayToTextsRDD, outputDir)

    outputModel.okRows = okRowCount.value
    outputModel.failedRows = failedRowCount.value
    outputModel.duplicatedRows = duplicatedRowCount.value
    outputModel.invalidUrls = invalidUrlCount.value
    outputModel.totalRows = totalRows
    outputModel.lowContentUrls = tooFewContent.value

    sc.stop()
    outputModel
  }
}
