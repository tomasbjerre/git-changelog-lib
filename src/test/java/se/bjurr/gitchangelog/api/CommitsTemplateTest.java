package se.bjurr.gitchangelog.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** {@code {{#commits}}}, {@code {{#authors}}}, message variables, partials and merge filtering. */
public class CommitsTemplateTest extends AbstractTemplatesTest {

  @Test
  public void testAuthorsCommits() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplateContent(
                """
                # Git Changelog changelog

                Changelog of Git Changelog.

                {{#authors}}
                * {{authorName}}
                 {{#commits}}

                [{{hash}}](https://server/{{hash}}) {{authorName}} *{{commitTime}}*

                {{{message}}}

                 {{/commits}}

                {{/authors}}
                """)
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
            # Git Changelog changelog

            Changelog of Git Changelog.

            * Bob

            [9427e3ef765e48e](https://server/9427e3ef765e48e) Bob *2020-01-01 00:11:00*

            Tidy up whitespace


            [127169f614ede3a](https://server/127169f614ede3a) Bob *2020-01-01 00:09:00*

            Address code quality flagged in


            [f01290c7a6cc563](https://server/f01290c7a6cc563) Bob *2020-01-01 00:07:00*

            Refactor module addressed in


            [68af4c90cc26bde](https://server/68af4c90cc26bde) Bob *2020-01-01 00:05:00*

            Add cool feature requested in


            [24043e3a72ec224](https://server/24043e3a72ec224) Bob *2020-01-01 00:04:00*

            Fix serious problem reported in


            * Alice

            [fa34fdd74dc8aec](https://server/fa34fdd74dc8aec) Alice *2020-01-01 00:10:00*

            General bug sweep flagged in


            [1e90a9c9ef6e7dc](https://server/1e90a9c9ef6e7dc) Alice *2020-01-01 00:08:00*

            Resolve incident logged in


            [18d2df17b08e6e1](https://server/18d2df17b08e6e1) Alice *2020-01-01 00:06:00*

            Improve reliability addressed in


            [cf032fad475c3db](https://server/cf032fad475c3db) Alice *2020-01-01 00:01:00*

            Add feature A

            paragraph one

             * item one
             * item two


            [3b2a25d3e86b87c](https://server/3b2a25d3e86b87c) Alice *2020-01-01 00:00:00*

            Initial commit



            """);
  }

  @Test
  public void testCommits() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplateContent(
                """
                # Git Changelog changelog

                Changelog of Git Changelog.

                {{#commits}}
                ## {{authorName}} - {{commitTime}}
                [{{hashFull}}](https://server/{{hash}})

                {{{message}}}

                {{/commits}}
                """)
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
            # Git Changelog changelog

            Changelog of Git Changelog.

            ## Bob - 2020-01-01 00:11:00
            [9427e3ef765e48e25c66e074386bec122cb21d40](https://server/9427e3ef765e48e)

            Tidy up whitespace

            ## Alice - 2020-01-01 00:10:00
            [fa34fdd74dc8aecaad8eaa5f9fd6bece00d32c2b](https://server/fa34fdd74dc8aec)

            General bug sweep flagged in

            ## Bob - 2020-01-01 00:09:00
            [127169f614ede3af56f0e71908fc69f6766256da](https://server/127169f614ede3a)

            Address code quality flagged in

            ## Alice - 2020-01-01 00:08:00
            [1e90a9c9ef6e7dc97e72400652d1ee7ade585a9a](https://server/1e90a9c9ef6e7dc)

            Resolve incident logged in

            ## Bob - 2020-01-01 00:07:00
            [f01290c7a6cc56373854eb0fb690658851466574](https://server/f01290c7a6cc563)

            Refactor module addressed in

            ## Alice - 2020-01-01 00:06:00
            [18d2df17b08e6e15ac61fc0054286313d4f49c63](https://server/18d2df17b08e6e1)

            Improve reliability addressed in

            ## Bob - 2020-01-01 00:05:00
            [68af4c90cc26bde3c64932dbbda86eb334dc6efb](https://server/68af4c90cc26bde)

            Add cool feature requested in

            ## Bob - 2020-01-01 00:04:00
            [24043e3a72ec224994a2669ee98472588a4f68cd](https://server/24043e3a72ec224)

            Fix serious problem reported in

            ## Alice - 2020-01-01 00:01:00
            [cf032fad475c3dbb5b33995269a0b842c7f0276b](https://server/cf032fad475c3db)

            Add feature A

            paragraph one

             * item one
             * item two

            ## Alice - 2020-01-01 00:00:00
            [3b2a25d3e86b87cd0de5df623be686e3823c5506](https://server/3b2a25d3e86b87c)

            Initial commit


            """);
  }

  @Test
  public void testCommitsVariables() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplateContent(
                """
                # Git Changelog changelog

                Changelog of Git Changelog.

                {{#commits}}
                ## Commit {{hash}}
                 Message: {{message}}

                 Message Title: {{messageTitle}}

                 Message Body: {{messageBody}}

                {{#messageBodyItems}}
                 Item: {{.}}

                {{/messageBodyItems}}

                {{/commits}}
                """)
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
            # Git Changelog changelog

            Changelog of Git Changelog.

            ## Commit 9427e3ef765e48e
             Message: Tidy up whitespace

             Message Title: Tidy up whitespace

             Message Body:


            ## Commit fa34fdd74dc8aec
             Message: General bug sweep flagged in

             Message Title: General bug sweep flagged in

             Message Body:


            ## Commit 127169f614ede3a
             Message: Address code quality flagged in

             Message Title: Address code quality flagged in

             Message Body:


            ## Commit 1e90a9c9ef6e7dc
             Message: Resolve incident logged in

             Message Title: Resolve incident logged in

             Message Body:


            ## Commit f01290c7a6cc563
             Message: Refactor module addressed in

             Message Title: Refactor module addressed in

             Message Body:


            ## Commit 18d2df17b08e6e1
             Message: Improve reliability addressed in

             Message Title: Improve reliability addressed in

             Message Body:


            ## Commit 68af4c90cc26bde
             Message: Add cool feature requested in

             Message Title: Add cool feature requested in

             Message Body:


            ## Commit 24043e3a72ec224
             Message: Fix serious problem reported in

             Message Title: Fix serious problem reported in

             Message Body:


            ## Commit cf032fad475c3db
             Message: Add feature A

            paragraph one

             * item one
             * item two

             Message Title: Add feature A

             Message Body: paragraph one
             * item one
             * item two

             Item: paragraph one

             Item: item one

             Item: item two


            ## Commit 3b2a25d3e86b87c
             Message: Initial commit

             Message Title: Initial commit

             Message Body:


            """);
  }

  @Test
  public void testThatIgnoreCommitsIfMessageMatchesCanBeEmptyToDisableTheFeature()
      throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplateContent(
                """
                {{#commits}}
                 {{#merge}}
                ## Merge: {{messageTitle}}
                 {{/merge}}
                 {{^merge}}
                ## Not merge: {{messageTitle}}
                 {{/merge}}
                {{/commits}}
                """)
            .withIgnoreCommitsWithMessage("")
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
            ## Not merge: Tidy up whitespace
            ## Not merge: General bug sweep flagged in
            ## Not merge: Address code quality flagged in
            ## Not merge: Resolve incident logged in
            ## Not merge: Refactor module addressed in
            ## Not merge: Improve reliability addressed in
            ## Not merge: Add cool feature requested in
            ## Not merge: Fix serious problem reported in
            ## Merge: Merge branch &#x27;feature-x&#x27; into master
            ## Not merge: [Gradle Release Plugin] plumbing commit, filtered out by default
            ## Not merge: Add feature A
            ## Not merge: Initial commit

            """);
  }

  @Test
  public void testThatPartialsCanBeIncluded() throws Exception {
    // The main template is inlined below, but commit.partial has to stay a real file - loading a
    // partial from a file, by templateBaseDir + templateSuffix, is the feature under test.
    final String rendered =
        this.baseBuilder
            .withTemplateContent(
                """
                # Git Changelog changelog

                Changelog of Git Changelog.

                {{#commits}}
                {{> commit}}
                {{/commits}}""")
            .withTemplateBaseDir("./src/test/resources/templatetest")
            .withTemplateSuffix(".partial")
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
            # Git Changelog changelog

            Changelog of Git Changelog.

            ## Bob - 2020-01-01 00:11:00
            [9427e3ef765e48e25c66e074386bec122cb21d40](https://server/9427e3ef765e48e)

            Tidy up whitespace
            ## Alice - 2020-01-01 00:10:00
            [fa34fdd74dc8aecaad8eaa5f9fd6bece00d32c2b](https://server/fa34fdd74dc8aec)

            General bug sweep flagged in
            ## Bob - 2020-01-01 00:09:00
            [127169f614ede3af56f0e71908fc69f6766256da](https://server/127169f614ede3a)

            Address code quality flagged in
            ## Alice - 2020-01-01 00:08:00
            [1e90a9c9ef6e7dc97e72400652d1ee7ade585a9a](https://server/1e90a9c9ef6e7dc)

            Resolve incident logged in
            ## Bob - 2020-01-01 00:07:00
            [f01290c7a6cc56373854eb0fb690658851466574](https://server/f01290c7a6cc563)

            Refactor module addressed in
            ## Alice - 2020-01-01 00:06:00
            [18d2df17b08e6e15ac61fc0054286313d4f49c63](https://server/18d2df17b08e6e1)

            Improve reliability addressed in
            ## Bob - 2020-01-01 00:05:00
            [68af4c90cc26bde3c64932dbbda86eb334dc6efb](https://server/68af4c90cc26bde)

            Add cool feature requested in
            ## Bob - 2020-01-01 00:04:00
            [24043e3a72ec224994a2669ee98472588a4f68cd](https://server/24043e3a72ec224)

            Fix serious problem reported in
            ## Alice - 2020-01-01 00:01:00
            [cf032fad475c3dbb5b33995269a0b842c7f0276b](https://server/cf032fad475c3db)

            Add feature A

            paragraph one

             * item one
             * item two
            ## Alice - 2020-01-01 00:00:00
            [3b2a25d3e86b87cd0de5df623be686e3823c5506](https://server/3b2a25d3e86b87c)

            Initial commit

            """);
  }
}
