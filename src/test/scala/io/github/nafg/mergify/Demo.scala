package io.github.nafg.mergify

import io.circe.Json
import io.circe.syntax._
import io.circe.yaml.printer


object Demo extends App {
  val data =
    Mergify(
      pullRequestRules =
        List(
          PullRequestRule(
            name = "automatic merge successful scala-steward PRs",
            conditions =
              List(
                Condition(Attribute.Author, Some(Operator.Equal -> "scala-steward")),
                Condition(Attribute.CheckSuccess, Some(Operator.Equal -> "Travis CI - Pull Request"))
              ),
            actions =
              ActionSet(
                List(
                  Action.Merge(
                    strict = Some(Json.fromBoolean(true))
                  ),
                  Action.DismissReviews(
                    changesRequested = Some(Json.fromBoolean(false))
                  )
                )
              )
          )
        )
    )

  val yaml = printer.print(data.asJson)

  println(yaml)

  requests.post(
    url = "https://gh.mergify.io/validate/",
    data = requests.MultiPart(requests.MultiItem("data", yaml, ".mergify.yml")),
    check = true
  )
}
