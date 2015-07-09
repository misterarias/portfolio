package com.ariasfreire.gdelt.utils

import com.ariasfreire.gdelt.models.{Geography, Actor, Row}
import com.ariasfreire.gdelt.processors.matchers.SimpleMatcher
import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
 * Convert a series of Sequential Files into a collection of files, suitable for Mallet
 * Created by juanito on 12/07/15.
 */
object SeqToMallet {

  def main(args: Array[String]) {
    val conf = ContextUtils.conf
    val sc = new SparkContext(conf)

    if (args.length != 2) {
      throw new RuntimeException("Parameters needed: <in dir> <out dir>")
    }
    val inDir = args(0)
    val outDir = args(1)

    // Get all the files in the input directory as Sequence Files and save them in outdir
    sc.sequenceFile[Int, String](inDir)
      .map(_._2).saveAsTextFile(outDir)

    sc.stop()
  }
}
