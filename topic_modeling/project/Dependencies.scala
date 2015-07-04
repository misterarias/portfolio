import sbt._

object Dependencies {
  val resolutionRepos = Seq(
    "Akka Repository" at "http://repo.akka.io/releases/"
  )

  object V {
    val spark = "1.4.0"
    val scala = "2.11.6"
    val scala_maven = "2.11"
    // Add versions for your additional libraries here...
  }

  object Libraries {
    val sparkCore = "org.apache.spark" %% "spark-core" % V.spark % "provided"
    val sparkMllib = "org.apache.spark" %% "spark-mllib" % V.spark % "provided"
    val scopt = "com.github.scopt" % "scopt_2.10" % "3.3.0"
    val boilerpipe = "com.robbypond" % "boilerpipe" % "1.2.3"
    val jsoup = "org.jsoup" % "jsoup" % "1.8.2"

    // Scala (test only)
    val scalaTest = "org.scalatest" % "scalatest_2.11" % "2.2.5" % "test"
  }

}
