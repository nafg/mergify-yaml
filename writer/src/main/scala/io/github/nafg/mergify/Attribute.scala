package io.github.nafg.mergify


sealed abstract class Attribute[A](val name: String)

object Attribute {
  /** The list of GitHub user or team login that are assigned to the pull request.
   * Team logins are prefixed with the @ character and must belong to the repository organization. */
  case object Assignee extends Attribute[Seq[String]]("assignee")

  /** The list of GitHub user or team login that approved the pull request.
   * Team logins are prefixed with the @ character and must belong to the repository organization.
   * This only matches reviewers with admin, write or maintain permission on the repository. */
  case object ApprovedReviewsBy extends Attribute[Seq[String]]("approved-reviews-by")

  /** The GitHub user or team login of the author of the pull request.
   * Team logins are prefixed with the @ character and must belong to the repository organization. */
  case object Author extends Attribute[String]("author")

  /** The name of the branch the pull request should be pulled into. */
  case object Base extends Attribute[String]("base")

  /** The contents of the pull request. */
  case object Body extends Attribute[String]("body")

  /** The list of GitHub user or team login that have requested changes in a review for the pull request.
   * Team logins are prefixed with the @ character and must belong to the repository organization.
   * This only matches reviewers with admin, write or maintain permission on the repository. */
  case object ChangesRequestedReviewsBy extends Attribute[Seq[String]]("Changes-Requested-Reviews-By")

  /** Whether the pull request is closed. */
  case object Closed extends Attribute[Boolean]("closed")

  /** Whether the pull request is conflicting with its base branch. */
  case object Conflict extends Attribute[Boolean]("conflict")

  /** The list of GitHub user or team login that have commented in a review for the pull request.
   * Team logins are prefixed with the @ character and must belong to the repository organization.
   * This only matches reviewers with admin, write or maintain permission on the repository. */
  case object CommentedReviewsBy extends Attribute[Seq[String]]("commented-reviews-by")

  /** The list of GitHub user or team login that have their review dismissed in the pull request.
   * Team logins are prefixed with the @ character and must belong to the repository organization.
   * This only matches reviewers with admin, write or maintain permission on the repository. */
  case object DismissedReviewsBy extends Attribute[Seq[String]]("dismissed-reviews-by")

  /** Whether the pull request is in draft state. */
  case object Draft extends Attribute[Boolean]("draft")

  /** The files that are modified, deleted or added by the pull request. */
  case object Files extends Attribute[Seq[String]]("files")

  /** The name of the branch where the pull request changes are implemented. */
  case object Head extends Attribute[String]("head")

  /** The list of labels of the pull request. */
  case object Label extends Attribute[Seq[String]]("label")

  /** Whether the pull request is locked. */
  case object Locked extends Attribute[Boolean]("locked")

  /** Whether the pull request is merged. */
  case object Merged extends Attribute[Boolean]("merged")

  /** The GitHub user or team login that merged the pull request.
   * Team logins are prefixed with the @ character and must belong to the repository organization. */
  case object MergedBy extends Attribute[String]("merged-by")

  /** The milestone title associated to the pull request. */
  case object Milestone extends Attribute[String]("milestone")

  /** The pull request number. */
  case object Number extends Attribute[Int]("number")

  /** The list of GitHub user or team login that were requested to review the pull request.
   * Team logins are prefixed with the @ character and must belong to the repository organization.
   * This only matches reviewers with admin, write or maintain permission on the repository. */
  case object ReviewRequested extends Attribute[Seq[String]]("review-requested")

  /** The list of status checks that successfully passed for the pull request.
   * This is the name of a status check such as continuous-integration/travis-ci/pr
   * or of a check run such as Travis CI - Pull Request. See About Status Checks for more details. */
  case object CheckSuccess extends Attribute[Seq[String]]("check-success")

  /** The list of status checks that are neutral for the pull request.
   * This is the name of a status check such as continuous-integration/travis-ci/pr
   * or of a check run such as Travis CI - Pull Request. See About Status Checks for more details. */
  case object CheckNeutral extends Attribute[Seq[String]]("check-neutral")

  /** The list of status checks that failed for the pull request.
   * This is the name of a status check such as continuous-integration/travis-ci/pr
   * or of a check run such as Travis CI - Pull Request. See About Status Checks for more details. */
  case object CheckFailure extends Attribute[Seq[String]]("check-failure")

  /** The title of the pull request. */
  case object Title extends Attribute[String]("title")
}
