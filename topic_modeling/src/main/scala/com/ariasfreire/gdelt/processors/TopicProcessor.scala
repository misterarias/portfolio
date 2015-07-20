package com.ariasfreire.gdelt.processors

import java.text.SimpleDateFormat

import com.ariasfreire.gdelt.models.lda._
import com.ariasfreire.gdelt.utils.ContextUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.{Logging, SparkContext}

/**
 * Created by juanito on 17/07/15.
 */
class TopicProcessor(
                      val dataDir: String,
                      val topicModelArray: Array[TopicTermsDataModel]
                      ) extends Serializable with Logging {

  def run: Array[(String, Iterable[TopicInferenceModel])] = {
    val conf = ContextUtils.conf
    conf.registerKryoClasses(Array(classOf[TopicTermModel], classOf[TopicTermsDataModel]))
    val sc = new SparkContext(conf)

    // Prepare a data structure
    val dateTopicToText: Array[(String, String)] =
      sc.wholeTextFiles(dataDir).map { (files: (String, String)) =>

        // Next time maybe use SequenceFiles?
        val date: String = files._1.split("/").last.replace(".txt", "")
        val content: String = files._2
        (date, content)
      }.collect()
    val combinedData = new Array[(TopicTermsDataModel, String, String)](dateTopicToText.length * topicModelArray.length)

    var i = 0
    val dateFormatter = new SimpleDateFormat("yyyyMMdd")
    val dateOutputter = new SimpleDateFormat("dd-MM-yyyy")
    for (t <- topicModelArray) {
      for (dtt <- dateTopicToText) {
        // This format is automagically indexed in Elastic Search as a date
        val niceDate = dateOutputter.format(dateFormatter.parse(dtt._1))
        combinedData(i) = (t, niceDate, dtt._2)
        i += 1
      }
    }

    // Returns the chance that texts found for date D speak about topic X
    val topicPerDateRDD: RDD[(String, TopicInferenceModel)] =
      sc.parallelize(wrapRefArray(combinedData), Math.min(combinedData.length, 60))
        .map(data => {
        val topicData = data._1
        val date = data._2
        val text = data._3

        // TODO: Logic to infer topics from text
        val chance: Double = Math.random()

        (date, new TopicInferenceModel(topicData.topicName, chance))
      })

    topicPerDateRDD.groupByKey().collect()
  }
}
