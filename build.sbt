ThisBuild / crossScalaVersions := Seq("2.12.14", "2.13.6")
ThisBuild / scalaVersion := (ThisBuild / crossScalaVersions).value.last
ThisBuild / scalacOptions += "-feature"
ThisBuild / organization := "io.github.nafg.mergify"

name := "mergify-writer"

libraryDependencies += "io.circe" %% "circe-yaml" % "0.14.1"
libraryDependencies += "io.circe" %% "circe-derivation" % "0.13.0-M5"
libraryDependencies += "com.propensive" %% "magnolia" % "0.17.0"
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided
libraryDependencies += "com.lihaoyi" %% "requests" % "0.6.9"

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
