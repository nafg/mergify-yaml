// GENERATED CODE: DO NOT EDIT THIS FILE MANUALLY.
// TO UPDATE, RUN `sbt generateModels`
// SEE `project/ScrapeActions.scala`

package io.github.nafg.mergify.models.generated

import io.github.nafg.mergify.ToJson


sealed trait Action
object Action {
  /** Assign a pull request to a user.
    */
  case class Assign(
  ) extends Action


  /** Copy a pull request to another branch once it is merged.
    */
  case class Backport(
    /** Users to assign the newly created pull request to. As the type is a data type template, you could use, e.g.,
      * {{author}} to assign the pull request to its original author.
      */
    assignees: Seq[String],
    /** The pull request's body.
  Default: |
    {{ body }} This is an automatic backport of pull request #{{number}} done
      * by [Mergify](https://mergify.com).
      */
    body: Option[String] = None,
    /** Mergify can impersonate a GitHub user to copy a pull request. If no bot_account is set, Mergify copies the pull
      * request itself.
  Default: null
      */
    botAccount: Option[ToJson /*template or null*/] = None,
    /** The list of branches the pull request should be copied to.
      */
    branches: Seq[ToJson /*Branch Name*/],
    /** Whether to create the pull requests even if there are conflicts when cherry-picking the commits.
  Default: true
      */
    ignoreConflicts: Option[Boolean] = None,
    /** The label to add to the created pull request if it has conflicts and ignore_conflicts is set to true.
  Default:
      * conflicts
      */
    labelConflicts: Option[String] = None,
    /** The list of labels to add to the created pull requests.
      */
    labels: Seq[String],
    /** Style used by git when displaying merge conflicts
  Default: merge
      */
    mergeConflictStyle: Option[ToJson /*merge or diff3*/] = None,
    /** The list of regexes to find branches the pull request should be copied to.
      */
    regexes: ToJson /*list of*/,
    /** Reporting modes for the action's result. Check will create a check on the pull request, and comment will post a
      * comment on the pull request.
  Default: - check
      */
    reportMode: Option[Seq[ToJson /*check or comment*/]] = None,
    /** The pull request's title.
  Default: "{{ title }} (backport #{{ number }})"
      */
    title: Option[String] = None
  ) extends Action


  /** Close a pull request.
    */
  case class Close(
    /** A string template using the Jinja2 syntax.
  Default: This pull request has been automatically closed by Mergify.
      */
    message: Option[String] = None
  ) extends Action


  /** Copy a pull request to another branch.
    */
  case class Copy(
    /** Users to assign the newly created pull request to. As the type is a data type template, you could use, e.g.,
      * {{author}} to assign the pull request to its original author.
      */
    assignees: Seq[String],
    /** The pull request's body.
  Default: |
    {{ body }} This is an automatic copy of pull request #{{number}} done by
      * [Mergify](https://mergify.com).
      */
    body: Option[String] = None,
    /** Mergify can impersonate a GitHub user to copy a pull request. If no bot_account is set, Mergify copies the pull
      * request itself.
  Default: null
      */
    botAccount: Option[ToJson /*template or null*/] = None,
    /** The list of branches the pull request should be copied to.
      */
    branches: Seq[ToJson /*Branch Name*/],
    /** Whether to create the pull requests even if there are conflicts when cherry-picking the commits.
  Default: true
      */
    ignoreConflicts: Option[Boolean] = None,
    /** The label to add to the created pull request if it has conflicts and ignore_conflicts is set to true.
  Default:
      * conflicts
      */
    labelConflicts: Option[String] = None,
    /** The list of labels to add to the created pull requests.
      */
    labels: Seq[String],
    /** Style used by git when displaying merge conflicts
  Default: merge
      */
    mergeConflictStyle: Option[ToJson /*merge or diff3*/] = None,
    /** The list of regexes to find branches the pull request should be copied to.
      */
    regexes: ToJson /*list of*/,
    /** Reporting modes for the action's result. Check will create a check on the pull request, and comment will post a
      * comment on the pull request.
  Default: - check
      */
    reportMode: Option[Seq[ToJson /*check or comment*/]] = None,
    /** The pull request's title.
  Default: "{{ title }} (copy #{{ number }})"
      */
    title: Option[String] = None
  ) extends Action


  /** Comment a pull request.
    */
  case class Comment(
    /** Mergify can impersonate a GitHub user to comment a pull request. If no bot_account is set, Mergify will comment
      * the pull request itself.
      */
    botAccount: ToJson /*template or null*/,
    /** The message to write as a comment.
      */
    message: ToJson /*template or null*/
  ) extends Action


  /** Delete pull request head branch. Useful to clean pull requests once closed.
    */
  case class DeleteHeadBranch(
    /** If set to true, the branch will be deleted even if another pull request depends on the head branch. GitHub will
      * therefore close the dependent pull requests.
      */
    force: Boolean
  ) extends Action


  /** Dismiss previous reviews on a pull request.
    */
  case class DismissReviews(
    /** If set to true, all the approving reviews will be removed when the pull request is updated. If set to false,
      * nothing will be done. If set to a list, each item should be the GitHub login of a user whose review will be
      * removed. If set to from_requested_reviewers, the list of requested reviewers will be used to get whose review
      * will be removed.
  Default: true
      */
    approved: Option[Seq[ToJson /*string, boolean or from_requested_reviewers*/]] = None,
    /** If set to true, all the reviews requesting changes will be removed when the pull request is updated. If set to
      * false, nothing will be done. If set to a list, each item should be the GitHub login of a user whose review will
      * be removed. If set to from_requested_reviewers, the list of requested reviewers will be used to get whose review
      * will be removed.
  Default: true
      */
    changesRequested: Option[Seq[ToJson /*string, boolean or from_requested_reviewers*/]] = None,
    /** Message to use when dismissing reviews.
  Default: null
      */
    message: Option[ToJson /*template or null*/] = None,
    /** If set to synchronize, the action will run only if the pull request commits changed. Otherwise, it will run each
      * time the rule matches.
  Default: synchronize
      */
    when: Option[ToJson /*synchronize or always*/] = None
  ) extends Action


  /** Edit pull request attributes.
    */
  case class Edit(
  ) extends Action


  /** Dispatch an existing GitHub workflow in the repository.
    */
  case class GitHubActions(
  ) extends Action


  /** Add, remove and toggle labels on a pull request.
    */
  case class Label(
    /** The list of labels to add.
      */
    add: Seq[String],
    /** The list of labels to remove.
      */
    remove: Seq[String],
    /** Remove all labels from the pull request.
      */
    removeAll: Boolean,
    /** Toggle labels in the list based on the conditions. If all the conditions are a success, all the labels in the
      * list will be added, otherwise, they will all be removed.
      */
    toggle: Seq[String]
  ) extends Action


  /** Automate the merging of your pull requests.
    */
  case class Merge(
    /** Template to use as the commit message when using the merge or squash merge method.
      */
    commitMessageTemplate: ToJson /*template or null*/,
    /** Mergify can impersonate a GitHub user to merge pull requests. If no merge_bot_account is set, Mergify merges the
      * pull request itself. The user account must have already been logged in Mergify dashboard once and have write or
      * maintain permission.
      */
    mergeBotAccount: ToJson /*template or null*/,
    /** Merge method to use. If no value is set, Mergify uses the first authorized method available in the repository
      * configuration.
      */
    method: ToJson /*merge, rebase, squash or fast-forward or null*/
  ) extends Action


  /** Create a check-run on a pull request.
    */
  case class PostCheck(
    /** List of conditions to match to mark the pull request check as neutral, otherwise, it will be marked as
      * failing.
  Default: null
      */
    neutralConditions: Option[ToJson /*List of conditions or null*/] = None,
    /** List of conditions to match to mark the pull request check as succeeded, otherwise, it will be marked as
      * failing. If unset, the conditions from the rule that triggers this action are used.
  Default: null
      */
    successConditions: Option[ToJson /*List of conditions or null*/] = None,
    /** The summary of the check.
  Default: "{{ check_conditions }}"
      */
    summary: Option[String] = None,
    /** The title of the check.
  Default: "'{{ check_rule_name }}'{% if check_status == 'success' %} succeeded{% elif
      * check_status == 'failure' %} failed{% endif %}"
      */
    title: Option[String] = None
  ) extends Action


  /** Put your pull request into the merge queue.
    */
  case class Queue(
    /** Allow merging the Mergify configuration file. This option does not do anything and is only present for backward
      * compatibility.
  Default: true
      */
    allowMergingConfigurationChange: Option[Boolean] = None,
    /** Template to use as the commit message when using the merge or squash merge method.
  Default: null
      */
    commitMessageTemplate: Option[ToJson /*template or null*/] = None,
    /** Mergify can impersonate a GitHub user to merge pull requests. If no merge_bot_account is set, Mergify merges the
      * pull request itself. The user account must have already been logged in Mergify dashboard once and have write or
      * maintain permission.
  Default: null
      */
    mergeBotAccount: Option[ToJson /*template or null*/] = None,
    /** Merge method to use. If no value is set, Mergify uses the first authorized method available in the repository
      * configuration. fast-forward is not supported on queues with max_parallel_checks > 1, batch_size > 1, or with
      * allow_inplace_checksset tofalse`.
  Default: null
      */
    mergeMethod: Option[ToJson /*merge, rebase, squash or fast-forward or null*/] = None,
    /** The name of the queue where the pull request should be added. If no name is set, queue_conditions will be
      * applied instead.
  Default: null
      */
    name: Option[ToJson /*string or null*/] = None,
    /** For certain actions, such as rebasing branches, Mergify has to impersonate a GitHub user. You can specify the
      * account to use with this option. If no update_bot_account is set, Mergify picks randomly one of the organization
      * users instead. The user account must have already been logged in Mergify dashboard once. This option overrides
      * the value defined in the queue rules section of the configuration.
  Default: null
      */
    updateBotAccount: Option[ToJson /*template or null*/] = None,
    /** Method to use to update the pull request with its base branch when the check is done in place. Possible values:
      * merge to merge the base branch into the pull request. rebase to rebase the pull request against its base branch.
      * This option overrides the value defined in the queue rules section of the configuration. The default is rebase
      * when the merge_method is fast-forward.
  Default: null
      */
    updateMethod: Option[ToJson /*merge or rebase or null*/] = None
  ) extends Action


  /** Rebase the pull request on top of its base branch.
    */
  case class Rebase(
    /** When set to true, commits starting with fixup!, squash! and amend! are squashed during the rebase.
  Default: true
      */
    autosquash: Option[Boolean] = None,
    /** To rebase, Mergify needs to impersonate a GitHub user. You can specify the account to use with this option. If
      * no bot_account is set, Mergify picks the pull request author. The user account must have already been logged in
      * Mergify dashboard once.
  Default: null
      */
    botAccount: Option[ToJson /*template or null*/] = None
  ) extends Action


  /** Request reviews from specific users or teams.
    */
  case class RequestReviews(
    /** Mergify can impersonate a GitHub user to request a review on a pull request. If no bot_account is set, Mergify
      * will request the review itself.
      */
    botAccount: ToJson /*template or null*/,
    /** Pick random users and teams from the provided lists. When random_count is specified, users and teams can be a
      * dictionary where the key is the login and the value is the weight to use. Weight must be between 1 and 15
      * included.
      */
    randomCount: ToJson /*integer or null*/,
    /** The team names to request reviews from.
      */
    teams: Seq[ToJson /*string or object*/],
    /** The usernames to request reviews from.
      */
    users: Seq[ToJson /*string or object*/],
    /** The team names to get the list of users to request reviews from.
      */
    usersFromTeams: Seq[ToJson /*string or object*/]
  ) extends Action


  /** Automate reviews for your pull requests with customizable comments and review types.
    */
  case class Review(
    /** Mergify can impersonate a GitHub user to review a pull request. If no bot_account is set, Mergify will review
      * the pull request itself.
  Default: null
      */
    botAccount: Option[ToJson /*template or null*/] = None,
    /** The message to post in the review
  Default: null
      */
    message: Option[ToJson /*template or null*/] = None,
    /** The type of review to post
  Default: APPROVE
      */
    `type`: Option[ToJson /*APPROVE, REQUEST_CHANGES or COMMENT*/] = None
  ) extends Action


  /** Update the pull request branch with its base branch.
    */
  case class Update(
    /** Mergify can impersonate a GitHub user to update a pull request. If no bot_account is set, Mergify will update
      * the pull request itself.
      */
    botAccount: ToJson /*template or null*/
  ) extends Action


  /** Squash commits in the pull request.
    */
  case class Squash(
    /** Mergify can impersonate a GitHub user to squash a pull request. If no bot_account is set, Mergify will squash
      * the pull request itself
  Default: null
      */
    botAccount: Option[ToJson /*template or null*/] = None,
    /** Defines what commit message to use for the squashed commit if no commit message is defined in the pull request
      * body. Possible values are: all-commits to use the same format as GitHub squashed merge commit. first-commit to
      * use the message of the first commit of the pull request. title+body means to use the title and body from the
      * pull request itself as the commit message. The pull request number will be added to end of the title.
  Default:
      * all-commits
      */
    commitMessage: Option[ToJson /*all-commits, first-commit or title+body*/] = None
  ) extends Action}
