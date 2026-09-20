package se.bjurr.gitchangelog.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * {@code {{#tags}}}, alone and combined with commits/issues/authors, plus the last-tag-only prepend
 * template.
 */
public class TagsTemplateTest extends AbstractTemplatesTest {

  @Test
  public void testTagsCommits() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplateContent(
                """
                # Git Changelog changelog

                Changelog of Git Changelog.

                {{#tags}}
                ## {{name}}{{#hasTagTime}} ({{tagTime}}){{/hasTagTime}}
                {{annotation}}
                 {{#commits}}
                ### {{authorName}} - {{commitTime}}
                [{{hash}}](https://server/{{hash}})

                {{{message}}}

                 {{/commits}}
                {{/tags}}
                """)
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
            # Git Changelog changelog

            Changelog of Git Changelog.

            ## test (2020-01-01 00:11:00)

            ### Bob - 2020-01-01 00:11:00
            [9427e3ef765e48e](https://server/9427e3ef765e48e)

            Tidy up whitespace

            ### Alice - 2020-01-01 00:10:00
            [fa34fdd74dc8aec](https://server/fa34fdd74dc8aec)

            General bug sweep flagged in

            ### Bob - 2020-01-01 00:09:00
            [127169f614ede3a](https://server/127169f614ede3a)

            Address code quality flagged in

            ### Alice - 2020-01-01 00:08:00
            [1e90a9c9ef6e7dc](https://server/1e90a9c9ef6e7dc)

            Resolve incident logged in

            ### Bob - 2020-01-01 00:07:00
            [f01290c7a6cc563](https://server/f01290c7a6cc563)

            Refactor module addressed in

            ### Alice - 2020-01-01 00:06:00
            [18d2df17b08e6e1](https://server/18d2df17b08e6e1)

            Improve reliability addressed in

            ## 1.0 (2020-01-01 00:05:00)

            ### Bob - 2020-01-01 00:05:00
            [68af4c90cc26bde](https://server/68af4c90cc26bde)

            Add cool feature requested in

            ### Bob - 2020-01-01 00:04:00
            [24043e3a72ec224](https://server/24043e3a72ec224)

            Fix serious problem reported in

            ### Alice - 2020-01-01 00:01:00
            [cf032fad475c3db](https://server/cf032fad475c3db)

            Add feature A

            paragraph one

             * item one
             * item two

            ### Alice - 2020-01-01 00:00:00
            [3b2a25d3e86b87c](https://server/3b2a25d3e86b87c)

            Initial commit


            """);
  }

  @Test
  public void testOnlyLastTag() throws Exception {
    // Uses the real, shipped changelog-prepend.hbs (not an inlined copy), so this test also
    // catches a regression in that actual template, not just in a description of it.
    final String rendered = this.baseBuilder.withTemplatePath("changelog-prepend.hbs").render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
            ## test (2020-01-01)

            ### Other changes

            **Tidy up whitespace**


            [9427e](https://github.com/tomasbjerre/git-changelog-lib/commit/9427e3ef765e48e) Bob *2020-01-01 00:11:00*

            **General bug sweep flagged in**


            [fa34f](https://github.com/tomasbjerre/git-changelog-lib/commit/fa34fdd74dc8aec) Alice *2020-01-01 00:10:00*

            **Address code quality flagged in**


            [12716](https://github.com/tomasbjerre/git-changelog-lib/commit/127169f614ede3a) Bob *2020-01-01 00:09:00*

            **Resolve incident logged in**


            [1e90a](https://github.com/tomasbjerre/git-changelog-lib/commit/1e90a9c9ef6e7dc) Alice *2020-01-01 00:08:00*

            **Refactor module addressed in**


            [f0129](https://github.com/tomasbjerre/git-changelog-lib/commit/f01290c7a6cc563) Bob *2020-01-01 00:07:00*

            **Improve reliability addressed in**


            [18d2d](https://github.com/tomasbjerre/git-changelog-lib/commit/18d2df17b08e6e1) Alice *2020-01-01 00:06:00*



            """);
  }

  @Test
  public void testTagsIssuesAuthorsCommits() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplateContent(
                """
                # Git Changelog changelog

                Changelog of Git Changelog.

                {{#tags}}
                ## {{name}}
                {{annotation}}
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
                {{/tags}}""")
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
            # Git Changelog changelog

            Changelog of Git Changelog.

            ## test

            ### Bugs Mixed bugs
            * Alice
            [fa34fdd74dc8aec](https://server/fa34fdd74dc8aec) *2020-01-01 00:10:00*
            General bug sweep flagged in


            ### CQ [CQ55](http://cq/55) 55
            * Bob
            [127169f614ede3a](https://server/127169f614ede3a) *2020-01-01 00:09:00*
            Address code quality flagged in


            ### Incident [INC123](http://inc/INC123) INC123
            * Alice
            [1e90a9c9ef6e7dc](https://server/1e90a9c9ef6e7dc) *2020-01-01 00:08:00*
            Resolve incident logged in


            ### Jira [JIR-1234](https://jiraserver/jira/browse/JIR-1234) Title of jira 1234
            * Alice
            [18d2df17b08e6e1](https://server/18d2df17b08e6e1) *2020-01-01 00:06:00*
            Improve reliability addressed in


            ### Jira [JIR-5262](https://jiraserver/jira/browse/JIR-5262) The Title of jira 5262
            * Bob
            [f01290c7a6cc563](https://server/f01290c7a6cc563) *2020-01-01 00:07:00*
            Refactor module addressed in


            ### No issue supplied
            * Bob
            [9427e3ef765e48e](https://server/9427e3ef765e48e) *2020-01-01 00:11:00*
            Tidy up whitespace


            ## 1.0

            ### GitHub [#20](https://github.com/tomasbjerre/made-up-mocked-repo/issues/20) Parameterized Jenkins&#x27; job error
            * Bob
            [24043e3a72ec224](https://server/24043e3a72ec224) *2020-01-01 00:04:00*
            Fix serious problem reported in


            ### GitHub [#80](https://github.com/tomasbjerre/made-up-mocked-repo/issues/80) Create resource that can be invoked from scripts to trigger events
            * Bob
            [68af4c90cc26bde](https://server/68af4c90cc26bde) *2020-01-01 00:05:00*
            Add cool feature requested in


            ### No issue supplied
            * Alice
            [cf032fad475c3db](https://server/cf032fad475c3db) *2020-01-01 00:01:00*
            Add feature A

            paragraph one

             * item one
             * item two

            [3b2a25d3e86b87c](https://server/3b2a25d3e86b87c) *2020-01-01 00:00:00*
            Initial commit



            """);
  }

  @Test
  public void testTagsIssuesCommits() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplateContent(
                """
                # Git Changelog changelog

                Changelog of Git Changelog.

                {{#tags}}
                ## {{name}}
                {{annotation}}
                 {{#issues}}
                  {{#hasLink}}
                ### {{name}} [{{issue}}]({{link}}) {{title}}
                  {{/hasLink}}
                  {{^hasLink}}
                ### {{name}} {{title}}
                  {{/hasLink}}
                   {{#commits}}
                [{{hash}}](https://server/{{hash}}) {{commitTime}}
                {{{message}}}

                   {{/commits}}
                 {{/issues}}
                {{/tags}}""")
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
            # Git Changelog changelog

            Changelog of Git Changelog.

            ## test

            ### Bugs Mixed bugs
            [fa34fdd74dc8aec](https://server/fa34fdd74dc8aec) 2020-01-01 00:10:00
            General bug sweep flagged in

            ### CQ [CQ55](http://cq/55) 55
            [127169f614ede3a](https://server/127169f614ede3a) 2020-01-01 00:09:00
            Address code quality flagged in

            ### Incident [INC123](http://inc/INC123) INC123
            [1e90a9c9ef6e7dc](https://server/1e90a9c9ef6e7dc) 2020-01-01 00:08:00
            Resolve incident logged in

            ### Jira [JIR-1234](https://jiraserver/jira/browse/JIR-1234) Title of jira 1234
            [18d2df17b08e6e1](https://server/18d2df17b08e6e1) 2020-01-01 00:06:00
            Improve reliability addressed in

            ### Jira [JIR-5262](https://jiraserver/jira/browse/JIR-5262) The Title of jira 5262
            [f01290c7a6cc563](https://server/f01290c7a6cc563) 2020-01-01 00:07:00
            Refactor module addressed in

            ### No issue supplied
            [9427e3ef765e48e](https://server/9427e3ef765e48e) 2020-01-01 00:11:00
            Tidy up whitespace

            ## 1.0

            ### GitHub [#20](https://github.com/tomasbjerre/made-up-mocked-repo/issues/20) Parameterized Jenkins&#x27; job error
            [24043e3a72ec224](https://server/24043e3a72ec224) 2020-01-01 00:04:00
            Fix serious problem reported in

            ### GitHub [#80](https://github.com/tomasbjerre/made-up-mocked-repo/issues/80) Create resource that can be invoked from scripts to trigger events
            [68af4c90cc26bde](https://server/68af4c90cc26bde) 2020-01-01 00:05:00
            Add cool feature requested in

            ### No issue supplied
            [cf032fad475c3db](https://server/cf032fad475c3db) 2020-01-01 00:01:00
            Add feature A

            paragraph one

             * item one
             * item two

            [3b2a25d3e86b87c](https://server/3b2a25d3e86b87c) 2020-01-01 00:00:00
            Initial commit


            """);
  }
}
