package io.github.nafg.mergify.sbt

import io.github.nafg.mergify.dsl.*

import sbt.*
import sbtghactions.GenerativeKeys.{githubWorkflowGenerate, githubWorkflowGeneratedCI}
import sbtghactions.{GenerativePlugin, WorkflowJob}

object WriteMergify extends AutoPlugin {
  override def requires = GenerativePlugin

  override def trigger = allRequirements

  def createMergify(job: WorkflowJob) = {
    val conditions =
      (Attr.Author :== "scala-steward") +:
        (for (o <- job.oses; s <- job.scalas; v <- job.javas)
          yield Attr.CheckSuccess :== s"${job.name} ($o, $s, ${v.render})")
    defaultMergify
      .addPullRequestRule("Automatically merge successful scala-steward PRs")(defaultQueueAction)(conditions*)
  }

  override def projectSettings = Seq(
    githubWorkflowGenerate := {
      githubWorkflowGenerate.value
      for (job <- githubWorkflowGeneratedCI.value if job.id == "build")
        IO.write(file(".mergify.yml"), createMergify(job).toYaml)
    }
  )
}
