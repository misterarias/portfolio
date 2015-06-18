package com.ariasfreire.gdelt.web

import com.ariasfreire.gdelt.models.Row
import org.apache.spark.rdd.RDD

/**
 * Parse a row from a GDELT entry
 *
 * Created by juanito on 18/06/15.
 */
object RowParser {

  def parse(gdeltDataFile: RDD[String]): RDD[Row] = {

    gdeltDataFile.map(data => {

      val info = data.split("\t")
      val row = new Row(info).parse()

      row
    })
  }
}
