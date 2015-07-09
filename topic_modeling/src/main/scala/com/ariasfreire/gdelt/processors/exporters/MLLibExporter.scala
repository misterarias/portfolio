package com.ariasfreire.gdelt.processors.exporters

import org.apache.spark.rdd.RDD

/**
 * Store given RDD as a Hadoop Sequence File
 * Created by juanito on 13/07/15.
 */
trait MLLibExporter {
  /**
   * Function that specified how to persist a RDD of tuples (ID, text)
   * @param texts RDD of ID to parsed text pairs
   * @param path  output path to write to
   */
  def export(texts: RDD[(String, String)], path: String): Unit = {
    texts.saveAsSequenceFile(path)
  }
}
