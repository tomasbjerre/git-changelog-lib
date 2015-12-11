# Git Changelog Lib [![Build Status](https://travis-ci.org/tomasbjerre/git-changelog-lib.svg?branch=master)](https://travis-ci.org/tomasbjerre/git-changelog-lib)

This is a library for generating a changelog, or releasenotes, from a GIT repository. It can also be run as a standalone program, Gradle plugin, Maven plugin or Jenkins plugin.

It is fully configurable with a [Mustache](http://mustache.github.io/) template. That can:

 * Be stored to file, like CHANGELOG.md. There are some templates used for testing available [here](https://github.com/tomasbjerre/git-changelog/tree/master/src/test/resources/templates) and the results [here](https://github.com/tomasbjerre/git-changelog/tree/master/src/test/resources/assertions).
 * Be posted to MediaWiki ([here](https://github.com/tomasbjerre/git-changelog-lib/tree/screenshots/sandbox) is an example)
 * Or just be printed to STDOUT

It can integrate with Jira and/or GitHub to retrieve the title of issues.

There are some screenshots [here](https://github.com/tomasbjerre/git-changelog-lib/tree/screenshots/sandbox).

## Usage
This software can be used:
 * With a [Gradle plugin](https://github.com/tomasbjerre/git-changelog-gradle-plugin).
 * With a [Maven plugin](https://github.com/tomasbjerre/git-changelog-maven-plugin).
 * With a [Jenkins plugin](https://github.com/jenkinsci/git-changelog-plugin).
 * With a [Bitbucket Server plugin](https://github.com/tomasbjerre/git-changelog-bitbucket-plugin).
 * As a library [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22git-changelog-lib%22).
 * From command line [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22git-changelog-lib%22) (the zip file).

Here is an example template. There are more examples [here](https://github.com/tomasbjerre/git-changelog/tree/master/src/test/resources/templates).
```
# Git Changelog changelog

Changelog of Git Changelog.
{{#tags}}
## {{name}}
 {{#issues}}
  {{#hasLink}}
### {{name}} [{{issue}}]({{link}}) {{title}}
  {{/hasLink}}
  {{^hasLink}}
### {{name}} {{title}}
  {{/hasLink}}
  {{#authors}}
* {{authorName}}
   {{#commits}}
[{{hash}}](https://server/{{hash}}) *{{commitTime}}*
{{{message}}}

   {{/commits}}

  {{/authors}}
 {{/issues}}
{{/tags}}
```

### Library

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

### Command line
Or from command line:
```
-cl, --custom-issue-link <string>          Custom issue link.
                                           <string>: any string
                                           Default: null
-cn, --custom-issue-name <string>          Custom issue name.
                                           <string>: any string
                                           Default: null
-cp, --custom-issue-pattern <string>       Custom issue pattern.
                                           <string>: any string
                                           Default: null
-df, --date-format <string>                Format to use when printing dates.
                                           <string>: any string
                                           Default: YYYY-MM-dd HH:mm:ss
-fc, --from-commit <string>                From commit.
                                           <string>: any string
                                           Default: 0000000000000000000000000000000000000000
-fr, --from-ref <string>                   From ref.
                                           <string>: any string
                                           Default: null
-gapi, --github-api <string>               GitHub API.
                                           <string>: any string
                                           Default: 
-h, --help <argument-to-print-help-for>    <argument-to-print-help-for>: an argument to print help for
                                           Default: If no specific parameter is given the whole usage text is given
-ip, --ignore-pattern <string>             Ignore commits where pattern 
                                           matches message.
                                           <string>: any string
                                           Default: ^\[maven-release-plugin\].*|^\[Gradle Release Plugin\].*|^Merge.*
-jp, --jira-pattern <string>               Jira issue pattern.
                                           <string>: any string
                                           Default: \b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\b
-jpw, --jira-password <string>             Optional password to authenticate 
                                           with Jira.
                                           <string>: any string
                                           Default: \b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\b
-js, --jiraServer <string>                 Jira server. When a Jira server is 
                                           given, the title of the Jira issues can be 
                                           used in the changelog.
                                           <string>: any string
                                           Default: 
-ju, --jira-username <string>              Optional username to authenticate 
                                           with Jira.
                                           <string>: any string
                                           Default: \b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\b
-mp, --mediawiki-password <string>         Password to authenticate with 
                                           MediaWiki.
                                           <string>: any string
                                           Default: 
-mt, --mediawiki-title <string>            Title of MediaWiki page.
                                           <string>: any string
                                           Default: null
-mu, --mediawiki-user <string>             User to authenticate with MediaWiki.
                                           <string>: any string
                                           Default: 
-murl, --mediawiki-url <string>            Base URL of MediaWiki.
                                           <string>: any string
                                           Default: 
-ni, --no-issue-name <string>              Name of virtual issue that contains 
                                           commits that has no issue associated.
                                           <string>: any string
                                           Default: No issue
-of, --output-file <string>                Write output to file.
                                           <string>: any string
                                           Default: 
-r, --repo <string>                        Repository.
                                           <string>: any string
                                           Default: .
-ri, --remove-issue-from-message           Dont print any issues in the 
                                           messages of commits.
                                           Default: disabled
-rt, --readable-tag-name <string>          Pattern to extract readable part of 
                                           tag.
                                           <string>: any string
                                           Default: /([^/]+?)$
-sf, --settings-file <string>              Use settings from file.
                                           <string>: any string
                                           Default: null
-std, --stdout                             Print builder to <STDOUT>.
                                           Default: disabled
-t, --template <string>                    Template to use. A default template 
                                           will be used if not specified.
                                           <string>: any string
                                           Default: git-changelog-template.mustache
-tc, --to-commit <string>                  To commit.
                                           <string>: any string
                                           Default: null
-tr, --to-ref <string>                     To ref.
                                           <string>: any string
                                           Default: refs/heads/master
-tz, --time-zone <string>                  TimeZone to use when printing dates.
                                           <string>: any string
                                           Default: UTC
-ut, --untagged-name <string>              When listing commits per tag, this 
                                           will by the name of a virtual tag that 
                                           contains commits not available in any git 
                                           tag.
                                           <string>: any string
                                           Default: No tag
```

Creating a MediaWiki page can be done like this.
```
./git-changelog-lib -murl http://localhost/mediawiki -mu tomas -mp tomaskod -mt "Tomas Title" -t /home/bjerre/workspace/git-changelog-lib/changelog_mediawiki.mustache -ut "Next release"
```

## Supplied information

The template is supplied with a datastructure like:
```
* commits
 - authorName
 - authorEmailAddress
 - message
 - commitTime
* tags
 - name
 * commits
  - authorName
  - authorEmailAddress
  - message
  - commitTime
 * authors
  - authorName
  - authrorEmail
  * commits
   - authorName
   - authorEmailAddress
   - message
   - commitTime
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
   - message
   - commitTime
  * authors
   - authorName
   - authrorEmail
   * commits
    - authorName
    - authorEmailAddress
    - message
    - commitTime
* authors
 - authorName
 - authrorEmail
 * commits
  - authorName
  - authorEmailAddress
  - message
  - commitTime
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
  - message
  - commitTime
 * authors
  - authorName
  - authrorEmail
  * commits
   - authorName
   - authorEmailAddress
   - message
   - commitTime
```

## MediaWiki
The library can create a wiki page in MediaWiki. To do this, you must enable the API in MediaWiki in `mediawiki/LocalSettings.php` by adding:
```
$wgEnableAPI = true;
$wgEnableWriteAPI = true;
```

## Developer instructions

To build the code, have a look at `.travis.yml`.

To do a release you need to do `./gradlew release` and release the artifact from [staging](https://oss.sonatype.org/#stagingRepositories). More information [here](http://central.sonatype.org/pages/releasing-the-deployment.html).
