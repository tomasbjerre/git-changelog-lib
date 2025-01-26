# Git Changelog Lib

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.bjurr.gitchangelog/git-changelog-lib/badge.svg)](https://maven-badges.herokuapp.com/maven-central/se.bjurr.gitchangelog/git-changelog-lib)

This is a library that can:

- Generate a changelog, or releasenotes, from a GIT repository.
- Determine next version, based on format of commits since last release.

It is fully configurable with a [Mustache (Handlebars)](https://github.com/jknack/handlebars.java) template filled with a [context of placeholders](#Context) and [helpers](#Helpers).

The changelog can:

- Be stored to file, like `CHANGELOG.md`. There are some templates used for testing available [here](/src/test/resources/templatetest).
- Or, just rendered to a `String`.

It can integrate with Jira, Redmine, GitLab and/or GitHub to retrieve the title of issues.

| Version                  | Java Version |
| ------------------------ | ------------ |
| version < 2.0.0          | 8            |
| 2.0.0 <= version < 2.2.0 | 11           |
| 2.2.0 <= version         | 17           |

## Usage

This software can be used with:

- A [Gradle plugin](https://github.com/tomasbjerre/git-changelog-gradle-plugin).
- A [Maven plugin](https://github.com/tomasbjerre/git-changelog-maven-plugin).
- A [Jenkins plugin](https://github.com/jenkinsci/git-changelog-plugin).
- A [command line tool](https://github.com/tomasbjerre/git-changelog-command-line).
- A [Docker image](https://hub.docker.com/r/tomasbjerre/git-changelog-command-line).
- A [GitHub Action](https://github.com/tomasbjerre/git-changelog-github-release)
- A [library](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22git-changelog-lib%22).

There are examples of different templates [in the code](/src/test/resources) that are used for testing.

### Template - Simple

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

### Template - Conventional

If you are using [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/):

```shell
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

You can use built in [helpers](#helpers) to produce a nice changelog. You can add your own helpers (using Javascript or Java) as described [here](https://github.com/jknack/handlebars.java).

```hbs
{{#tags}}
{{#ifReleaseTag .}}
## [{{name}}](https://gitlab.com/html-validate/html-validate/compare/{{name}}) ({{tagDate .}})

  {{#ifContainsBreaking commits}}
    ### Breaking changes

    {{#commits}}
      {{#ifCommitBreaking .}}
  - {{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}} {{{commitDescription .}}} ([{{hash}}](https://github.com/{{ownerName}}/{{repoName}}/commit/{{hash}}))
      {{/ifCommitBreaking}}
    {{/commits}}
  {{/ifContainsBreaking}}


  {{#ifContainsType commits type='feat'}}
    ### Features

    {{#commits}}
      {{#ifCommitType . type='feat'}}
  - {{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}} {{{commitDescription .}}} ([{{hash}}](https://github.com/{{ownerName}}/{{repoName}}/commit/{{hash}}))
      {{/ifCommitType}}
    {{/commits}}
  {{/ifContainsType}}


  {{#ifContainsType commits type='fix'}}
    ### Bug Fixes

    {{#commits}}
      {{#ifCommitType . type='fix'}}
        - {{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}} {{{commitDescription .}}} ([{{hash}}](https://github.com/{{ownerName}}/{{repoName}}/commit/{{hash}}))
      {{/ifCommitType}}
    {{/commits}}
  {{/ifContainsType}}

{{/ifReleaseTag}}
{{/tags}}
```

### Partials

You can use [partials](http://jknack.github.io/handlebars.java/reuse.html) in your templates.

`changelog.hbs`

```hbs
{{#commits}}
{{> commit}}
{{/commits}}
```

`commit.partial`

```hbs
## {{authorName}} - {{commitTime}}
[{{hashFull}}](https://server/{{hash}})
{{{message}}}
```

This is configured like:

```java
gitChangelogApi
  .withTemplateBaseDir("...")
  .withTemplateSuffix(".partial"); //Optional, defaults to ".partial"
```

## Helpers

Some [helpers](/src/main/java/se/bjurr/gitchangelog/api/helpers) are implemented in this library. And users can also add more helpers as described in [Handlebars](https://github.com/jknack/handlebars.java). If you add your own helpers, using Javascript, you will need to add a scriptengine like [Nashorn](https://central.sonatype.com/artifact/org.openjdk.nashorn/nashorn-core/overview) to your classpath.

### `eachUrlPart <Changelog>`

Loop each part of the URL.

```hbs
https://gitlab.com/{{#eachUrlPart .}}{{#if @first}}{{else}}{{.}}/{{/if}}{{/eachUrlPart}}
```

### `ifReleaseTag <Tag>`

Conditional, renders a block if given `Tag` matches release-tag.

```hbs
{{#tags}}
 {{#ifReleaseTag .}}
  "{{.}}" is a release tag
 {{/ifReleaseTag}}
{{/tags}}
```

### `tagDate <Tag>`

Renders date of `Tag` on format `YYYY-MM-DD`.

```hbs
{{#tags}}
 {{tagDate .}}
{{/tags}}
```

### `ifContainsIssueType <List<Issue>>`

Conditional, renders a block if given `List<Issue>` contains given `type`.

```hbs
{{#ifContainsIssueType issues type="Bug"}}
  issues contains bugs
{{/ifContainsIssueType}}
```

### `ifContainsIssueTypeOtherThan <List<Issue>>`

Conditional, renders a block if given `List<Issue>` contains issues that don't match the given `type`.

```hbs
{{#ifContainsIssueTypeOtherThan issues type="fix"}}
  commits contains other types than fix
{{/ifContainsIssueTypeOtherThan}}
```

### `ifContainsIssueLabel <List<Issue>> label="<value>"`

Conditional, renders a block if given `List<Issue>` contains given `label`.

```hbs
{{#ifContainsIssueLabel issues label='enhancement'}}
  content here
{{/ifContainsIssueLabel}}
```

### `ifContainsIssueLabelOtherThan <List<Issue>>`

Conditional, renders a block if given `List<Issue>` contains labels that don't match the given `label`.

```hbs
{{#ifContainsIssueLabelOtherThan issues label='enhancement'}}
  content here
{{/ifContainsIssueLabel}}
```

### `ifIssueLabel <label> label="<value>"`

Conditional, renders a block if given `label` matches the given `value`.

```hbs
{{#ifIssueLabel . label='enhancement'}}
  Found a enhancement
{{/ifIssueLabel}}
```

### `ifContainsType <List<Commit>>`

Conditional, renders a block if given `List<Commits>` contains given `type`.

```hbs
{{#ifContainsType commits type="fix"}}
  commits contains fixes
{{/ifContainsType}}
```

### `ifContainsScope <List<Commit>>`

Conditional, renders a block if given `List<Commits>` contains given `scope`.

```hbs
{{#ifContainsScope commits scope="deps"}}
  commits contains deps
{{/ifContainsScope}}
```

### `ifContainsTypeAndScope <List<Commit>>`

Conditional, renders a block if given `List<Commits>` contains given `type` and `scope`.

```hbs
{{#ifContainsTypeAndScope commits type="chore" scope="deps"}}
  commits contains chore with deps
{{/ifContainsScope}}
```

### `ifContainsTypeOtherThan <List<Commit>>`

Conditional, renders a block if given `List<Commits>` contains commits that don't match the given `type`.

```hbs
{{#ifContainsTypeOtherThan commits type="fix"}}
  commits contains other types than fix
{{/ifContainsTypeOtherThan}}
```

### `ifContainsBreaking <List<Commit>>`

Conditional, renders a block if given `List<Commits>` contains `breaking` changes.

```hbs
{{#ifContainsBreaking commits}}
  commits contains fixes
{{/ifContainsBreaking}}
```

### `commitDate <Commit>`

Renders date of `Commit` on format `YYYY-MM-DD`.

```hbs
{{#commits}}
 {{commitDate .}}
{{/commits}}
```

### `commitDescription <Commit>`

Renders description of `Commit`.

```hbs
{{#commits}}
 {{commitDescription .}}
{{/commits}}
```

### `revertedCommit <Commit>`

Renders reverted commit refered to by `Commit`.

```hbs
{{#commits}}
 {{revertedCommit .}}
{{/commits}}
```

### `ifIssueType <Issue> type="<type>"`

Conditional, renders a block if given `Issue` is of `type`.

```hbs
{{#issues}}
 {{#ifIssueType . type="fix"}} is type fix {{/ifIssueType}}
{{/issues}}
```

### `ifIssueTypeOtherThan <Issue> type="<type>"`

Conditional, renders a block if given `Issue` is of `type`.

```hbs
{{#issues}}
 {{#ifIssueTypeOtherThan . type="fix"}} is not type fix {{/ifIssueTypeOtherThan}}
{{/issues}}
```

### `ifCommitType <Commit> type="<type>"`

Conditional, renders a block if given `Commit` is of `type`.

```hbs
{{#commits}}
 {{#ifCommitType . type="fix"}} is type fix {{/ifCommitType}}
{{/commits}}
```

### `ifCommitTypeOtherThan <Commit> type="<type>"`

Conditional, renders a block if given `Commit` is of `type`.

```hbs
{{#commits}}
 {{#ifCommitTypeOtherThan . type="fix"}} is not type fix {{/ifCommitTypeOtherThan}}
{{/commits}}
```

### `ifCommitBreaking <Commit>`

Conditional, renders a block if given `Commit` is `breaking`.

```hbs
{{#commits}}
 {{#ifCommitBreaking .}} is breaking {{/ifCommitBreaking}}
{{/commits}}
```

### `ifCommitScope <Commit> scope="utils"`

Conditional, renders a block if given `Commit` has `scope`.

```hbs
{{#commits}}
 {{#ifCommitScope . scope="utils"}} is scope utils {{/ifCommitScope}}
{{/commits}}
```

### `ifCommitHasFooters <Commit>`

Conditional, renders a block if given `Commit` has `footers`.

```hbs
{{#commits}}
 {{#ifCommitHasFooters .}} has footers {{/ifCommitHasFooters}}
{{/commits}}
```

### `ifCommitHasParagraphs <Commit>`

Conditional, renders a block if given `Commit` has `paragraphs`.

```hbs
{{#commits}}
 {{#ifCommitHasParagraphs .}} has paragraphs {{/ifCommitHasParagraphs}}
{{/commits}}
```

### `eachCommitScope <Commit>`

Renders block for each `scope` in `Commit`.

```hbs
{{#commits}}
 {{#eachCommitScope .}}
  scope: {{.}}
 {{/eachCommitScope}}
{{/commits}}
```

### `eachCommitRefs <Commit>`

Renders block for each `refs` in `Commit`.

```hbs
{{#commits}}
 {{#eachCommitRefs .}}
  references issue: {{.}}
 {{/eachCommitRefs}}
{{/commits}}
```

### `eachCommitFixes <Commit>`

Renders block for each `fixes` in `Commit`.

```hbs
{{#commits}}
 {{#eachCommitFixes .}}
  fixes issue: {{.}}
 {{/eachCommitFixes}}
{{/commits}}
```

### `eachCommitParagraph <Commit>`

Renders block for each `paragraph` in `Commit`.

```hbs
{{#commits}}
 {{#eachCommitParagraph .}}
  {{.}}
 {{/eachCommitParagraph}}
{{/commits}}
```

### `eachCommitFooter <Commit>`

Renders block for each `footer` in `Commit`.

```hbs
{{#commits}}
 {{#eachCommitFooter .}}
  {{token}}
 {{/eachCommitFooter}}
{{/commits}}
```

Optional `tokenMatching` regex parameter filters footer tokens.

### `ifFooterHasValue <Footer>`

Conditional, renders a block if given `Footer` has `value`.

```hbs
{{#commits}}
 {{#eachCommitFooter .}}
   {{#ifFooterHasValue .}}
    {{{value}}}
   {{/ifFooterHasValue}}
 {{/eachCommitFooter}}
{{/commits}}
```

### `ifEquals <a> <b>`

Conditional, renders a block if `a` equals `b`.

```hbs
{{#tags}}
 {{name}} Unreleased ? {{#ifEquals name "Unreleased"}} ja {{else}} nej {{/ifEquals}}
{{/tags}}
```

### `ifMatches <a> <b>`

Conditional, renders a block if `a` matches regexp `b`.

```hbs
{{#eachCommitFixes .}}
 {{#ifMatches . "^[A-Z]+-[0-9]+"}}
  fixes : "{{subString . 0 3}}" and number {{subString . 4}}
 {{/ifMatches}}
{{/eachCommitFixes}}
```

### `subString <a> <b> <c>`

Works just like [Java substring](<https://docs.oracle.com/javase/7/docs/api/java/lang/String.html#substring(int)>).

```hbs
{{#eachCommitFixes .}}
 {{#ifMatches . "^[A-Z]+-[0-9]+"}}
  fixes : "{{subString . 0 3}}" and number {{subString . 4}}
 {{/ifMatches}}
{{/eachCommitFixes}}
```

## Context

The template is supplied with this context of prepopulated mustache/handlebars variables:

<details><summary>Click here to show context</summary>
<p>

```
(ownerName, repoName, urlParts - derived from the clone URL, git remote origin MUST BE SET)
- ownerName (for this repo it would be "tomasbjerre")
- repoName (for this repo it would be "git-changelog-lib")
- urlParts (for this repo it would be [git-changelog-lib, tomasbjerre, git@github.com])
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
   - hasAdditionalFields
   - additionalFields
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
  - hasAdditionalFields
  - additionalFields
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
 - hasAdditionalFields
 - additionalFields
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
   .render();
```

It can be used to calculate next version number, based on commits:

```java
def nextVersion = gitChangelogApiBuilder()
  .withSemanticMajorVersionPattern("^[Bb]reaking")
  .withSemanticMinorVersionPattern("[Ff]eature")
  .getNextSemanticVersion();

println "Next version:" + nextVersion.toString();
println " Major:" + nextVersion.getMajor();
println " Minor:" + nextVersion.getMinor();
println " Patch:" + nextVersion.getPatch();
```

Settings can be supplied with the build or from a JSON config ([documented here](/src/main/java/se/bjurr/gitchangelog/internal/settings/Settings.java)).
