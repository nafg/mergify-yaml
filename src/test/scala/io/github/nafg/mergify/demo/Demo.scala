package io.github.nafg.mergify.demo

import io.github.nafg.mergify.dsl._


object Demo extends App {
  val yaml =
    mergify
      .withRule("automatic merge successful scala-steward PRs")(
        Attr.Author :== "scala-steward",
        Attr.CheckSuccess :== "Travis CI - Pull Request"
      )(
        Action.Merge(strict = true),
        Action.DismissReviews(changesRequested = false)
      )
      .toYaml

  println(yaml)

  requests.post(
    url = "https://gh.mergify.io/validate/",
    data = requests.MultiPart(requests.MultiItem("data", yaml, ".mergify.yml")),
    check = true
  )
}
