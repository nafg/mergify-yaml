ThisBuild / scalacOptions += "-feature"
ThisBuild / organization := "io.github.nafg.mergify"

name := "mergify-writer"

publish / skip := true

crossScalaVersions := Nil

val Scala212 = "2.12.15"
val Scala213 = "2.13.7"

lazy val writer =
  project
    .settings(
      scalaVersion := Scala212,
      crossScalaVersions := Seq(scalaVersion.value, Scala213),
      libraryDependencies += "io.circe" %% "circe-yaml" % "0.14.1",
      libraryDependencies += "io.circe" %% "circe-derivation" % "0.13.0-M5",
      libraryDependencies += "com.propensive" %% "magnolia" % "0.17.0",
      libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided,
      libraryDependencies += "com.lihaoyi" %% "requests" % "0.7.0",
      Compile / sourceGenerators += Def.task {
        val dir = (Compile / sourceManaged).value / "io/github/nafg/mergify"
        val file = dir / "models.scala"
        IO.write(
          file,
          ScrapeActions.run()
            .map(_.linesWithSeparators.map("  " + _).mkString + " extends Action")
            .mkString(
              start =
                """package io.github.nafg.mergify
                  |
                  |import io.circe.Json
                  |
                  |
                  |sealed trait Action
                  |object Action {
                  |""".stripMargin,
              sep = "\n\n\n",
              end = "}\n"
            )
        )
        Seq(file)
      }.taskValue
    )

lazy val plugin =
  project
    .dependsOn(writer)
    .settings(
      sbtPlugin := true,
      scalaVersion := Scala212,
      addSbtPlugin("com.codecommit" % "sbt-github-actions" % "0.13.0"),
      name := "sbt-mergify-github-actions"
    )
