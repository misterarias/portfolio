package com.ariasfreire.gdelt.utils

import com.ariasfreire.gdelt.processors.extractors.LargestContentExtractor
import com.ariasfreire.gdelt.processors.parsers.GdeltRowParser
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
   * After calling this, and before creating a SparkContext, the applications must set the Kryo classes
   * @return
   */
  def conf: SparkConf = {
    val conf = new SparkConf()
      .setAppName(s"Spark GDELT Project")
      .setMaster("local[*]")
      // Supposedly up to 10x faster serialization (Note: Holy **** it's fast)
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    // So that output directory gets overwritten
    if (overwrite)
      conf.set("spark.hadoop.validateOutputSpecs", "false")

    conf.registerKryoClasses(Array(classOf[GdeltRowParser], classOf[LargestContentExtractor]))

    conf
  }
}
