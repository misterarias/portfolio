package com.ariasfreire.gdelt.processors

import java.text.SimpleDateFormat

import com.ariasfreire.gdelt.models.lda._
import com.ariasfreire.gdelt.utils.{ContextUtils, SimpleTokenizer}
import org.apache.spark.rdd.RDD
import org.apache.spark.{Logging, SparkContext}

/**
 * Created by juanito on 17/07/15.
 */
class TopicProcessor(
                      val dataDir: String,
                      val stopWordsFile: String,
                      val topicModelArray: Array[TopicTermsDataModel]
                      ) extends Serializable with Logging {

  def run: Array[(String, Iterable[TopicChanceForDateModel])] = {
    val conf = ContextUtils.conf
    conf.registerKryoClasses(Array(classOf[TopicTermModel], classOf[TopicTermsDataModel]))
    val sc = new SparkContext(conf)

    // Prepare a data structure
    val dateFormatter = new SimpleDateFormat("yyyyMMdd")
    val dateOutputter = new SimpleDateFormat("yyyy-MM-dd")
    val tokenizer = new SimpleTokenizer(sc, stopWordsFile)
    val dateTopicToText: Array[(String, IndexedSeq[String])] =
      sc.wholeTextFiles(dataDir).map { (files: (String, String)) =>

        // Next time maybe use SequenceFiles?
        val date: String = files._1.split("/").last.replace(".txt", "")
        val niceDate = dateOutputter.format(dateFormatter.parse(date))

        val content: String = files._2
        val tokens = tokenizer.getWords(content)

        (niceDate, tokens)
      }.collect()


    val combinedData: Array[(TopicTermsDataModel, String, IndexedSeq[String])] =
      new Array[(TopicTermsDataModel, String, IndexedSeq[String])](dateTopicToText.length * topicModelArray.length)

    var i = 0
    for (t <- topicModelArray) {
      for (dtt <- dateTopicToText) {
        val date = dtt._1
        val tokens = dtt._2
        combinedData(i) = (t, date, tokens)
        i += 1
      }
    }

    // Returns the chance that texts found for date D speak about topic X
    val topicPerDateRDD: RDD[(String, TopicChanceForDateModel)] =
      sc.parallelize(wrapRefArray(combinedData)).map(data => {
        val topicData: TopicTermsDataModel = data._1
        val date: String = data._2
        val tokens: IndexedSeq[String] = data._3
        var chance: Double = 0.0

        // @todo this must be done MUCH better: Worcounts, apply function 1-e^-x, ...
        for (termData <- topicData.termsData) {
          for (token <- tokens) {
            if (termData.term.equalsIgnoreCase(token)) {
              chance = chance + termData.weight
            }
          }
        }

        (topicData.topicName, new TopicChanceForDateModel(date, chance))
      })

    topicPerDateRDD.groupByKey().collect()
  }
}
