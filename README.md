# Git Release Notes [![Build Status](https://travis-ci.org/tomasbjerre/git-changelog-lib.svg?branch=master)](https://travis-ci.org/tomasbjerre/git-changelog-lib)

This is a library for generating a changelog, or releasenotes, from a GIT repository. It can also be run as a standalone program.

It is fully configurable with a [Mustache](http://mustache.github.io/) template. There are some templates used for testing available [here](https://github.com/tomasbjerre/git-changelog/tree/master/src/test/resources/templates) and the results [here](https://github.com/tomasbjerre/git-changelog/tree/master/src/test/resources/assertions).

Available in [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22git-changelog-lib%22).

## Usage
This software can be used:
 * As a library
 * From command line

### As a library

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

### Command line
Or from command line:
```
-cl, --customIssueLink <string>            Custom issue link.
                                           <string>: any string
                                           Default: null
-cn, --customIssueName <string>            Custom issue name.
                                           <string>: any string
                                           Default: null
-cp, --customIssuePattern <string>         Custom issue pattern.
                                           <string>: any string
                                           Default: null
-df, --date-format <string>                Format to use when printing dates.
                                           <string>: any string
                                           Default: YYYY-MM-dd HH:mm:ss
-fc, --fromCommit <string>                 From commit.
                                           <string>: any string
                                           Default: 0000000000000000000000000000000000000000
-fr, --fromRef <string>                    From ref.
                                           <string>: any string
                                           Default: null
-gp, --githubPattern <string>              Github pattern.
                                           <string>: any string
                                           Default: #[0-9]*
-gs, --githubServer <string>               Github server. When a Github server 
                                           is given, the title of the Github 
                                           issues can be used in the changelog.
                                           <string>: any string
                                           Default: 
-h, --help <argument-to-print-help-for>    <argument-to-print-help-for>: an argument to print help for
                                           Default: If no specific parameter is given the whole usage text is given
-ip, --ignorePattern <string>              Ignore commits where pattern 
                                           matches message.
                                           <string>: any string
                                           Default: ^\[maven-release-plugin\].*|^\[Gradle Release Plugin\].*|^Merge.*
-jp, --jiraPattern <string>                Jira issue pattern.
                                           <string>: any string
                                           Default: \b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\b
-js, --jiraServer <string>                 Jira server. When a Jira server is 
                                           given, the title of the Jira issues can be 
                                           used in the changelog.
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
-tc, --toCommit <string>                   To commit.
                                           <string>: any string
                                           Default: null
-tr, --toRef <string>                      To ref.
                                           <string>: any string
                                           Default: refs/heads/master
-tz, --timeZone <string>                   TimeZone to use when printing dates.
                                           <string>: any string
                                           Default: UTC
-ut, --untaggedName <string>               When listing commits per tag, this 
                                           will by the name of a virtual tag that 
                                           contains commits not available in any git 
                                           tag.
                                           <string>: any string
                                           Default: No tag
```

## Developer instructions

To build the code, have a look at `.travis.yml`.

To do a release you need to do `./gradlew release` and release the artifact from [staging](https://oss.sonatype.org/#stagingRepositories). More information [here](http://central.sonatype.org/pages/releasing-the-deployment.html).
