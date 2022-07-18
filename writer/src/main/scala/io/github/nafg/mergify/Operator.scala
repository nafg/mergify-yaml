package io.github.nafg.mergify

sealed abstract class Operator(val symbol: String)

object Operator {

  /** This operator checks for strict equality. If the target attribute type is a list, each element of the list is
    * compared against the value and the condition is true if any value matches.
    */
  case object Equal extends Operator("=")

  /** This operator checks for non equality. If the target attribute type is a list, each element of the list is
    * compared against the value and the condition is true if no value matches.
    */
  case object NotEqual extends Operator("!=")

  /** This operator checks for regular expression matching. If the target attribute type is a list, each element of the
    * list is matched against the value and the condition is true if any value matches.
    */
  case object Match extends Operator("~=")

  /** This operator checks for the value to be greater than or equal to the provided value. It’s usually used to compare
    * against the length of a list using the # prefix.
    */
  case object GreaterThanOrEqual extends Operator(">=")

  /** This operator checks for the value to be greater than the provided value. It’s usually used to compare against the
    * length of a list using the # prefix.
    */
  case object GreaterThan extends Operator(">")

  /** This operator checks for the value to be lesser then or equal to the provided value. It’s usually used to compare
    * against the length of a list using the # prefix.
    */
  case object LesserThanOrEqual extends Operator("<=")

  /** This operator checks for the value to be lesser than the provided value. It’s usually used to compare against the
    * length of a list using the # prefix.
    */
  case object LesserThan extends Operator("<")
}
