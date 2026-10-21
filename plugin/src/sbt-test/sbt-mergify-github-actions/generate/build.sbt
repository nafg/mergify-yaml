import _root_.io.github.nafg.mergify.dsl.*

ThisBuild / scalaVersion               := "3.10.0"
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("17"))

mergifyExtraConditions := Seq(
  (Attr.Author :== "scala-steward") ||
    (Attr.Author :== "nafg-scala-steward[bot]")
)
