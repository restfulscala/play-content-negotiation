versionWithGit

git.baseVersion := "0.3.0"

name := "play-content-negotiation"

organization := "org.restfulscala"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M1" % "test"
)

val root = project.in(file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

StandardLayout.settings

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

bintrayOrganization := Some("restfulscala")

bintrayPackageLabels := Seq("scala", "rest", "play")
