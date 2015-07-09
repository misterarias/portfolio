package com.ariasfreire.gdelt.processors.parsers

import breeze.io.CSVReader
import com.ariasfreire.gdelt.models.Row

/**
 * Created by juanito on 10/07/15.
 */
trait BigQueryParser extends GdeltRowParser {
  override val fromGoogle = true

  override def parse(inputRow: String): Row = {
    val reader = CSVReader.parse(inputRow)
    val item: IndexedSeq[String] = reader.head
    val info: Array[String] = item.toArray

    new Row(info).parse(fromGoogle)
  }
}
