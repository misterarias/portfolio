package com.ariasfreire.gdelt.processors.exporters

import com.ariasfreire.gdelt.utils.ContextUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FSDataOutputStream, FSDataInputStream, Path, FileSystem}
import org.apache.spark.rdd.RDD

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Store given RDD as Hadoop Text Files, to be used as input for Mallet's Text2Vector
 * Created by juanito on 13/07/15.
 */
trait MalletExporter {
  /**
   * Function that specified how to persist a RDD of tuples (ID, text)
   * @param texts RDD of DAY, Text pairs
   * @param path  output path to write to
   */
  def export(texts: RDD[(String, String)], path: String): Unit = {
    val fs: FileSystem = FileSystem.get(new Configuration)
    texts.collect().foreach { (item: (String, String)) =>
      Future {
        val finalPath = new Path(path + "/" + item._1 + ".txt")
        val file: FSDataOutputStream = fs.create(finalPath)
        file.writeBytes(item._2)
        file.close()
      }
    }
  }
}
