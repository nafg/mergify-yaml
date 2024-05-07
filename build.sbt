import _root_.io.github.nafg.mergify.dsl._

val Scala212 = "2.12.19"
val Scala213 = "2.13.14"

ThisBuild / scalacOptions += "-feature"
ThisBuild / scalacOptions += "-Xsource:3"
ThisBuild / organization := "io.github.nafg.mergify"

mergifyExtraConditions := Seq(
  (Attr.Author :== "scala-steward") ||
    (Attr.Author :== "nafg-scala-steward[bot]")
)

name           := "mergify-yaml"
publish / skip := true

val generateModels = taskKey[File]("Generate the models by scraping the documentation")

lazy val `generated-models` =
  projectMatrix
    .jvmPlatform(List(Scala212, Scala213))
    .settings(
      libraryDependencies += "io.circe" %% "circe-core" % "0.14.7",
      generateModels := {
        val dir  = (Compile / scalaSource).value / "io/github/nafg/mergify/models/generated"
        val file = dir / "Action.scala"
        IO.write(
          file,
          ScrapeActions
            .run()
            .map(_.linesWithSeparators.map("  " + _).mkString + " extends Action")
            .mkString(
              start = """// GENERATED CODE: DO NOT EDIT THIS FILE MANUALLY.
                  |// TO UPDATE, RUN `sbt generateModels`
                  |// SEE `project/ScrapeActions.scala`
                  |
                  |package io.github.nafg.mergify.models.generated
                  |
                  |import io.github.nafg.mergify.ToJson
                  |
                  |
                  |sealed trait Action
                  |object Action {
                  |""".stripMargin,
              sep = "\n\n\n",
              end = "}\n"
            )
        )
        file
      }
    )

lazy val writer =
  projectMatrix
    .jvmPlatform(List(Scala212, Scala213))
    .dependsOn(`generated-models`)
    .settings(
      name := "mergify-writer",
      libraryDependencies ++= Seq(
        "io.circe"                     %% "circe-yaml"           % "0.14.1",
        "io.circe"                     %% "circe-generic-extras" % "0.14.3",
        "com.softwaremill.magnolia1_2" %% "magnolia"             % "1.1.9",
        "org.scala-lang"                % "scala-reflect"        % scalaVersion.value % Provided,
        "com.lihaoyi"                  %% "requests"             % "0.8.2"            % Test,
        "org.scalameta"                %% "munit"                % "0.7.29"           % Test
      )
    )

//noinspection SpellCheckingInspection
lazy val plugin =
  projectMatrix
    .jvmPlatform(List(Scala212))
    .dependsOn(writer)
    .settings(
      name               := "sbt-mergify-github-actions",
      sbtPlugin          := true,
      crossScalaVersions := List(Scala212),
      addSbtPlugin("com.github.sbt" % "sbt-github-actions" % "0.23.0"),
      libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
    )
