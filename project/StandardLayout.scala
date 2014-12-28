import sbt._
import sbt.Keys._

object StandardLayout {
  def settings: Seq[Setting[_]] = List(
    sourceDirectory := baseDirectory.value / "src",
    sourceDirectory in Compile := sourceDirectory.value / "main",
    sourceDirectory in Test := sourceDirectory.value / "test",
    resourceDirectory in Compile := (sourceDirectory in Compile).value / "resources",
    resourceDirectory in Test := (sourceDirectory in Test).value / "resources",
    scalaSource in Compile := (sourceDirectory in Compile).value / "scala",
    scalaSource in Test := (sourceDirectory in Test).value / "scala",
    javaSource in Compile := (sourceDirectory in Compile).value / "java",
    javaSource in Test := (sourceDirectory in Test).value / "java"
  )

}