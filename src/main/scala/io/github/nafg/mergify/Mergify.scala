package io.github.nafg.mergify

import io.circe.Encoder
import io.circe.derivation.{deriveEncoder, renaming}


case class Mergify(pullRequestRules: Seq[PullRequestRule], defaults: ActionSet = ActionSet())

object Mergify {
  implicit val encodeMergify: Encoder[Mergify] = deriveEncoder(renaming.snakeCase)
}
