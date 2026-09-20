package se.bjurr.gitchangelog.api.helpers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * A catalog of nearly every built-in Handlebars helper this library ships: breaking-change
 * detection (both the "!" marker and a BREAKING CHANGE footer), footers, paragraphs, revert,
 * scopes, refs, fixes, release-tag detection, and string helpers.
 */
public class BuiltInHelpersTest extends AbstractHandlebarsHelperTest {

  @Test
  public void testThatBuiltInHelperMethodsCanBeUsed() throws Exception {
    final String rendered =
        this.baseBuilder //
            .withTemplateContent(
                """

                Test template:

                Tags:

                {{#tags}}
                 * {{.}}
                {{/tags}}


                Commits:

                {{#commits}}
                 * {{.}}
                {{/commits}}


                ifMatches and subString:
                {{#commits}}
                 {{#eachCommitFixes .}}
                  {{#ifMatches . "^[A-Z]+-[0-9]+"}}
                   fixes : "{{subString . 0 3}}" and number {{subString . 4}}
                  {{/ifMatches}}
                 {{/eachCommitFixes}}
                {{/commits}}


                {{#tags}}
                 {{name}} Unreleased ? {{#ifEquals name "Unreleased"}} yes {{else}} no {{/ifEquals}}
                {{/tags}}


                ## breaking changes:
                 {{#ifContainsBreaking commits}}
                  contains breaking changes
                  {{#commits}}
                   {{#ifCommitBreaking .}}
                    is breaking: {{hash}}\s
                   {{/ifCommitBreaking}}
                  {{/commits}}
                 {{/ifContainsBreaking}}


                ## feat utils:
                 {{#ifContainsTypeAndScope commits type="feat" scope="utils"}}
                  feat and utils exist!
                  {{#commits}}
                   {{#ifCommitType . type="feat"}}
                \t{{#ifCommitScope . scope="utils"}}
                \t is feat and utils: {{hash}}
                \t{{/ifCommitScope}}
                   {{/ifCommitType}}
                  {{/commits}}
                 {{/ifContainsTypeAndScope}}


                ## Footers
                {{#commits}}
                {{#ifCommitHasFooters}}
                Has footers: {{hash}}\s
                {{/ifCommitHasFooters}}
                {{#eachCommitFooter .}}
                 - "{{token}}"
                ```
                {{#ifFooterHasValue .}}
                ----footer-value-begins----
                {{{value}}}\s
                ----footer-value-ends---
                {{/ifFooterHasValue}}
                ```

                {{/eachCommitFooter}}
                {{#eachCommitFooter . tokenMatching="first.*|Refs"}}
                 - "{{token}}" matches `first.*|Refs`

                {{/eachCommitFooter}}
                {{/commits}}


                ## Paragraphs
                {{#commits}}
                {{#ifCommitHasParagraphs}}
                Has paragraphs: {{hash}}\s
                {{/ifCommitHasParagraphs}}
                ```
                {{#eachCommitParagraph .}}
                ----paragraph-begins----
                {{{.}}}
                ----paragraph-ends---
                ```


                {{/eachCommitParagraph}}
                {{/commits}}


                {{#tags}}
                 tag: {{.}}
                 {{#ifReleaseTag .}}
                  "{{.}}" is a release tag
                 {{/ifReleaseTag}}

                 {{#ifContainsType commits type="fix"}}
                  commits contains fix
                 {{/ifContainsType}}

                 {{#ifContainsTypeOtherThan commits type="fix"}}
                  commits contains other types than fix
                 {{/ifContainsTypeOtherThan}}

                 date: {{tagDate .}}\s

                {{/tags}}


                {{#commits}}
                 Commit: {{.}}
                 date: {{commitDate .}}\s

                 {{#ifCommitType . type="revert"}} is type revert {{/ifCommitType}}
                 {{#ifCommitType . type="fix"}} is type fix {{/ifCommitType}}
                 {{#ifCommitType . type=""}} commit has no type {{/ifCommitType}}
                 {{#ifCommitType . type="fix|revert"}} commit has type fix or revert {{/ifCommitType}}
                 {{#ifCommitTypeOtherThan . type="fix|revert"}} commit has not type fix or revert {{/ifCommitTypeOtherThan}}

                 {{#ifCommitScope . scope="utils"}} is scope utils {{/ifCommitScope}}

                 {{#eachCommitScope .}}
                  scope: {{.}}
                 {{/eachCommitScope}}

                 description: {{commitDescription .}}

                 {{#ifCommitType . type="revert"}}
                   this reverted commit: {{revertedCommit .}}
                 {{/ifCommitType}}

                 {{#eachCommitRefs .}}
                  ref: {{.}}
                 {{/eachCommitRefs}}

                 {{#eachCommitFixes .}}
                  fix: {{.}}
                 {{/eachCommitFixes}}


                {{/commits}}


                ## StringHelpers

                {{capitalizeFirst 'hello'}}""")
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
            \t is feat and utils: b8f77dd1eb3fe6c


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
}
