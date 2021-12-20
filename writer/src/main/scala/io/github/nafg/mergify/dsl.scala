package io.github.nafg.mergify

import scala.language.implicitConversions


object dsl {
  implicit def boolAttributeToCondition(attribute: Attribute[Boolean]): Condition = Condition(attribute)

  protected class conditionBuilder(attribute: Attribute[_], operator: Operator) {
    def apply(value: String) = Condition(attribute, Some(operator -> value))
  }

  implicit class AttributeOps(attribute: Attribute[_]) {
    object :== extends conditionBuilder(attribute, Operator.Equal)

    object :!= extends conditionBuilder(attribute, Operator.NotEqual)

    object ~= extends conditionBuilder(attribute, Operator.Match)

    object >= extends conditionBuilder(attribute, Operator.GreaterThanOrEqual)

    object > extends conditionBuilder(attribute, Operator.GreaterThan)

    object <= extends conditionBuilder(attribute, Operator.LesserThanOrEqual)

    object < extends conditionBuilder(attribute, Operator.LesserThan)
  }

  def defaultMergify = Mergify().withDefaultQueueRule()

  val Attr = Attribute
  val Action = io.github.nafg.mergify.Action

  def defaultQueueAction = Action.Queue(name = "default")
}
