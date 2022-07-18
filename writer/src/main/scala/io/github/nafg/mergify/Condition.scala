package io.github.nafg.mergify

import io.circe.syntax.{EncoderOps, KeyOps}
import io.circe.{Encoder, Json}

sealed trait Condition {
  final def &&(that: Condition) = (this, that) match {
    case (Condition.And(conditions1 @ _*), Condition.And(conditions2 @ _*)) =>
      Condition.And(
        conditions1 ++
          conditions2: _*
      )
    case (Condition.And(conditions1 @ _*), other2) => Condition.And(conditions1 :+ other2: _*)
    case (other1, Condition.And(conditions2 @ _*)) => Condition.And(other1 +: conditions2: _*)
    case (other1, other2)                          => Condition.And(other1, other2)
  }
  final def ||(that: Condition) = (this, that) match {
    case (Condition.Or(conditions1 @ _*), Condition.Or(conditions2 @ _*)) =>
      Condition.Or(
        conditions1 ++
          conditions2: _*
      )
    case (Condition.Or(conditions1 @ _*), other2) => Condition.Or(conditions1 :+ other2: _*)
    case (other1, Condition.Or(conditions2 @ _*)) => Condition.Or(other1 +: conditions2: _*)
    case (other1, other2)                         => Condition.Or(other1, other2)
  }
}
object Condition {
  case class Simple(attribute: Attribute[_],
                    test: Option[(Operator, String)] = None,
                    negated: Boolean = false,
                    lengthBased: Boolean = false)
      extends Condition {
    def unary_! = copy(negated = !negated)
  }
  case class And(conditions: Condition*) extends Condition
  case class Or(conditions: Condition*)  extends Condition

  private def encodeConditionImpl: Encoder[Condition] = Encoder.instance {
    case And(conditions @ _*) => Json.obj("and" := conditions.asJson)
    case Or(conditions @ _*)  => Json.obj("or" := conditions.asJson)
    case Simple(attribute, test, negated, lengthBased) =>
      Json.fromString(
        (if (negated) "-" else "") +
          (if (lengthBased) "#" else "") +
          attribute.name +
          test.fold("") { case (op, value) => op.symbol + value }
      )
  }
  implicit lazy val encodeCondition: Encoder[Condition] = encodeConditionImpl
}
