package io.github.nafg.mergify.sbt

import io.github.nafg.mergify.dsl.*
import io.github.nafg.mergify.{Condition, Mergify}

import sbt.*
import sbtghactions.GenerativeKeys.{githubWorkflowGenerate, githubWorkflowGeneratedCI}
import sbtghactions.{GenerativePlugin, WorkflowJob}

object WriteMergify extends AutoPlugin {
  override def requires = GenerativePlugin

  override def trigger = allRequirements

  // noinspection ScalaWeakerAccess
  object autoImport {
    val mergifyExtraConditions = settingKey[Seq[Condition]]("Conditions required to merge other than passing the build")
    val mergifyIncludedJobs = settingKey[Seq[WorkflowJob]](
      "The generated jobs that have to pass to merge Scala Steward PRs"
    )
    val mergifyConditions = settingKey[Seq[Condition]]("The conditions for the Scala Steward PR rule")
    val mergify           = settingKey[Mergify]("The resulting Mergify object")
  }

  import autoImport.*

  def workflowJobCheckNames(jobs: Seq[WorkflowJob]) =
    for (job <- jobs; o <- job.oses; s <- job.scalas; v <- job.javas)
      yield s"${job.name} ($o, $s, ${v.render})"

  def defaultExtraConditions = Seq(Attr.Author :== "scala-steward")

  def defaultConditions(extraConditions: Seq[Condition], jobs: Seq[WorkflowJob]) =
    extraConditions ++
      (for (name <- workflowJobCheckNames(jobs))
        yield Attr.CheckSuccess :== name)

  def buildMergify(conditions: Seq[Condition]) =
    defaultMergify
      .addPullRequestRule("Automatically merge successful Scala Steward PRs")(defaultQueueAction)(
        conditions*
      )

  override def projectSettings =
    Seq(
      mergifyExtraConditions := defaultExtraConditions,
      mergifyIncludedJobs    := githubWorkflowGeneratedCI.value.filter(_.id == "build"),
      mergifyConditions      := defaultConditions(mergifyExtraConditions.value, mergifyIncludedJobs.value),
      mergify                := buildMergify(mergifyConditions.value),
      githubWorkflowGenerate := {
        githubWorkflowGenerate.value
        IO.write(file(".mergify.yml"), mergify.value.toYaml)
      }
    )
}
