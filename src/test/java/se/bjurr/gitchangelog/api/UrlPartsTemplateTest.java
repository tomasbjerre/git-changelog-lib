package se.bjurr.gitchangelog.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** {@code {{ownerName}}}, {@code {{repoName}}}, {@code {{urlParts}}} and {@code eachUrlPart}. */
public class UrlPartsTemplateTest extends AbstractTemplatesTest {

  @Test
  public void testUrlParts() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplateContent(
                """
                ownerName: {{ownerName}}
                repoName: {{repoName}}
                urlParts: {{urlParts}}
                urlParts.0: {{urlParts.0}}
                urlParts.1: {{urlParts.1}}
                urlParts.2: {{urlParts.2}}""")
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
            ownerName: tomasbjerre
            repoName: git-changelog-lib
            urlParts: [git-changelog-lib, tomasbjerre, git@github.com]
            urlParts.0: git-changelog-lib
            urlParts.1: tomasbjerre
            urlParts.2: git@github.com
            """);
  }

  @Test
  public void testEachUrlPart() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplateContent(
                """
                ownerName: {{ownerName}}
                repoName: {{repoName}}
                urlParts: {{urlParts}}
                https://github.com/{{#eachUrlPart .}}{{#if @first}}{{else}}{{.}}/{{/if}}{{/eachUrlPart}}
                """)
            .render();

    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
            """
            ownerName: tomasbjerre
            repoName: git-changelog-lib
            urlParts: [git-changelog-lib, tomasbjerre, git@github.com]
            https://github.com/tomasbjerre/git-changelog-lib/
            """);
  }
}
