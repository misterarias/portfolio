import sbt.Keys._

// factor out common settings into a sequence
name := "spark_sbt"

version := "1.0"

scalaVersion := "2.10.4"
val sparkVersion = "1.4.0"

// for debugging sbt problems
logLevel := Level.Debug

scalacOptions += "-deprecation"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.10" % sparkVersion % "provided",
  "org.apache.spark" % "spark-mllib_2.10" % sparkVersion
)
libraryDependencies += "org.jsoup" % "jsoup" % "1.8.2"
libraryDependencies += "com.github.scopt" % "scopt_2.10" % "3.3.0"
libraryDependencies += "org.scalatest" % "scalatest_2.10" % "3.0.0-M3"
//libraryDependencies += "org.scala-lang" % "scala-library" % scalaVersion.value
//libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

/**
 * Scala version was updated by one of library dependencies:
 * org.scala-lang:scala-compiler:2.10.0 -> 2.10.4
 * To force scalaVersion, add the following
 */
ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}
