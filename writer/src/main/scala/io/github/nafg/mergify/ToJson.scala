package io.github.nafg.mergify

import io.circe.{Encoder, Json}

import scala.language.implicitConversions


class ToJson(val underlying: Json) extends AnyVal

object ToJson {
  implicit def valueToSome[A: Encoder](a: A): Option[ToJson] = Some(new ToJson(Encoder[A].apply(a)))

  implicit val encodeToJson: Encoder[ToJson] = Encoder.instance(_.underlying)
}
