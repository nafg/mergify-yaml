package io.github.nafg.mergify

import io.circe.generic.extras.Configuration

private[mergify] object CirceConfig {
  implicit val snakeCase: Configuration =
    Configuration.default.withSnakeCaseMemberNames.withSnakeCaseConstructorNames
}
