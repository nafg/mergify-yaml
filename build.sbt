val Scala212 = "2.12.16"
val Scala213 = "2.13.8"

ThisBuild / scalacOptions += "-feature"
ThisBuild / organization := "io.github.nafg.mergify"


name := "mergify-yaml"
publish / skip := true


lazy val writer =
  projectMatrix
    .jvmPlatform(List(Scala212, Scala213))
    .settings(
      name := "mergify-writer",
      libraryDependencies += "io.circe" %% "circe-yaml" % "0.14.1",
      libraryDependencies += "io.circe" %% "circe-derivation" % "0.13.0-M5",
      libraryDependencies += "com.propensive" %% "magnolia" % "0.17.0",
      libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided,
      libraryDependencies += "com.lihaoyi" %% "requests" % "0.7.1" % Test,
      libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
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
