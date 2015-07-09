import sbt._

object Dependencies {
  val resolutionRepos = Seq(
    "Akka Repository" at "http://repo.akka.io/releases/"
  )

  object V {
    val spark = "1.4.0"
    val scala = "2.10.5"
    val scala_short = "2.10"
  }

  object Libraries {
    val scala_short = V.scala_short
    val sparkCore = "org.apache.spark" % s"spark-core_$scala_short" % V.spark
    val sparkMllib = "org.apache.spark" % s"spark-mllib_$scala_short" % V.spark
    val scopt = "com.github.scopt" % "scopt_2.10" % "3.3.0"
    val boilerpipe = "com.robbypond" % "boilerpipe" % "1.2.3"
    val jsoup = "org.jsoup" % "jsoup" % "1.8.2"
    val elasticSearch = "com.sksamuel.elastic4s" % s"elastic4s-core_$scala_short" % "1.6.4"
    val mallet =  "cc.mallet" % "mallet" % "2.0.7"


    // Scala (test only)
    val scalaTest = "org.scalatest" % s"scalatest_$scala_short" % "2.2.4" % "test"
  }

}
