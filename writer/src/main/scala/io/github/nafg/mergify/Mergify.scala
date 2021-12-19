package io.github.nafg.mergify

import io.circe.Encoder
import io.circe.derivation.{deriveEncoder, renaming}


case class Mergify(defaults: ActionSet = ActionSet(), pullRequestRules: Seq[PullRequestRule] = Nil) {
  def toYaml: String = Mergify.toYaml(this)

  def addRule(name: String)(conditions: Condition*)(actions: Action*) =
    copy(pullRequestRules = pullRequestRules :+ PullRequestRule(name, conditions, ActionSet(actions)))
}

object Mergify {
  implicit val encodeMergify: Encoder[Mergify] = deriveEncoder(renaming.snakeCase)
  private val printer = io.circe.yaml.Printer(indent = 4, indicatorIndent = 2)

  def toYaml(mergify: Mergify) = printer.pretty(encodeMergify(mergify))
}
