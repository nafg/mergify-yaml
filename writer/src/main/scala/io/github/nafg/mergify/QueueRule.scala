package io.github.nafg.mergify

import io.circe.Encoder
import io.circe.derivation.{deriveEncoder, renaming}

import java.time.Duration


case class QueueRule(name: String,
                     conditions: Seq[Condition],
                     speculativeChecks: Option[Int] = None,
                     allowInplaceSpeculativeChecks: Option[Boolean] = None,
                     allowSpeculativeChecksInterruption: Option[Boolean] = None,
                     batchSize: Option[Int] = None,
                     checksTimeout: Option[Duration] = None)

object QueueRule {
  implicit val encodeDuration: Encoder[Duration] =
    Encoder.encodeString.contramap { d =>
      List(
        d.toDaysPart -> "days",
        d.toHoursPart -> "hours",
        d.toMinutesPart -> "minutes",
        d.toSecondsPart -> "seconds"
      )
        .filter(_._1 != 0)
        .map(_.productIterator.mkString(" "))
        .mkString(" ")
    }
  implicit val encodeQueueRule: Encoder[QueueRule] = deriveEncoder(renaming.snakeCase)
}
