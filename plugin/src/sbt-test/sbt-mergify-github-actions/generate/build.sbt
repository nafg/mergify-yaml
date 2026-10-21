import _root_.io.github.nafg.mergify.dsl.*

ThisBuild / scalaVersion               := "2.13.18"
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("17"))

mergifyExtraConditions := Seq(
  (Attr.Author :== "scala-steward") ||
    (Attr.Author :== "nafg-scala-steward[bot]")
)
