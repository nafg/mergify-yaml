val Scala212 = "2.12.16"
val Scala213 = "2.13.8"

ThisBuild / scalacOptions += "-feature"
ThisBuild / organization := "io.github.nafg.mergify"


name := "mergify-yaml"
publish / skip := true

val generateModels = taskKey[File]("Generate the models by scraping the documentation")

lazy val `generated-models` =
  projectMatrix
    .jvmPlatform(List(Scala212, Scala213))
    .settings(
      libraryDependencies += "io.circe" %% "circe-core" % "0.14.1",
      generateModels := {
        val dir = (Compile / scalaSource).value / "io/github/nafg/mergify/models/generated"
        val file = dir / "Action.scala"
        IO.write(
          file,
          ScrapeActions.run()
            .map(_.linesWithSeparators.map("  " + _).mkString + " extends Action")
            .mkString(
              start =
                """// GENERATED CODE: DO NOT EDIT THIS FILE MANUALLY.
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
      libraryDependencies += "io.circe" %% "circe-yaml" % "0.14.1",
      libraryDependencies += "io.circe" %% "circe-derivation" % "0.13.0-M5",
      libraryDependencies += "com.propensive" %% "magnolia" % "0.17.0",
      libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided,
      libraryDependencies += "com.lihaoyi" %% "requests" % "0.7.1" % Test,
      libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
    )

lazy val plugin =
  projectMatrix
    .jvmPlatform(List(Scala212))
    .dependsOn(writer)
    .settings(
      name := "sbt-mergify-github-actions",
      sbtPlugin := true,
      crossScalaVersions := List(Scala212),
      addSbtPlugin("com.codecommit" % "sbt-github-actions" % "0.14.2"),
      libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
    )
