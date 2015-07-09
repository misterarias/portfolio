package com.ariasfreire.gdelt.processors.parsers

import com.ariasfreire.gdelt.models.Row

/**
 * This simple parser only reads files with one element: SourceURL
 *
 * Created by juanito on 12/07/15.
 */
trait BigQueryExportedUrlParser extends BigQueryParser {
  override def parse(inputRow: String): Row = {
    val row = new Row(Array(""))
    row.sourceURL = inputRow
    row
  }
}
