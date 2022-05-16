package io.github.nafg.mergify.demo

import io.github.nafg.mergify.QueueRule
import io.github.nafg.mergify.dsl._

import java.time.Duration


class Test extends munit.FunSuite {
  test("test") {
    val yaml =
      defaultMergify
        .addPullRequestRule("automatic merge successful scala-steward PRs")(
          Action.Merge(),
          Action.Queue(name = "default"),
          Action.DismissReviews(changesRequested = false)
        )(
          Attr.Author :== "scala-steward",
          Attr.CheckSuccess :== "Travis CI - Pull Request"
        ).copy(queueRules = List(
        QueueRule(
          name = "default",
          conditions = List(
            Attr.Author :== "scala-steward",
            Attr.CheckSuccess :== "Travis CI - Pull Request"
          ),
          checksTimeout = Some(Duration.ZERO.plusDays(1).plusHours(2).plusMinutes(3).plusSeconds(4))
        )
      ))
        .toYaml

    println(yaml)

    requests.post(
      url = "https://engine.mergify.io/validate/",
      data = requests.MultiPart(requests.MultiItem("data", yaml, ".mergify.yml")),
      check = true
    )
  }
}
