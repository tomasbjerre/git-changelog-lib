# Git Changelog Lib [![Build Status](https://travis-ci.org/tomasbjerre/git-changelog-lib.svg?branch=master)](https://travis-ci.org/tomasbjerre/git-changelog-lib) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.bjurr.gitchangelog/git-changelog-lib/badge.svg)](https://maven-badges.herokuapp.com/maven-central/se.bjurr.gitchangelog/git-changelog-lib) [ ![Bintray](https://api.bintray.com/packages/tomasbjerre/tomasbjerre/se.bjurr.gitchangelog%3Agit-changelog-lib/images/download.svg) ](https://bintray.com/tomasbjerre/tomasbjerre/se.bjurr.gitchangelog%3Agit-changelog-lib/_latestVersion)

This is a library for generating a changelog, or releasenotes, from a GIT repository. It can also be run as a standalone program, Gradle plugin, Maven plugin or Jenkins plugin.

It is fully configurable with a [Mustache](http://mustache.github.io/) template. That can:

 * Be stored to file, like `CHANGELOG.md`. There are some templates used for testing available [here](/src/test/resources/templatetest).
 * Or just rendered to a `String`.

It can integrate with Jira and/or GitHub to retrieve the title of issues.

The [changelog](/CHANGELOG.md) of this project is automatically generated with [this template](/changelog.mustache).

## Usage
This software can be used:
 * With a [Gradle plugin](https://github.com/tomasbjerre/git-changelog-gradle-plugin).
 * With a [Maven plugin](https://github.com/tomasbjerre/git-changelog-maven-plugin).
 * With a [Jenkins plugin](https://github.com/jenkinsci/git-changelog-plugin).
 * From [command line](https://github.com/tomasbjerre/git-changelog-command-line).
 * As a library [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22git-changelog-lib%22).

Here is an example template. 
```hbs
# Changelog

Changelog for {{ownerName}} {{repoName}}.

{{#tags}}
## {{name}}
 {{#issues}}
  {{#hasIssue}}
   {{#hasLink}}
### {{name}} [{{issue}}]({{link}}) {{title}} {{#hasIssueType}} *{{issueType}}* {{/hasIssueType}} {{#hasLabels}} {{#labels}} *{{.}}* {{/labels}} {{/hasLabels}}
   {{/hasLink}}
   {{^hasLink}}
### {{name}} {{issue}} {{title}} {{#hasIssueType}} *{{issueType}}* {{/hasIssueType}} {{#hasLabels}} {{#labels}} *{{.}}* {{/labels}} {{/hasLabels}}
   {{/hasLink}}
  {{/hasIssue}}
  {{^hasIssue}}
### {{name}}
  {{/hasIssue}}

  {{#commits}}
**{{{messageTitle}}}**

{{#messageBodyItems}}
 * {{.}} 
{{/messageBodyItems}}

[{{hash}}](https://github.com/{{ownerName}}/{{repoName}}/commit/{{hash}}) {{authorName}} *{{commitTime}}*

  {{/commits}}

 {{/issues}}
{{/tags}}
```

There are some examples [here](/examples) that are ready to use.

There are also different variations [here](/src/test/resources/templates) that are used for testing.

## Context

The template is supplied with this context:

<details><summary>Click here to show context</summary>
<p>

```
- ownerName (Derived from the clone URL, for this repo it would be "tomasbjerre")
- repoName (Derived from the clone URL, for this repo it would be "git-changelog-lib")
* commits
 - authorName
 - authorEmailAddress
 - commitTime
 - hash
 - hashFull
 - merge (True if this is a merge-commit)
 - message (The full message)
 - messageTitle (Only the first line of the message)
 - messageBody (Everything, except the title)
 * messageBodyItems (List of strings, the lines after the title)
* tags
 - name
 - annotation
 - tagTime
 - hasTagTime
 * commits
  - authorName
  - authorEmailAddress
  - commitTime
  - hash
  - hashFull
  - merge (True if this is a merge-commit)
  - message (The full message)
  - messageTitle (Only the first line of the message)
  - messageBody (Everything, except the title)
  * messageBodyItems (List of strings, the lines after the title)
 * authors
  - authorName
  - authorEmail
  * commits
   - authorName
   - authorEmailAddress
   - commitTime
   - hash
   - hashFull
   - merge (True if this is a merge-commit)
   - message (The full message)
   - messageTitle (Only the first line of the message)
   - messageBody (Everything, except the title)
   * messageBodyItems (List of strings, the lines after the title)
 * issueTypes
  - name (Like GitHub, GitLab, Jira, ...)
  * issues
   - name
   - hasIssue
   - issue
   - hasLink
   - link
   - hasTitle
   - title
   - hasDescription
   - description
   - hasType
   - type
   - isJira
   - isGitHub
   - isGitLab
   - isCustom
   - isNoIssue
   - hasLabels
   - labels
   - hasLinkedIssues
   - linkedIssues
   * commits
    - authorName
    - authorEmailAddress
    - commitTime
    - hash
    - hashFull
    - merge (True if this is a merge-commit)
    - message (The full message)
    - messageTitle (Only the first line of the message)
    - messageBody (Everything, except the title)
    * messageBodyItems (List of strings, the lines after the title)
   * authors
    - authorName
    - authorEmail
    * commits
     - authorName
     - authorEmailAddress
     - commitTime
     - hash
     - hashFull
     - merge (True if this is a merge-commit)
     - message (The full message)
     - messageTitle (Only the first line of the message)
     - messageBody (Everything, except the title)
     * messageBodyItems (List of strings, the lines after the title)
 * issues
  - name
  - hasIssue
  - issue
  - hasLink
  - link
  - hasTitle
  - title
  - hasDescription
  - description
  - hasType
  - type
  - isJira
  - isGitHub
  - isGitLab
  - isCustom
  - isNoIssue
  - hasLabels
  - labels
  - hasLinkedIssues
  - linkedIssues
  * commits
   - authorName
   - authorEmailAddress
   - commitTime
   - hash
   - hashFull
   - merge (True if this is a merge-commit)
   - message (The full message)
   - messageTitle (Only the first line of the message)
   - messageBody (Everything, except the title)
   * messageBodyItems (List of strings, the lines after the title)
  * authors
   - authorName
   - authorEmail
   * commits
    - authorName
    - authorEmailAddress
    - commitTime
    - hash
    - hashFull
    - merge (True if this is a merge-commit)
    - message (The full message)
    - messageTitle (Only the first line of the message)
    - messageBody (Everything, except the title)
    * messageBodyItems (List of strings, the lines after the title)
* authors
 - authorName
 - authorEmail
 * commits
  - authorName
  - authorEmailAddress
  - commitTime
  - hash
  - hashFull
  - merge (True if this is a merge-commit)
  - message (The full message)
  - messageTitle (Only the first line of the message)
  - messageBody (Everything, except the title)
  * messageBodyItems (List of strings, the lines after the title)
* issues
 - name
 - hasIssue
 - issue
 - hasLink
 - link
 - hasTitle
 - title
 - hasDescription
 - description
 - hasType
 - type
 - isJira
 - isGitHub
 - isGitLab
 - isCustom
 - isNoIssue
 - hasLabels
 - labels
 - hasLinkedIssues
 - linkedIssues
 * commits
  - authorName
  - authorEmailAddress
  - commitTime
  - hash
  - hashFull
  - merge (True if this is a merge-commit)
  - message (The full message)
  - messageTitle (Only the first line of the message)
  - messageBody (Everything, except the title)
  * messageBodyItems (List of strings, the lines after the title)
 * authors
  - authorName
  - authorEmail
  * commits
   - authorName
   - authorEmailAddress
   - commitTime
   - hash
   - hashFull
   - merge (True if this is a merge-commit)
   - message (The full message)
   - messageTitle (Only the first line of the message)
   - messageBody (Everything, except the title)
   * messageBodyItems (List of strings, the lines after the title)
```

</p>
</details>

## Library

It has a [builder](/src/main/java/se/bjurr/gitchangelog/api/GitChangelogApi.java) for creating the changelog.

```java
  gitChangelogApiBuilder()
   .withFromCommit(ZERO_COMMIT)
   .withToRef("refs/heads/master")
   .withTemplatePath("changelog.mustache")
   .toFile("CHANGELOG.md");
```

It can also create releasenotes. If you are using git flow it may look like this.

```java
  gitChangelogApiBuilder()
   .withFromRef("refs/heads/dev")
   .withToRef("refs/heads/master")
   .withTemplatePath("releasenotes.mustache")
   .toStdout();
```

Settings can be supplied with a JSON config ([documented here](/src/main/java/se/bjurr/gitchangelog/internal/settings/Settings.java)).

