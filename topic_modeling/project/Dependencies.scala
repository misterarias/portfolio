import sbt._

object Dependencies {
  val resolutionRepos = Seq(
    "Akka Repository" at "http://repo.akka.io/releases/"
 //   "Spray Repository" at "http://repo.spray.cc/"
  )

  object V {
    val spark     = "1.4.0"
   // val specs2    = "1.13" // -> "1.13" when we bump to Scala 2.10.0
  //  val guava     = "11.0.1"
    // Add versions for your additional libraries here...
  }

  object Libraries {
    val sparkCore    = "org.apache.spark"           %% "spark-core"            % V.spark        % "provided"
    val sparkMllib   = "org.apache.spark"           %% "spark-mllib"           % V.spark        % "provided"
 //   val sparkSql     = "org.apache.spark"           %% "spark-sql"             % V.spark        % "provided"
    // Add additional libraries from mvnrepository.com (SBT syntax) here...
    val scopt =  "com.github.scopt" % "scopt_2.10" % "3.3.0"
    val boilerpipe = "com.robbypond" % "boilerpipe" % "1.2.3"
    val jsoup = "org.jsoup" % "jsoup" % "1.8.2"

    // Scala (test only)
  //  val specs2       = "org.specs2"                 % "specs2_2.10"           % V.specs2       % "test"
   // val guava        = "com.google.guava"           % "guava"                 % V.guava        % "test"
  }
}
