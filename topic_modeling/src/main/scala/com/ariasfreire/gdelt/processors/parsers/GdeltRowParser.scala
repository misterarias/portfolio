package com.ariasfreire.gdelt.processors.parsers

import com.ariasfreire.gdelt.models.Row

/**
 * Created by juanito on 10/07/15.
 */
trait GdeltRowParser extends Serializable {
  val fromGoogle = false

  def parse(inputRow: String): Row = {
    // Try common separators
    val info = inputRow.split("\t")
    if (info.length < 2) {
      throw new RuntimeException("Unexpected separator for input file")
    }

    new Row(info).parse(fromGoogle)
  }
}
