versionWithGit

git.baseVersion := "0.4.0"

name := "play-content-negotiation"

organization := "org.restfulscala"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % "test"
)

val root = project.in(file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

StandardLayout.settings

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

bintrayOrganization := Some("restfulscala")

bintrayPackageLabels := Seq("scala", "rest", "play")
