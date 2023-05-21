package io.github.nafg.mergify

import io.github.nafg.mergify.CirceConfig.snakeCase

import io.circe.Encoder
import io.circe.generic.extras.semiauto

case class PullRequestRule(name: String, conditions: Seq[Condition], actions: ActionSet)

object PullRequestRule {
  implicit val encodePullRequestRule: Encoder[PullRequestRule] = semiauto.deriveConfiguredEncoder
}
