package com.ariasfreire.gdelt

import com.ariasfreire.gdelt.mllib.MLlibLDA
import com.ariasfreire.gdelt.models.es.ScrapeResults
import com.ariasfreire.gdelt.models.lda.TopicData
import com.ariasfreire.gdelt.processors.Processor
import com.ariasfreire.gdelt.processors.exporters.MalletExporter
import com.ariasfreire.gdelt.processors.extractors.LargestContentExtractor
import com.ariasfreire.gdelt.processors.matchers.SimpleMatcher
import com.ariasfreire.gdelt.processors.parsers.BigQueryParser
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.source.Indexable

/**
 * Created by juanito on 10/07/15.
 */
object Main {


  def main(args: Array[String]) {

    if (args.length != 3)
      throw new RuntimeException("Expected params: <csv file> <stopwords file> <output dir>")

    implicit object ScrapeResultsIndexable extends Indexable[ScrapeResults] {
      override def json(t: ScrapeResults): String =
        t.toJson.stripMargin
    }

    val csvFile = args(0)
    val stopWordsFile = args(1)
    val outputDir = args(2)
    val client = ElasticClient.remote("127.0.0.1", 9300)

    // Configure a Processor for my needs
    val parser = new Processor with BigQueryParser with LargestContentExtractor with MalletExporter
    parser.matcher = SimpleMatcher.get

    val scrapeResults: ScrapeResults = parser.process(outputDir, csvFile)
    if (scrapeResults.totalRows == 0) {
      printf("Scraping failed, no data!")
      return
    }

    // Show parsing metrics
    scrapeResults.summary()

    // Run Distributed LDA
    val topics: Array[Array[TopicData]] = new MLlibLDA(
      input = Seq(outputDir),
      stopWordsFile = stopWordsFile
    ).run
    scrapeResults.topicData = topics

    // Store corpus-inferred topics into ES
    client.execute {
      index into "results" / "topics" source scrapeResults
    }

    printf("Topic Modelling finished")
  }
}