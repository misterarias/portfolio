package com.ariasfreire.gdelt.utils

import com.ariasfreire.gdelt.processors.extractors.LargestContentExtractor
import com.ariasfreire.gdelt.processors.parsers.GdeltRowParser
import org.apache.spark.SparkConf

/**
 * Created by juanito on 11/07/15.
 */
object ContextUtils {

  /**
   * After calling this, and before creating a SparkContext, the applications must set the Kryo classes
   * @return
   */
  def conf: SparkConf = {
    val conf = new SparkConf()
      .setAppName(s"Spark GDELT Project")
      .setMaster("local[*]")
      // So that output directory gets overwritten
      .set("spark.hadoop.validateOutputSpecs", "false")
      // Supposedly up to 10x faster serialization (Note: Holy **** it's fast)
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    conf.registerKryoClasses(Array(classOf[GdeltRowParser], classOf[LargestContentExtractor]))

    conf
  }
}
