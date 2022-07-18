package io.github.nafg.mergify

import io.circe.Encoder

case class Condition(attribute: Attribute[_],
                     test: Option[(Operator, String)] = None,
                     negated: Boolean = false,
                     lengthBased: Boolean = false)

object Condition {
  implicit val encoder: Encoder[Condition] = Encoder.encodeString.contramap {
    case Condition(attribute, test, negated, lengthBased) =>
      (if (negated) "-" else "") +
        (if (lengthBased) "#" else "") +
        attribute.name +
        test.fold("") { case (op, value) => op.symbol + value }
  }
}
