# Git Changelog Lib [![Build Status](https://travis-ci.org/tomasbjerre/git-changelog-lib.svg?branch=master)](https://travis-ci.org/tomasbjerre/git-changelog-lib) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.bjurr.gitchangelog/git-changelog-lib/badge.svg)](https://maven-badges.herokuapp.com/maven-central/se.bjurr.gitchangelog/git-changelog-lib)

This is a library for generating a changelog, or releasenotes, from a GIT repository. It can also be run as a standalone program, Gradle plugin, Maven plugin or Jenkins plugin.

It is fully configurable with a [Mustache](http://mustache.github.io/) template. That can:

 * Be stored to file, like CHANGELOG.md. There are some templates used for testing available [here](https://github.com/tomasbjerre/git-changelog/tree/master/src/test/resources/templates) and the results [here](https://github.com/tomasbjerre/git-changelog/tree/master/src/test/resources/assertions).
 * Be posted to MediaWiki ([here](https://github.com/tomasbjerre/git-changelog-lib/tree/screenshots/sandbox) is an example)
 * Or just be printed to STDOUT

It can integrate with Jira and/or GitHub to retrieve the title of issues.

The [changelog](https://github.com/tomasbjerre/git-changelog-lib/blob/master/CHANGELOG.md) of this project is automatically generated with [this template](https://github.com/tomasbjerre/git-changelog-lib/blob/master/changelog.mustache).

There are some screenshots [here](https://github.com/tomasbjerre/git-changelog-lib/tree/screenshots/sandbox).

## Usage
This software can be used:
 * With a [Gradle plugin](https://github.com/tomasbjerre/git-changelog-gradle-plugin).
 * With a [Maven plugin](https://github.com/tomasbjerre/git-changelog-maven-plugin).
 * With a [Jenkins plugin](https://github.com/jenkinsci/git-changelog-plugin).
 * With a [Bitbucket Server plugin](https://github.com/tomasbjerre/git-changelog-bitbucket-plugin).
 * From [command line](https://github.com/tomasbjerre/git-changelog-command-line).
 * As a library [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22git-changelog-lib%22).

Here is an example template. There are more examples [here](https://github.com/tomasbjerre/git-changelog/tree/master/src/test/resources/templates).
```
# Git Changelog changelog

Changelog of Git Changelog.
# Git Changelog changelog

Changelog of Git Changelog.

{{#tags}}
## {{name}}
 {{#issues}}
  {{#hasIssue}}
   {{#hasLink}}
### {{name}} [{{issue}}]({{link}}) {{title}}
   {{/hasLink}}
   {{^hasLink}}
### {{name}} {{issue}} {{title}}
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

[{{hash}}](https://github.com/tomasbjerre/git-changelog-lib/commit/{{hash}}) {{authorName}} *{{commitTime}}*

  {{/commits}}

 {{/issues}}
{{/tags}}
```

## Supplied information

The template is supplied with a datastructure like:
```
* commits
 - authorName
 - authorEmailAddress
 - commitTime
 - message (The full message)
 - messageTitle (Only the first line of the message)
 - messageBody (Everything, except the title)
 * messageBodyItems (List of strings, the lines after the title)
* tags
 - name
 * commits
  - authorName
  - authorEmailAddress
  - commitTime
  - message (The full message)
  - messageTitle (Only the first line of the message)
  - messageBody (Everything, except the title)
  * messageBodyItems (List of strings, the lines after the title)
 * authors
  - authorName
  - authrorEmail
  * commits
   - authorName
   - authorEmailAddress
   - commitTime
   - message (The full message)
   - messageTitle (Only the first line of the message)
   - messageBody (Everything, except the title)
   * messageBodyItems (List of strings, the lines after the title)
 * issueTypes
  - name (Like GitHub, Jira, ...)
  * issues
   - name
   - hasIssue
   - issue
   - hasLink
   - link
   - hasTitle
   - title
   * commits
    - authorName
    - authorEmailAddress
    - commitTime
    - message (The full message)
    - messageTitle (Only the first line of the message)
    - messageBody (Everything, except the title)
    * messageBodyItems (List of strings, the lines after the title)
   * authors
    - authorName
    - authrorEmail
    * commits
     - authorName
     - authorEmailAddress
     - commitTime
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
  * commits
   - authorName
   - authorEmailAddress
   - commitTime
   - message (The full message)
   - messageTitle (Only the first line of the message)
   - messageBody (Everything, except the title)
   * messageBodyItems (List of strings, the lines after the title)
  * authors
   - authorName
   - authrorEmail
   * commits
    - authorName
    - authorEmailAddress
    - commitTime
    - message (The full message)
    - messageTitle (Only the first line of the message)
    - messageBody (Everything, except the title)
    * messageBodyItems (List of strings, the lines after the title)
* authors
 - authorName
 - authrorEmail
 * commits
  - authorName
  - authorEmailAddress
  - commitTime
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
 * commits
  - authorName
  - authorEmailAddress
  - commitTime
  - message (The full message)
  - messageTitle (Only the first line of the message)
  - messageBody (Everything, except the title)
  * messageBodyItems (List of strings, the lines after the title)
 * authors
  - authorName
  - authrorEmail
  * commits
   - authorName
   - authorEmailAddress
   - commitTime
   - message (The full message)
   - messageTitle (Only the first line of the message)
   - messageBody (Everything, except the title)
   * messageBodyItems (List of strings, the lines after the title)
```

## Library

It has a [builder](https://github.com/tomasbjerre/git-changelog/blob/master/src/main/java/se/bjurr/gitchangelog/api/GitChangelogApi.java) for creating the changelog.

```
  gitChangelogApiBuilder()
   .withFromCommit(ZERO_COMMIT)
   .withToRef("refs/heads/master")
   .withTemplatePath("changelog.mustache")
   .toFile("CHANGELOG.md");
```

It can also create releasenotes. If you are using git flow it may look like this.

```
  gitChangelogApiBuilder()
   .withFromRef("refs/heads/dev")
   .withToRef("refs/heads/master")
   .withTemplatePath("releasenotes.mustache")
   .toStdout();
```
A page can be created in MediaWiki like this.

```
 .toMediaWiki(
  "username",
  "password",
  "http://host/mediawiki",
  "Title of page");
```

Settings can be supplied with a JSON config ([documented here](https://github.com/tomasbjerre/git-changelog/blob/master/src/main/java/se/bjurr/gitchangelog/internal/settings/Settings.java)).

## MediaWiki
The library can create a wiki page in MediaWiki. To do this, you must enable the API in MediaWiki in `mediawiki/LocalSettings.php` by adding:
```
$wgEnableAPI = true;
$wgEnableWriteAPI = true;
```

## Developer instructions

To build the code, have a look at `.travis.yml`.

To do a release you need to do `./gradlew release` and release the artifact from [staging](https://oss.sonatype.org/#stagingRepositories). More information [here](http://central.sonatype.org/pages/releasing-the-deployment.html).
