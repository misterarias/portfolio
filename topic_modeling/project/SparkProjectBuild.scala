import sbt.Keys._
import sbt._

object SparkProjectBuild extends Build {

  import BuildSettings._
  import Dependencies._

  // Configure prompt to show current project
  override lazy val settings = super.settings :+ {
    shellPrompt := { s => Project.extract(s).currentProject.id + " > " }
  }

  // Define our project, with basic project information and library dependencies
  lazy val project = Project("topic_modeling", file("."))
    .settings(buildSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        Libraries.sparkCore,
        Libraries.sparkMllib,
        // Additional libs
        Libraries.scopt,
        Libraries.jsoup,
        Libraries.boilerpipe,

        // Test libs
        Libraries.scalaTest
      )
    )
}
