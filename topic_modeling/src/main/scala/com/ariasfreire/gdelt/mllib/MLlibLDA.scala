package com.ariasfreire.gdelt.mllib

import com.ariasfreire.gdelt.models.lda.{TopicTermModel, TopicTermsDataModel}
import com.ariasfreire.gdelt.utils.{ContextUtils, SimpleTokenizer}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.clustering._
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.rdd.RDD
import org.apache.spark.{Logging, SparkContext}

import scala.collection.mutable

/**
 * Runs the LDA algorithm that comes with Spark 1.4 MLLib
 *
 * Created by juanito on 11/07/15.
 */
class MLlibLDA(
                inputDir: String,
                k: Int = 30,
                maxIterations: Int = 20,
                maxTermsPerTopic: Int = 15,
                docConcentration: Double = -1,
                topicConcentration: Double = -1,
                vocabSize: Int = -1,
                stopWordsFile: String = "",
                algorithm: String = "em",
                checkpointDir: Option[String] = None,
                checkpointInterval: Int = 10) extends Serializable with Logging {


  // XXX Make sure this is just called once
  val sc: SparkContext = {
    val conf = ContextUtils.conf
    new SparkContext(conf)
  }

  def run: Array[TopicTermsDataModel] = {

    Logger.getRootLogger.setLevel(Level.INFO)

    // Load documents, and prepare them for LDA.
    val (corpus, vocabArray, actualCorpusSize) = preProcess
    corpus.cache()

    // Run LDA.
    val lda = new LDA()

    val optimizer = algorithm.toLowerCase match {
      case "em" => new EMLDAOptimizer
      // add (1.0 / actualCorpusSize) to MiniBatchFraction be more robust on tiny datasets.
      case "online" => new OnlineLDAOptimizer().setMiniBatchFraction(0.05 + 1.0 / actualCorpusSize)
      case _ => throw new IllegalArgumentException(
        s"Only em, online are supported but got $algorithm.")
    }

    lda.setOptimizer(optimizer)
      .setK(k)
      .setMaxIterations(maxIterations)
      .setDocConcentration(docConcentration)
      .setTopicConcentration(topicConcentration)
      .setCheckpointInterval(checkpointInterval)
    if (checkpointDir.nonEmpty) {
      sc.setCheckpointDir(checkpointDir.get)
    }
    val startTime = System.nanoTime()
    val ldaModel: LDAModel = lda.run(corpus)
    val elapsed = (System.nanoTime() - startTime) / 1e9

    logInfo(s"Finished training LDA model.  Summary:\n" +
      s"\t Training time: $elapsed sec")

    ldaModel match {
      case distLDAModel: DistributedLDAModel =>
        val avgLogLikelihood = distLDAModel.logLikelihood / actualCorpusSize.toDouble
        logInfo(s"\t Training data average log likelihood: $avgLogLikelihood")
      case _ =>
    }

    val topicIndices: Array[(Array[Int], Array[Double])] = ldaModel.describeTopics(maxTermsPerTopic)
    val topicModelArray: Array[TopicTermsDataModel] =
      topicIndices.zipWithIndex.map { case ((terms, termWeights), index: Int) =>
        val topicData: Array[TopicTermModel] = terms.zip(termWeights).map { case (term, weight) =>
          new TopicTermModel(vocabArray(term.toInt), weight)
        }
        new TopicTermsDataModel(inputDir, s"Topic $index", topicData)
      }

    sc.stop()
    topicModelArray
  }

  /**
   * Load documents, tokenize them, create vocabulary, and prepare documents as term count vectors.
   * @return (corpus, vocabulary as array, total token count in corpus)
   */
  def preProcess: (RDD[(Long, Vector)], Array[String], Long) = {
    val preprocessStart = System.nanoTime()

    val textRDD: RDD[String] = sc.textFile(inputDir)

    // Split text into words
    val tokenizer = new SimpleTokenizer(sc, stopWordsFile)
    val tokenized: RDD[(Long, IndexedSeq[String])] = textRDD.zipWithIndex().map {
      case (text, id) =>
        id -> tokenizer.getWords(text)
    }
    tokenized.cache()

    // Counts words: RDD[(word, wordCount)]
    val wordCounts: RDD[(String, Long)] = tokenized
      .flatMap { case (_, tokens) => tokens.map(_ -> 1L) }
      .reduceByKey(_ + _)
    wordCounts.cache()
    val fullVocabSize = wordCounts.count()

    val (vocab: Map[String, Int], selectedTokenCount: Long) = {
      val tmpSortedWC: Array[(String, Long)] = if (vocabSize == -1 || fullVocabSize <= vocabSize) {
        // Use all terms
        wordCounts.collect().sortBy(-_._2)
      } else {
        // Sort terms to select vocab
        wordCounts.sortBy(_._2, ascending = false).take(vocabSize)
      }
      (tmpSortedWC.map(_._1).zipWithIndex.toMap, tmpSortedWC.map(_._2).sum)
    }

    val documents = tokenized.map { case (id, tokens) =>
      // Filter tokens by vocabulary, and create word count vector representation of document.
      val wc = new mutable.HashMap[Int, Int]()
      tokens.foreach { term =>
        if (vocab.contains(term)) {
          val termIndex = vocab(term)
          wc(termIndex) = wc.getOrElse(termIndex, 0) + 1
        }
      }
      val indices = wc.keys.toArray.sorted
      val values = indices.map(i => wc(i).toDouble)

      val sb = Vectors.sparse(vocab.size, indices, values)
      (id, sb)
    }

    val vocabArray = new Array[String](vocab.size)
    vocab.foreach { case (term, i) => vocabArray(i) = term }

    val actualCorpusSize = documents.count()
    val actualVocabSize = vocabArray.length
    val preprocessElapsed = (System.nanoTime() - preprocessStart) / 1e9

    println()
    println(s"Corpus summary:")
    println(s"\t Training set size: $actualCorpusSize documents")
    println(s"\t Vocabulary size: $actualVocabSize terms")
    println(s"\t Training set size: $selectedTokenCount tokens")
    println(s"\t Preprocessing time: $preprocessElapsed sec")
    println()

    (documents, vocabArray, selectedTokenCount)
  }
}
