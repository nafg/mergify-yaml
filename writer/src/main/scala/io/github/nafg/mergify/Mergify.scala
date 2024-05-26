package io.github.nafg.mergify

import io.github.nafg.mergify.CirceConfig.snakeCase
import io.github.nafg.mergify.models.generated.Action

import io.circe.Encoder
import io.circe.generic.extras.semiauto.*
import io.circe.yaml.Printer

case class Mergify(defaults: ActionSet = ActionSet(),
                   queueRules: Seq[QueueRule] = Nil,
                   pullRequestRules: Seq[PullRequestRule] = Nil) {
  def addPullRequestRule(name: String)(actions: Action*)(conditions: Condition*): Mergify =
    copy(pullRequestRules = pullRequestRules :+ PullRequestRule(name, conditions, ActionSet(actions)))

  def withDefaultQueueRule(conditions: Condition*): Mergify =
    copy(queueRules = List(QueueRule("default", mergeConditions = conditions)))

  def toYaml: String = Mergify.toYaml(this)
}

object Mergify {
  implicit val encodeMergify: Encoder[Mergify] = deriveConfiguredEncoder
  private val printer = Printer(indent = 4, indicatorIndent = 2, dropNullKeys = true, preserveOrder = true)

  def toYaml(mergify: Mergify): String = printer.pretty(encodeMergify(mergify))
}
