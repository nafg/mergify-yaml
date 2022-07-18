package io.github.nafg.mergify.sbt

import io.github.nafg.mergify.dsl.*
import io.github.nafg.mergify.{Condition, Mergify}

import sbt.*
import sbtghactions.GenerativeKeys.{githubWorkflowGenerate, githubWorkflowGeneratedCI}
import sbtghactions.{GenerativePlugin, WorkflowJob}

object WriteMergify extends AutoPlugin {
  override def requires = GenerativePlugin

  override def trigger = allRequirements

  val mergifyScalaStewardAuthor = settingKey[String]("The author that identifies Scala Steward pull requests")
  val mergifyScalaStewardIncludedJobs = settingKey[Seq[WorkflowJob]](
    "The generated jobs that have to pass to merge Scala Steward PRs"
  )
  val mergifyScalaStewardConditions = settingKey[Seq[Condition]]("The conditions for the Scala Steward PR rule")
  val mergify                       = settingKey[Mergify]("The resulting Mergify object")

  def workflowJobCheckNames(jobs: Seq[WorkflowJob]) =
    for (job <- jobs; o <- job.oses; s <- job.scalas; v <- job.javas)
      yield s"${job.name} ($o, $s, ${v.render})"

  def defaultScalaStewardAuthor = "scala-steward"

  def defaultScalaStewardConditions(author: String, jobs: Seq[WorkflowJob]) =
    (Attr.Author :== author) +:
      (for (name <- workflowJobCheckNames(jobs))
        yield Attr.CheckSuccess :== name)

  def defaultScalaStewardMergify(conditions: Seq[Condition]) =
    defaultMergify
      .addPullRequestRule("Automatically merge successful Scala Steward PRs")(defaultQueueAction)(
        conditions*
      )

  override def projectSettings = {
    Seq(
      mergifyScalaStewardAuthor       := defaultScalaStewardAuthor,
      mergifyScalaStewardIncludedJobs := githubWorkflowGeneratedCI.value.filter(_.id == "build"),
      mergifyScalaStewardConditions :=
        defaultScalaStewardConditions(
          mergifyScalaStewardAuthor.value,
          mergifyScalaStewardIncludedJobs.value
        ),
      mergify := defaultScalaStewardMergify(mergifyScalaStewardConditions.value),
      githubWorkflowGenerate := {
        githubWorkflowGenerate.value
        IO.write(file(".mergify.yml"), mergify.value.toYaml)
      }
    )
  }
}
