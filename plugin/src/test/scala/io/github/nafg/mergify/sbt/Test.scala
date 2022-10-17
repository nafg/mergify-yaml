package io.github.nafg.mergify.sbt

import io.github.nafg.mergify.Condition
import io.github.nafg.mergify.dsl.*

import sbtghactions.{JavaSpec, WorkflowJob}

class Test extends munit.FunSuite {
  test("Simple example") {
    val mergify =
      WriteMergify.buildMergify(
        WriteMergify.defaultConditions(
          WriteMergify.defaultExtraConditions,
          Seq(
            WorkflowJob(
              id = "build",
              name = "Build and Test",
              steps = Nil,
              oses = List("ubuntu-latest"),
              scalas = List("2.13.7"),
              javas = List(JavaSpec.temurin("11"))
            )
          )
        )
      )

    assertEquals(
      """defaults: {}
        |queue_rules:
        |  - name: default
        |    conditions: []
        |pull_request_rules:
        |  - name: Automatically merge successful Scala Steward PRs
        |    conditions:
        |      - author=scala-steward
        |      - check-success=Build and Test (ubuntu-latest, 2.13.7, temurin@11)
        |    actions:
        |        queue: {}
        |""".stripMargin,
      mergify.toYaml
    )
  }

  test("Nested conditions and boolean operators") {
    val thing =
      defaultMergify.addPullRequestRule("name")(defaultQueueAction)(
        ((Attr.Author :== "test") && (Attr.Assignee :== "someone")) ||
          !Attr.Draft
      )

    assertEquals(
      """defaults: {}
        |queue_rules:
        |  - name: default
        |    conditions: []
        |pull_request_rules:
        |  - name: name
        |    conditions:
        |      - or:
        |          - and:
        |              - author=test
        |              - assignee=someone
        |          - -draft
        |    actions:
        |        queue: {}
        |""".stripMargin,
      thing.toYaml
    )
  }

  test("Multiple alternatives") {
    val jobs = List(WorkflowJob("build", "Build", Nil, scalas = List("2.13.10", "3.2.0")))
    val authorsCondition =
      Seq("scala-steward", "renovate[bot]")
        .map(Attr.Author :== _)
        .reduce[Condition](_ || _)
    val conditions = WriteMergify.defaultConditions(Seq(authorsCondition), jobs)

    assertEquals(
      """defaults: {}
        |queue_rules:
        |  - name: default
        |    conditions: []
        |pull_request_rules:
        |  - name: Automatically merge successful Scala Steward PRs
        |    conditions:
        |      - or:
        |          - author=scala-steward
        |          - author=renovate[bot]
        |      - check-success=Build (ubuntu-latest, 2.13.9, temurin@11)
        |      - check-success=Build (ubuntu-latest, 3.2.0, temurin@11)
        |    actions:
        |        queue: {}
        |""".stripMargin,
      WriteMergify.buildMergify(conditions).toYaml
    )
  }
}
