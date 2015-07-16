package com.ariasfreire.gdelt.utils

import com.ariasfreire.gdelt.processors.extractors.LargestContentExtractor
import com.ariasfreire.gdelt.processors.parsers.GdeltRowParser
import com.sksamuel.elastic4s.ElasticClient
import org.apache.spark.SparkConf

/**
 * Created by juanito on 11/07/15.
 */
object ContextUtils {

  /**
   * If set to true, override output directory
   */
  var overwrite: Boolean = false

  /**
   * Set master type
   */
  var masterType: String = "local[*]"

  private val myConf = new SparkConf()
    .setAppName(s"Spark GDELT Project")
    // Supposedly up to 10x faster serialization (Note: Holy **** it's fast)
    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

  /**
   * After calling this, and before creating a SparkContext, the applications must set the Kryo classes
   * @return
   */
  def conf: SparkConf = {
    val conf = myConf
      .setMaster(masterType)

    // So that output directory gets overwritten
    if (overwrite)
      conf.set("spark.hadoop.validateOutputSpecs", "false")

    conf.registerKryoClasses(Array(classOf[GdeltRowParser], classOf[LargestContentExtractor]))

    conf
  }

  /**
   * Read from configuration the URL and port of the Elastic Search master
   *
   * @return a valid elastic search client
   */
  val esClient: ElasticClient = ElasticClient.remote("127.0.0.1", 9300)

}
