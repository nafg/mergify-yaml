package io.github.nafg.mergify.sbt

import sbtghactions.{JavaSpec, WorkflowJob}


class Test extends munit.FunSuite {
  test("test") {
    val mergify =
      WriteMergify
        .createMergify(
          WorkflowJob(
            id = "build",
            name = "Build and Test",
            steps = Nil,
            oses = List("ubuntu-latest"),
            scalas = List("2.13.7"),
            javas = List(JavaSpec.temurin("11"))
          )
        )

    val yaml = mergify.toYaml

    assertEquals(
      yaml,
      """defaults: {}
        |queue_rules:
        |  - name: default
        |    conditions: []
        |pull_request_rules:
        |  - name: Automatically merge successful scala-steward PRs
        |    conditions:
        |      - author=scala-steward
        |      - check-success=Build and Test (ubuntu-latest, 2.13.7, temurin@11)
        |    actions:
        |        queue:
        |            name: default
        |""".stripMargin
    )

  }
}
