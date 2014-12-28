import com.typesafe.sbt.SbtGit._
import play.PlayScala

versionWithGit

git.baseVersion := "0.2.0"

name := "play-content-negotiation"

organization := "org.restfulscala"

libraryDependencies ++= Seq(
  "com.yetu" %% "siren-scala" % "0.4.0",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalatestplus" %% "play" % "1.2.0" % "test"
)

val root = project in file(".") enablePlugins PlayScala

scalaVersion := "2.11.4"

StandardLayout.settings

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

bintray.Plugin.bintraySettings

bintray.Keys.bintrayOrganization in bintray.Keys.bintray := Some("restfulscala")

bintray.Keys.packageLabels in bintray.Keys.bintray := Seq("scala", "rest", "play")

scoverage.ScoverageSbtPlugin.instrumentSettings

org.scoverage.coveralls.CoverallsPlugin.coverallsSettings
