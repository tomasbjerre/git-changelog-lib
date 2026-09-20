package se.bjurr.gitchangelog.api.helpers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** A realistic conventional-commit changelog template, grouped by release tag. */
public class ConventionalChangelogHelperTest extends AbstractHandlebarsHelperTest {

  @Test
  public void testThatConventionalChangelogCanBeRendered() throws Exception {
    final String rendered =
        this.baseBuilder //
            .withTemplateContent(
                """
                # Changelog

                {{#tags}}
                {{#ifReleaseTag .}}
                ## [{{name}}](https://gitlab.com/html-validate/html-validate/compare/{{name}}) ({{tagDate .}})

                \t{{#ifContainsBreaking commits}}
                ### Breaking changes

                \t\t{{#commits}}
                \t\t\t{{#ifCommitBreaking . }}
                 - {{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}} {{{commitDescription .}}} ([{{hash}}](https://gitlab.com/html-validate/html-validate/commit/{{hashFull}}))
                \t\t\t{{/ifCommitBreaking}}
                \t\t{{/commits}}
                \t{{/ifContainsBreaking}}

                \t{{#ifContainsType commits type='feat'}}
                ### Features

                \t\t{{#commits}}
                \t\t\t{{#ifCommitType . type='feat'}}
                 - {{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}} {{{commitDescription .}}} ([{{hash}}](https://gitlab.com/html-validate/html-validate/commit/{{hashFull}}))
                \t\t\t{{/ifCommitType}}
                \t\t{{/commits}}
                \t{{/ifContainsType}}

                \t{{#ifContainsType commits type='fix'}}
                ### Bug Fixes

                \t\t{{#commits}}
                \t\t\t{{#ifCommitType . type='fix'}}
                 - {{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}} {{{commitDescription .}}} ([{{hash}}](https://gitlab.com/html-validate/html-validate/commit/{{hashFull}}))
                \t\t\t{{/ifCommitType}}
                \t\t{{/commits}}
                \t{{/ifContainsType}}

                {{/ifReleaseTag}}
                {{/tags}}""")
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
