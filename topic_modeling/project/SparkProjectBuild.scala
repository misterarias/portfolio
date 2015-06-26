import sbt._
import Keys._

object SparkExampleProjectBuild extends Build {

  import Dependencies._
  import BuildSettings._

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
 
        Libraries.scopt,
        Libraries.jsoup,
        Libraries.boilerpipe
        // Add your additional libraries here (comma-separated)...
      )
    )
}
