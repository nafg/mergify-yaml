package io.github.nafg.mergify

import io.circe.Encoder
import io.circe.derivation.{deriveEncoder, renaming}


case class PullRequestRule(name: String, conditions: Seq[Condition], actions: ActionSet)

object PullRequestRule {
  implicit val encodePullRequestRule: Encoder[PullRequestRule] = deriveEncoder(renaming.snakeCase)
}
