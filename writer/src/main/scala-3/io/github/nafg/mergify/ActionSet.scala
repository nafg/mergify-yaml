package io.github.nafg.mergify

import io.github.nafg.mergify.models.generated.Action

import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}
import magnolia1.*

case class ActionSet(actions: Seq[Action] = Nil)

object ActionSet {

  private object MyEncoder extends AutoDerivation[Encoder] {
    override def join[T](ctx: CaseClass[Encoder, T]): Encoder[T] = Encoder.instance {
      case j: Json =>
        j
      case a =>
        Json.fromFields(
          ctx.params
            .filterNot(param => param.default.contains(param.deref(a)))
            .map(param => Utils.toSnakeCase(param.label) -> param.typeclass(param.deref(a)))
        )
    }

    override def split[A](ctx: SealedTrait[Encoder, A]): Encoder[A] = Encoder.instance {
      case j: Json =>
        j
      case a =>
        ctx.choose(a) { subtype =>
          subtype.typeclass.apply(subtype.cast(a))
        }
    }
  }

  private given Encoder[Action] = MyEncoder.derivedMirror[Action]

  given encodeActionSet: Encoder[ActionSet] = Encoder.instance { case ActionSet(actions) =>
    actions
      .map(action => Utils.toSnakeCase(action.getClass.getSimpleName) -> action.asJson)
      .toMap
      .asJson
  }
}
