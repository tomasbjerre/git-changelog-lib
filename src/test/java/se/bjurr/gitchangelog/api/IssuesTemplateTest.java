package se.bjurr.gitchangelog.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** {@code {{#issues}}} and the issue-shaped helpers: title/link, type, labels, linked issues. */
public class IssuesTemplateTest extends AbstractTemplatesTest {

  @Test
  public void testIssuesAuthorsCommits() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplateContent(
                """
                # Git Changelog changelog

                Changelog of Git Changelog.

                {{#issues}}
                {{#hasLink}}
                ## {{name}} [{{issue}}]({{link}}) {{title}}
                {{/hasLink}}
                {{^hasLink}}
                ## {{name}} {{title}}
                {{/hasLink}}
                  {{#authors}}
                ### {{authorName}}
                   {{#commits}}
                {{commitTime}}
                {{{message}}}

                   {{/commits}}

                  {{/authors}}
                {{/issues}}""")
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
            # Git Changelog changelog

            Changelog of Git Changelog.

            ## Bugs Mixed bugs
            ### Alice
            2020-01-01 00:10:00
            General bug sweep flagged in


            ## CQ [CQ55](http://cq/55) 55
            ### Bob
            2020-01-01 00:09:00
            Address code quality flagged in


            ## GitHub [#20](https://github.com/tomasbjerre/made-up-mocked-repo/issues/20) Parameterized Jenkins&#x27; job error
            ### Bob
            2020-01-01 00:04:00
            Fix serious problem reported in


            ## GitHub [#80](https://github.com/tomasbjerre/made-up-mocked-repo/issues/80) Create resource that can be invoked from scripts to trigger events
            ### Bob
            2020-01-01 00:05:00
            Add cool feature requested in


            ## Incident [INC123](http://inc/INC123) INC123
            ### Alice
            2020-01-01 00:08:00
            Resolve incident logged in


            ## Jira [JIR-1234](https://jiraserver/jira/browse/JIR-1234) Title of jira 1234
            ### Alice
            2020-01-01 00:06:00
            Improve reliability addressed in


            ## Jira [JIR-5262](https://jiraserver/jira/browse/JIR-5262) The Title of jira 5262
            ### Bob
            2020-01-01 00:07:00
            Refactor module addressed in


            ## No issue supplied
            ### Bob
            2020-01-01 00:11:00
            Tidy up whitespace


            ### Alice
            2020-01-01 00:01:00
            Add feature A

            paragraph one

             * item one
             * item two

            2020-01-01 00:00:00
            Initial commit



            """);
  }

  @Test
  public void testIssuesCommits() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplateContent(
                """
                # Git Changelog changelog

                Changelog of Git Changelog.

                {{#issues}}
                  {{#hasLink}}
                ## {{name}} [{{issue}}]({{link}}) {{title}}
                  {{/hasLink}}
                  {{^hasLink}}
                ## {{name}} {{title}}
                  {{/hasLink}}

                 {{#commits}}
                ### {{authorName}} - {{commitTime}}
                [{{hash}}](https://server/{{hash}})

                {{{message}}}

                 {{/commits}}
                {{/issues}}
                """)
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
            # Git Changelog changelog

            Changelog of Git Changelog.

            ## Bugs Mixed bugs

            ### Alice - 2020-01-01 00:10:00
            [fa34fdd74dc8aec](https://server/fa34fdd74dc8aec)

            General bug sweep flagged in

            ## CQ [CQ55](http://cq/55) 55

            ### Bob - 2020-01-01 00:09:00
            [127169f614ede3a](https://server/127169f614ede3a)

            Address code quality flagged in

            ## GitHub [#20](https://github.com/tomasbjerre/made-up-mocked-repo/issues/20) Parameterized Jenkins&#x27; job error

            ### Bob - 2020-01-01 00:04:00
            [24043e3a72ec224](https://server/24043e3a72ec224)

            Fix serious problem reported in

            ## GitHub [#80](https://github.com/tomasbjerre/made-up-mocked-repo/issues/80) Create resource that can be invoked from scripts to trigger events

            ### Bob - 2020-01-01 00:05:00
            [68af4c90cc26bde](https://server/68af4c90cc26bde)

            Add cool feature requested in

            ## Incident [INC123](http://inc/INC123) INC123

            ### Alice - 2020-01-01 00:08:00
            [1e90a9c9ef6e7dc](https://server/1e90a9c9ef6e7dc)

            Resolve incident logged in

            ## Jira [JIR-1234](https://jiraserver/jira/browse/JIR-1234) Title of jira 1234

            ### Alice - 2020-01-01 00:06:00
            [18d2df17b08e6e1](https://server/18d2df17b08e6e1)

            Improve reliability addressed in

            ## Jira [JIR-5262](https://jiraserver/jira/browse/JIR-5262) The Title of jira 5262

            ### Bob - 2020-01-01 00:07:00
            [f01290c7a6cc563](https://server/f01290c7a6cc563)

            Refactor module addressed in

            ## No issue supplied

            ### Bob - 2020-01-01 00:11:00
            [9427e3ef765e48e](https://server/9427e3ef765e48e)

            Tidy up whitespace

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
  public void testIssueTitles() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplateContent(
                """
                {{#issues}}
                {{name}} {{title}}
                {{/issues}}
                """)
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
            Bugs Mixed bugs
            CQ 55
            GitHub Parameterized Jenkins&#x27; job error
            GitHub Create resource that can be invoked from scripts to trigger events
            Incident INC123
            Jira Title of jira 1234
            Jira The Title of jira 5262
            No issue supplied

            """);
  }

  // @Test
  // Enable when this is fixed: https://github.com/jknack/handlebars.java/issues/951
  public void testIssueType() throws Exception {
    // Disabled (see above), so left unasserted - just keeping it exercisable for when it's
    // re-enabled.
    this.baseBuilder
        .withTemplateContent(
            """
            {{#tags}}
            ## {{name}}
            {{#issues}}

            {{name}}
            {{hasType}} {{type}}
            isGitHub: {{#isGitHub}}yes{{/isGitHub}}
            isGitLab: {{#isGitLab}}yes{{/isGitLab}}
            isJira: {{#isJira}}yes{{/isJira}}
            isRedmine: {{#isRedmine}}yes{{/isRedmine}}
            isCustom: {{#isCustom}}yes{{/isCustom}}
            isNoIssue: {{#isNoIssue}}yes{{/isNoIssue}}

            {{/issues}}
            {{/tags}}


            Issues:
            {{#ifContainsIssueType issues type='Bug'}}
            {{#issues}}
            {{#ifIssueType . type='Bug'}}
            ### Bugs
            {{name}}

            {{/ifIssueType}}
            {{/issues}}
            {{/ifContainsIssueType}}


            {{#ifContainsIssueTypeOtherThan issues type='Bug'}}
            {{#issues}}
            {{#ifIssueTypeOtherThan . type='Bug'}}
            ### Other issues
            {{name}}

            {{/ifIssueTypeOtherThan}}
            {{/issues}}
            {{/ifContainsIssueTypeOtherThan}}
            """)
        .render();
  }

  @Test
  public void testIssueLabels() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplateContent(
                """
                {{#tags}}
                ## {{name}}
                {{#ifContainsIssueLabel issues label='enhancement'}}
                ### Enhancements
                {{#issues}}
                  {{#ifIssueLabel . label='enhancement'}}Found a enhancement{{/ifIssueLabel}}
                {{/issues}}
                {{/ifContainsIssueLabel}}
                {{#ifContainsIssueLabel issues label='bug'}}
                ### Bugs
                {{#issues}}
                  {{#ifIssueLabel . label='bug'}}Found a bug{{/ifIssueLabel}}
                {{/issues}}
                {{/ifContainsIssueLabel}}
                {{#issues}}
                 {{hasLabels}}
                 {{#labels}}
                  {{.}}
                 {{/labels}}
                {{/issues}}
                {{/tags}}
                """)
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
            ## test
             false
             false
             false
             true
              label1
              label2
             false
             false
            ## 1.0
            ### Enhancements

              Found a enhancement

            ### Bugs
              Found a bug


             true
              bug
             true
              enhancement
             false

            """);
  }

  @Test
  public void testIssueLinkedIssues() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplateContent(
                """
                {{#issues}}
                    {{#hasLinkedIssues}}
                        {{issue}} - {{title}} [{{#linkedIssues}} {{.}} {{/linkedIssues}}]
                    {{/hasLinkedIssues}}
                {{/issues}}
                """)
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
                    JIR-5262 - The Title of jira 5262 [ JIRSD-490  JIRSD-567 ]

            """);
  }

  @Test
  public void testIssueTypesIssuesCommits() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplateContent(
                """
                # Git Changelog changelog

                Changelog of Git Changelog.

                {{#tags}}
                ## {{name}}
                 {{#issueTypes}}
                ### {{name}}
                  {{#issues}}
                   {{#hasTitle}}
                     {{#hasLink}}
                #### [{{issue}}]({{link}}) {{title}}
                     {{/hasLink}}
                   {{/hasTitle}}

                   {{#hasTitle}}
                     {{^hasLink}}
                #### {{title}}
                     {{/hasLink}}
                   {{/hasTitle}}

                   {{^hasTitle}}
                    {{^hasIssue}}
                These commits has no issue.
                    {{/hasIssue}}
                   {{/hasTitle}}

                   {{#commits}}
                **{{{messageTitle}}}**

                    {{#messageBodyItems}}
                 * {{.}}
                    {{/messageBodyItems}}

                [{{hash}}](https://github.com/tomasbjerre/git-changelog-lib/commit/{{hash}}) {{authorName}} *{{commitTime}}*

                   {{/commits}}
                  {{/issues}}
                 {{/issueTypes}}
                {{/tags}}
                """)
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
            # Git Changelog changelog

            Changelog of Git Changelog.

            ## test
            ### Bugs

            #### Mixed bugs


            **General bug sweep flagged in**


            [fa34fdd74dc8aec](https://github.com/tomasbjerre/git-changelog-lib/commit/fa34fdd74dc8aec) Alice *2020-01-01 00:10:00*

            ### CQ
            #### [CQ55](http://cq/55) 55



            **Address code quality flagged in**


            [127169f614ede3a](https://github.com/tomasbjerre/git-changelog-lib/commit/127169f614ede3a) Bob *2020-01-01 00:09:00*

            ### Incident
            #### [INC123](http://inc/INC123) INC123



            **Resolve incident logged in**


            [1e90a9c9ef6e7dc](https://github.com/tomasbjerre/git-changelog-lib/commit/1e90a9c9ef6e7dc) Alice *2020-01-01 00:08:00*

            ### Jira
            #### [JIR-1234](https://jiraserver/jira/browse/JIR-1234) Title of jira 1234



            **Improve reliability addressed in**


            [18d2df17b08e6e1](https://github.com/tomasbjerre/git-changelog-lib/commit/18d2df17b08e6e1) Alice *2020-01-01 00:06:00*

            #### [JIR-5262](https://jiraserver/jira/browse/JIR-5262) The Title of jira 5262



            **Refactor module addressed in**


            [f01290c7a6cc563](https://github.com/tomasbjerre/git-changelog-lib/commit/f01290c7a6cc563) Bob *2020-01-01 00:07:00*

            ### No issue supplied


            These commits has no issue.

            **Tidy up whitespace**


            [9427e3ef765e48e](https://github.com/tomasbjerre/git-changelog-lib/commit/9427e3ef765e48e) Bob *2020-01-01 00:11:00*

            ## 1.0
            ### GitHub
            #### [#20](https://github.com/tomasbjerre/made-up-mocked-repo/issues/20) Parameterized Jenkins&#x27; job error



            **Fix serious problem reported in**


            [24043e3a72ec224](https://github.com/tomasbjerre/git-changelog-lib/commit/24043e3a72ec224) Bob *2020-01-01 00:04:00*

            #### [#80](https://github.com/tomasbjerre/made-up-mocked-repo/issues/80) Create resource that can be invoked from scripts to trigger events



            **Add cool feature requested in**


            [68af4c90cc26bde](https://github.com/tomasbjerre/git-changelog-lib/commit/68af4c90cc26bde) Bob *2020-01-01 00:05:00*

            ### No issue supplied


            These commits has no issue.

            **Add feature A**

             * paragraph one
             * item one
             * item two

            [cf032fad475c3db](https://github.com/tomasbjerre/git-changelog-lib/commit/cf032fad475c3db) Alice *2020-01-01 00:01:00*

            **Initial commit**


            [3b2a25d3e86b87c](https://github.com/tomasbjerre/git-changelog-lib/commit/3b2a25d3e86b87c) Alice *2020-01-01 00:00:00*


            """);
  }

  @Test
  public void testIssueAdditionalField() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplateContent(
                """
                # Git Changelog changelog

                Changelog of Git Changelog.

                {{#issues}}
                  {{#additionalFields.customfield_10000}}
                ## {{name}} {{title}} {{additionalFields.customfield_10000}} has customfield_10000
                  {{/additionalFields.customfield_10000}}
                  {{^additionalFields.customfield_10000}}
                ## {{name}} {{title}} {{additionalFields.customfield_10000}} does not have customfield_10000
                  {{/additionalFields.customfield_10000}}
                  {{#additionalFields.customfield_10001}}
                ## {{name}} {{title}} has customfield_10001
                  {{/additionalFields.customfield_10001}}
                  {{^additionalFields.customfield_10001}}
                ## {{name}} {{title}} does not have customfield_10001
                  {{/additionalFields.customfield_10001}}
                  {{#additionalFields.customfield_10002}}
                ## {{name}} {{title}} has customfield_10002
                  {{/additionalFields.customfield_10002}}
                  {{^additionalFields.customfield_10002}}
                ## {{name}} {{title}} does not have customfield_10002
                  {{/additionalFields.customfield_10002}}
                  {{#hasAdditionalFields}}
                ### We have additional fields
                  {{/hasAdditionalFields}}
                  {{^hasAdditionalFields}}
                ### We do not have additional fields
                  {{/hasAdditionalFields}}
                  {{#each additionalFields}}
                #### for key "{{@key}}" we have value "{{this}}"
                  {{/each}}
                {{/issues}}
                """)
            .withJiraIssueAdditionalField("customfield_10000")
            .withJiraIssueAdditionalField("customfield_10002")
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
            # Git Changelog changelog

            Changelog of Git Changelog.

            ## Bugs Mixed bugs  does not have customfield_10000
            ## Bugs Mixed bugs does not have customfield_10001
            ## Bugs Mixed bugs does not have customfield_10002
            ### We do not have additional fields
            ## CQ 55  does not have customfield_10000
            ## CQ 55 does not have customfield_10001
            ## CQ 55 does not have customfield_10002
            ### We do not have additional fields
            ## GitHub Parameterized Jenkins&#x27; job error  does not have customfield_10000
            ## GitHub Parameterized Jenkins&#x27; job error does not have customfield_10001
            ## GitHub Parameterized Jenkins&#x27; job error does not have customfield_10002
            ### We do not have additional fields
            ## GitHub Create resource that can be invoked from scripts to trigger events  does not have customfield_10000
            ## GitHub Create resource that can be invoked from scripts to trigger events does not have customfield_10001
            ## GitHub Create resource that can be invoked from scripts to trigger events does not have customfield_10002
            ### We do not have additional fields
            ## Incident INC123  does not have customfield_10000
            ## Incident INC123 does not have customfield_10001
            ## Incident INC123 does not have customfield_10002
            ### We do not have additional fields
            ## Jira Title of jira 1234 Custom Field 10000 for jira 1234 has customfield_10000
            ## Jira Title of jira 1234 does not have customfield_10001
            ## Jira Title of jira 1234 does not have customfield_10002
            ### We have additional fields
            #### for key "customfield_10000" we have value "Custom Field 10000 for jira 1234"
            ## Jira The Title of jira 5262 Custom Field 10000 for jira 5262 has customfield_10000
            ## Jira The Title of jira 5262 does not have customfield_10001
            ## Jira The Title of jira 5262 does not have customfield_10002
            ### We have additional fields
            #### for key "customfield_10000" we have value "Custom Field 10000 for jira 5262"
            ## No issue supplied   does not have customfield_10000
            ## No issue supplied  does not have customfield_10001
            ## No issue supplied  does not have customfield_10002
            ### We do not have additional fields

            """);
  }
}
