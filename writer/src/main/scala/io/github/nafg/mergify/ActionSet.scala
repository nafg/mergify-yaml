package io.github.nafg.mergify

import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}
import magnolia.{CaseClass, Magnolia, SealedTrait}

import scala.language.experimental.macros
import scala.language.higherKinds


case class ActionSet(actions: Seq[Action] = Nil)

object ActionSet {
  private type Typeclass[A] = Encoder[A]

  //noinspection ScalaUnusedSymbol
  private def combine[A](ctx: CaseClass[Encoder, A]): Encoder[A] = Encoder.encodeMap[String, Json].contramap { a =>
    ctx.parameters
      .filterNot(param => param.default.contains(param.dereference(a)))
      .map(param => Utils.toSnakeCase(param.label) -> param.typeclass.apply(param.dereference(a)))
      .toMap
  }

  //noinspection ScalaUnusedSymbol
  private def dispatch[A](ctx: SealedTrait[Encoder, A]): Encoder[A] = Encoder.instance { a =>
    ctx.dispatch(a) { subtype =>
      subtype.typeclass.apply(subtype.cast(a))
    }
  }


  private def gen[A]: Encoder[A] = macro Magnolia.gen[A]

  private implicit def encodeAction: Encoder[Action] = gen[Action]

  implicit val encodeActionSet: Encoder[ActionSet] = Encoder.instance { case ActionSet(actions) =>
    actions
      .map(action => Utils.toSnakeCase(action.getClass.getSimpleName) -> action.asJson)
      .toMap
      .asJson
  }
}
