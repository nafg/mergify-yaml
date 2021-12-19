package io.github.nafg.mergify

import io.circe.Encoder
import io.circe.derivation.{deriveEncoder, renaming}


case class Mergify(defaults: ActionSet = ActionSet(), pullRequestRules: Seq[PullRequestRule] = Nil) {
  def toYaml: String = Mergify.toYaml(this)

  def addRule(name: String)(conditions: Condition*)(actions: Action*): Mergify =
    copy(pullRequestRules = pullRequestRules :+ PullRequestRule(name, conditions, ActionSet(actions)))

  @deprecated(
    "Renamed to addRule. For the common use case of sbt-github-actions 0.14.0+, try sbt-mergify-github-actions.",
    "0.3.0"
  )
  def withRule(name: String)(conditions: Condition*)(actions: Action*): Mergify =
    addRule(name)(conditions: _*)(actions: _*)
}

object Mergify {
  implicit val encodeMergify: Encoder[Mergify] = deriveEncoder(renaming.snakeCase)
  private val printer = io.circe.yaml.Printer(indent = 4, indicatorIndent = 2)

  def toYaml(mergify: Mergify) = printer.pretty(encodeMergify(mergify))
}
