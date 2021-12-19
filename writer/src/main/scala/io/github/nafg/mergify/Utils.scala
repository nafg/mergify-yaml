package io.github.nafg.mergify

object Utils {
  private def toSnakeCase(chars: List[Char]): List[Char] = chars match {
    case Nil                                          => Nil
    case c1 :: c2 :: rest if c1.isLower && c2.isUpper => c1 :: '_' :: c2.toLower :: toSnakeCase(rest)
    case c :: rest                                    => c.toLower :: toSnakeCase(rest)
  }

  def toSnakeCase(string: String): String = toSnakeCase(string.toList).mkString
}
