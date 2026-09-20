package se.bjurr.gitchangelog.api.helpers;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_MASTER;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.io.File;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import se.bjurr.gitchangelog.api.GitChangelogApi;
import se.bjurr.gitchangelog.test.TestGitRepo;

/**
 * These templates exercise nearly every conventional-commit Handlebars helper (breaking changes via
 * both the "!" marker and a BREAKING CHANGE footer, other footers, multi-paragraph bodies, revert,
 * scopes, refs, fixes, release-tag detection, ...), so the repo built in {@link #before} reuses -
 * verbatim - the same 15 conventional-commit messages this suite has exercised for years (they're
 * just example text, not tied to any particular commit), now as fresh synthetic commits instead of
 * a real, un-rewritable slice of this project's history.
 */
public class HandlebarsHelperTest {
  private GitChangelogApi baseBuilder;
  private TestGitRepo repo;

  @BeforeEach
  public void before(@TempDir final File repoDir) throws Exception {
    this.repo =
        TestGitRepo.in(repoDir) //
            .commit("c1", "fix: dont use integrations when determining versions", "f", "1")
            .commit("c2", "New version: 1.147.1 [GRADLE SCRIPT]", "f", "2")
            .tag("1.147.1", "c2")
            .commit("c3", "Updating changelog with 1.147.1 [GRADLE SCRIPT]", "f", "3")
            .commit("c4", "fix: dont require semantic patterns to get highest tag", "f", "4")
            .commit("c5", "New version: 1.147.2 [GRADLE SCRIPT]", "f", "5")
            .tag("1.147.2", "c5")
            .commit("c6", "doing change 2", "f", "6")
            .commit("c7", "feat(utils): more utils", "f", "7")
            .commit("c8", "fix(utils:mix): the description (fixes ABC-123)", "f", "8")
            .commit(
                "c9",
                "Revert \"[Gradle Release Plugin] - pre tag commit:  '1.94'.\"\n\n"
                    + "This reverts commit 1edc0d71eccce51abfb5f62fdddfbe73913785f5.",
                "f",
                "9")
            .commit("c10", "refactor!: doing major stuff", "f", "10")
            .commit(
                "c11",
                "refactor!: using both ! and br\n\n"
                    + "BREAKING CHANGE: refactor to use JavaScript features not available in"
                    + " Node 6.",
                "f",
                "11")
            .commit(
                "c12",
                "refactor: using only br\n\n"
                    + "BREAKING CHANGE: refactor to use JavaScript features not available in"
                    + " Node 6.",
                "f",
                "12")
            .commit(
                "c13",
                "feat: allow provided config object to extend other configs\n\n"
                    + "BREAKING CHANGE: `extends` key in config file is now used for extending"
                    + " other config files",
                "f",
                "13")
            .commit(
                "c14",
                "fix: correct minor typos in code\n\n"
                    + "see the issue for details\n\n"
                    + "on typos fixed.\n\n"
                    + "Reviewed-by: Z\n"
                    + "Refs #133",
                "f",
                "14")
            .commit(
                "c15",
                "feat: dsc\n\n"
                    + "paragraph first line of two here!\n"
                    + "second line of first paragraph\n\n"
                    + "second paragraph the only line\n\n"
                    + "third paragraph first line of two\n"
                    + "and second line of third paragraph.\n\n"
                    + "first-token-here: value of first token\n"
                    + "second line of first token\n"
                    + "second-token-here: value of second token",
                "f",
                "15");

    this.baseBuilder =
        gitChangelogApiBuilder() //
            .withUseIntegrations(false)
            .withJiraEnabled(true)
            .withGitHubEnabled(true)
            .withGitLabEnabled(true)
            .withRedmineEnabled(true)
            .withFromRepo(this.repo.dir()) //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef(REF_MASTER);
  }

  @AfterEach
  public void after() throws Exception {
    this.repo.close();
  }

  // @Test //Available depending on JVM
  public void testThatHelperCanBeSuppliedWithJavascript() throws Exception {
    // Disabled (JVM-dependent Nashorn/JS engine availability), so left unasserted - just
    // keeping it exercisable for when it's re-enabled.
    this.baseBuilder //
        .withTemplatePath("templatetest/helpers/testThatHelperCanBeSuppliedWithJavascript.mustache")
        .withHandlebarsHelper(
            "Handlebars.registerHelper(\"firstWord\", function(options) {"
                + "  return options.fn(this).split(\" \")[0];"
                + "});")
        .render();
  }

  @Test
  public void testThatBuiltInHelperMethodsCanBeUsed() throws Exception {
    final String rendered =
        this.baseBuilder //
            .withTemplatePath("templatetest/helpers/testThatBuiltInHelperMethodsCanBeUsed.mustache")
            .render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""

Test template:

Tags:

 * name: Unreleased
 * name: 1.147.2
 * name: 1.147.1


Commits:

 * hash: 4bf0c8d4cd9039f message: feat: dsc

paragraph first line of two here!
second line of first paragraph

second paragraph the only line

third paragraph first line of two
and second line of third paragraph.

first-token-here: value of first token
second line of first token
second-token-here: value of second token
 * hash: 915e7b71d2433fa message: fix: correct minor typos in code

see the issue for details

on typos fixed.

Reviewed-by: Z
Refs #133
 * hash: 3fd984a80bbad8a message: feat: allow provided config object to extend other configs

BREAKING CHANGE: &#x60;extends&#x60; key in config file is now used for extending other config files
 * hash: 12996880af59622 message: refactor: using only br

BREAKING CHANGE: refactor to use JavaScript features not available in Node 6.
 * hash: 5235b52c222d928 message: refactor!: using both ! and br

BREAKING CHANGE: refactor to use JavaScript features not available in Node 6.
 * hash: 9528ad59b417fcf message: refactor!: doing major stuff
 * hash: cdbd649e5d93976 message: Revert &quot;[Gradle Release Plugin] - pre tag commit:  &#x27;1.94&#x27;.&quot;

This reverts commit 1edc0d71eccce51abfb5f62fdddfbe73913785f5.
 * hash: f4cb735372819fd message: fix(utils:mix): the description (fixes ABC-123)
 * hash: b8f77dd1eb3fe6c message: feat(utils): more utils
 * hash: 215922bcf60054c message: doing change 2
 * hash: bf906e37650a1d7 message: New version: 1.147.2 [GRADLE SCRIPT]
 * hash: ee0d94b7b066c05 message: fix: dont require semantic patterns to get highest tag
 * hash: ecac47c16c4c148 message: Updating changelog with 1.147.1 [GRADLE SCRIPT]
 * hash: 8c836ef4f817b02 message: New version: 1.147.1 [GRADLE SCRIPT]
 * hash: ef1c8ee0a56c2e2 message: fix: dont use integrations when determining versions


ifMatches and subString:
   fixes : "ABC" and number 123


 Unreleased Unreleased ?  yes
 1.147.2 Unreleased ?  no
 1.147.1 Unreleased ?  no


## breaking changes:
  contains breaking changes
    is breaking: 3fd984a80bbad8a
    is breaking: 12996880af59622
    is breaking: 5235b52c222d928
    is breaking: 9528ad59b417fcf


## feat utils:
  feat and utils exist!
	 is feat and utils: b8f77dd1eb3fe6c


## Footers
Has footers: 4bf0c8d4cd9039f
 - "first-token-here"
```
----footer-value-begins----
value of first token
second line of first token
----footer-value-ends---
```

 - "second-token-here"
```
----footer-value-begins----
value of second token
----footer-value-ends---
```

 - "first-token-here" matches `first.*|Refs`

Has footers: 915e7b71d2433fa
 - "Reviewed-by"
```
----footer-value-begins----
Z
----footer-value-ends---
```

 - "Refs"
```
----footer-value-begins----
133
----footer-value-ends---
```

 - "Refs" matches `first.*|Refs`

Has footers: 3fd984a80bbad8a
 - "BREAKING CHANGE"
```
----footer-value-begins----
`extends` key in config file is now used for extending other config files
----footer-value-ends---
```

Has footers: 12996880af59622
 - "BREAKING CHANGE"
```
----footer-value-begins----
refactor to use JavaScript features not available in Node 6.
----footer-value-ends---
```

Has footers: 5235b52c222d928
 - "BREAKING CHANGE"
```
----footer-value-begins----
refactor to use JavaScript features not available in Node 6.
----footer-value-ends---
```



## Paragraphs
Has paragraphs: 4bf0c8d4cd9039f
```
----paragraph-begins----
paragraph first line of two here!
second line of first paragraph
----paragraph-ends---
```


----paragraph-begins----
second paragraph the only line
----paragraph-ends---
```


----paragraph-begins----
third paragraph first line of two
and second line of third paragraph.
----paragraph-ends---
```


Has paragraphs: 915e7b71d2433fa
```
----paragraph-begins----
see the issue for details
----paragraph-ends---
```


----paragraph-begins----
on typos fixed.
----paragraph-ends---
```


```
```
```
```
Has paragraphs: cdbd649e5d93976
```
----paragraph-begins----
This reverts commit 1edc0d71eccce51abfb5f62fdddfbe73913785f5.
----paragraph-ends---
```


```
```
```
```
```
```
```
```


 tag: name: Unreleased

  commits contains fix

  commits contains other types than fix

 date: 2020-01-01

 tag: name: 1.147.2
  "name: 1.147.2" is a release tag

  commits contains fix

  commits contains other types than fix

 date: 2020-01-01

 tag: name: 1.147.1
  "name: 1.147.1" is a release tag

  commits contains fix

  commits contains other types than fix

 date: 2020-01-01



 Commit: hash: 4bf0c8d4cd9039f message: feat: dsc

paragraph first line of two here!
second line of first paragraph

second paragraph the only line

third paragraph first line of two
and second line of third paragraph.

first-token-here: value of first token
second line of first token
second-token-here: value of second token
 date: 2020-01-01





  commit has not type fix or revert




 description: dsc





 Commit: hash: 915e7b71d2433fa message: fix: correct minor typos in code

see the issue for details

on typos fixed.

Reviewed-by: Z
Refs #133
 date: 2020-01-01


  is type fix

  commit has type fix or revert





 description: correct minor typos in code





 Commit: hash: 3fd984a80bbad8a message: feat: allow provided config object to extend other configs

BREAKING CHANGE: &#x60;extends&#x60; key in config file is now used for extending other config files
 date: 2020-01-01





  commit has not type fix or revert




 description: allow provided config object to extend other configs





 Commit: hash: 12996880af59622 message: refactor: using only br

BREAKING CHANGE: refactor to use JavaScript features not available in Node 6.
 date: 2020-01-01





  commit has not type fix or revert




 description: using only br





 Commit: hash: 5235b52c222d928 message: refactor!: using both ! and br

BREAKING CHANGE: refactor to use JavaScript features not available in Node 6.
 date: 2020-01-01





  commit has not type fix or revert




 description: using both ! and br





 Commit: hash: 9528ad59b417fcf message: refactor!: doing major stuff
 date: 2020-01-01





  commit has not type fix or revert




 description: doing major stuff





 Commit: hash: cdbd649e5d93976 message: Revert &quot;[Gradle Release Plugin] - pre tag commit:  &#x27;1.94&#x27;.&quot;

This reverts commit 1edc0d71eccce51abfb5f62fdddfbe73913785f5.
 date: 2020-01-01

  is type revert


  commit has type fix or revert





 description:

   this reverted commit: 1edc0d71eccce51abfb5f62fdddfbe73913785f5




 Commit: hash: f4cb735372819fd message: fix(utils:mix): the description (fixes ABC-123)
 date: 2020-01-01


  is type fix

  commit has type fix or revert


  is scope utils

  scope: utils
  scope: mix

 description: the description



  fix: ABC-123


 Commit: hash: b8f77dd1eb3fe6c message: feat(utils): more utils
 date: 2020-01-01





  commit has not type fix or revert

  is scope utils

  scope: utils

 description: more utils





 Commit: hash: 215922bcf60054c message: doing change 2
 date: 2020-01-01



  commit has no type

  commit has not type fix or revert




 description:





 Commit: hash: bf906e37650a1d7 message: New version: 1.147.2 [GRADLE SCRIPT]
 date: 2020-01-01



  commit has no type

  commit has not type fix or revert




 description:





 Commit: hash: ee0d94b7b066c05 message: fix: dont require semantic patterns to get highest tag
 date: 2020-01-01


  is type fix

  commit has type fix or revert





 description: dont require semantic patterns to get highest tag





 Commit: hash: ecac47c16c4c148 message: Updating changelog with 1.147.1 [GRADLE SCRIPT]
 date: 2020-01-01



  commit has no type

  commit has not type fix or revert




 description:





 Commit: hash: 8c836ef4f817b02 message: New version: 1.147.1 [GRADLE SCRIPT]
 date: 2020-01-01



  commit has no type

  commit has not type fix or revert




 description:





 Commit: hash: ef1c8ee0a56c2e2 message: fix: dont use integrations when determining versions
 date: 2020-01-01


  is type fix

  commit has type fix or revert





 description: dont use integrations when determining versions







## StringHelpers

Hello
""");
  }

  @Test
  public void testThatConventionalChangelogCanBeRendered() throws Exception {
    final String rendered =
        this.baseBuilder //
            .withTemplatePath(
                "templatetest/helpers/testThatConventionalChangelogCanBeRendered.mustache")
            .render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
# Changelog

## [1.147.2](https://gitlab.com/html-validate/html-validate/compare/1.147.2) (2020-01-01)



### Bug Fixes

 -  dont require semantic patterns to get highest tag ([ee0d94b7b066c05](https://gitlab.com/html-validate/html-validate/commit/ee0d94b7b066c05e94a985c2fb10679da1a96f13))

## [1.147.1](https://gitlab.com/html-validate/html-validate/compare/1.147.1) (2020-01-01)



### Bug Fixes

 -  dont use integrations when determining versions ([ef1c8ee0a56c2e2](https://gitlab.com/html-validate/html-validate/commit/ef1c8ee0a56c2e215b6f509dd0a1d6e0f946a9f4))


""");
  }
}
