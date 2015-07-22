package com.ariasfreire.gdelt.utils

import com.ariasfreire.gdelt.models.lda.TopicInferenceInfoModel
import com.ariasfreire.gdelt.processors.extractors.LargestContentExtractor
import com.ariasfreire.gdelt.processors.parsers.GdeltRowParser
import com.sksamuel.elastic4s.{SimpleAnalyzer, KeywordAnalyzer, ElasticClient}
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.{StringType, DateType}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.SparkConf
import org.elasticsearch.indices.IndexMissingException

/**
 * Created by juanito on 11/07/15.
 */
object ContextUtils {

  /**
   * If set to true, override output directory
   */
  var overwrite: Boolean = false

  /**
   * Set master type
   */
  var masterType: String = "local[*]"

  private val myConf = new SparkConf()
    .setAppName(s"Spark GDELT Project")
    // Supposedly up to 10x faster serialization (Note: Holy **** it's fast)
    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

  /**
   * After calling this, and before creating a SparkContext, the applications must set the Kryo classes
   * @return
   */
  def conf: SparkConf = {

    val conf = myConf
      .setMaster(masterType)

    // So that output directory gets overwritten
    if (overwrite)
      conf.set("spark.hadoop.validateOutputSpecs", "false")

    // So that output directory gets overwritten
    if (overwrite)
      conf.set("spark.hadoop.validateOutputSpecs", "false")

    conf.registerKryoClasses(Array(classOf[GdeltRowParser], classOf[LargestContentExtractor]))

    conf
  }

  /**
   * Read from configuration the URL and port of the Elastic Search master
   * @todo actually read from configuration
   *
   * @return a valid elastic search client
   */
  val esClient: ElasticClient = ElasticClient.remote("127.0.0.1", 9300)

  /**
   * Overridable index name
   */
  var indexName: String = "results"

  /**
   * Create indexes for this session
   * @return
   */
  def createIndex = {
    esClient.execute {
      create index indexName mappings {
        TopicInferenceInfoModel.indexType as (
          "dataSetName" typed StringType analyzer KeywordAnalyzer,
          "topicName" typed StringType analyzer KeywordAnalyzer
          ) dateDetection true dynamicDateFormats("yyyyMMdd", "dd-MM-yyyy")
      }
    }
  }

  /**
   * Drop indexes created for this session
   * @return
   */
  def dropIndex = {
    try {
      esClient.execute {
        deleteIndex(indexName)
      } await()
    } catch {
      case x: IndexMissingException =>
        println(s"Index $indexName does not exist")
      case undefined: Throwable =>
        println(s"Undefined error dropping Index: ${undefined.getLocalizedMessage}")
    }
  }

  /**
   * Returns true if given path exists
   * @param pathName path to query
   * @return
   */
  def dirExists(pathName: String): Boolean = {
    val fs = FileSystem.get(new Configuration())
    val hdfsDir = new Path(pathName)
    fs.exists(hdfsDir)
  }

  /**
   * Returns true if given directory is not empty
   * @param pathName path to check
   * @return
   */
  def hasFiles(pathName: String): Boolean = {
    val fs = FileSystem.get(new Configuration())
    val hdfsDir = new Path(pathName)
    fs.exists(hdfsDir) && fs.listStatus(hdfsDir).nonEmpty
  }
}
