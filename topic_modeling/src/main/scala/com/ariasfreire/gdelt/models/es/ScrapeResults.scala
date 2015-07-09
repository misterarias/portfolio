package com.ariasfreire.gdelt.models.es

import java.time.LocalDate

import com.ariasfreire.gdelt.models.lda.TopicData

/**
 * This object will model the scrape results, to be shared between steps of a topic modeling session
 * Created by juanito on 12/07/15.
 */
class ScrapeResults(
                     val name: String,
                     val date: String = LocalDate.now().toString,
                     var okRows: Long = 0,
                     var totalRows: Long = 0,
                     var invalidUrls: Long = 0,
                     var failedRows: Long = 0,
                     var lowContentUrls: Long = 0,
                     var duplicatedRows: Long = 0,
                     var topicData: Array[Array[TopicData]] = Array()) {
  def summary(): Unit = {
    printf("Finished parsing file for date %s\n" +
      "Matched rows:\t\t\t%d (%.2f %%)\n" +
      "Unmatched rows:\t\t\t%d (%.2f %%)\n" +
      "Duplicated URLs:\t\t\t%d (%.2f %%)\n" +
      "Low content URLs:\t\t\t%d (%.2f %%)\n" +
      "404 errors:\t\t\t%d (%.2f %%)\n",
      date,
      okRows, 100.0 * okRows / totalRows,
      failedRows, 100.0 * failedRows / totalRows,
      duplicatedRows, 100.0 * duplicatedRows / totalRows,
      lowContentUrls, 100.0 * lowContentUrls / totalRows,
      invalidUrls, 100.0 * invalidUrls / totalRows
    )
    val topicNumber = topicData.length
    println(s"$topicNumber topics:")
    topicData.zipWithIndex.foreach { case (topic: Array[TopicData], i) =>
      println(s"TOPIC $i")
      topic.foreach { topic: TopicData =>
        println(s"$topic.term\t$topic.weight")
      }
      println()
    }
  }

  def toJson: String = {
    var index: Int = 0
    val topicString = topicData map { topics=>
      index += 1
      s"""{ "name": "Topic $index", "topics": [""" +
        topics.map {
          _.toJson
        }.mkString(",") + "]}"
    } mkString(",")
    s"""{
      "name": "${name}", "ok": ${okRows}, "failed":${failedRows},
      "invalid": ${invalidUrls},
      "total": ${totalRows}, "lowContent": ${lowContentUrls},
      "topics": [ ${topicString} ]
      }"""
  }
}