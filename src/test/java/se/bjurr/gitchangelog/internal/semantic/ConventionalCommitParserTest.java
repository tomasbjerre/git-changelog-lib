package se.bjurr.gitchangelog.internal.semantic;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ConventionalCommitParserTest {

  @Test
  public void testThatDescriptionCanBeParsed() {
    assertThat(ConventionalCommitParser.commitDescription("feat: a (refs #1)")).isEqualTo("a");
    assertThat(ConventionalCommitParser.commitDescription("feat: a (refs #123)")).isEqualTo("a");
    assertThat(ConventionalCommitParser.commitDescription("feat: a (refs J-1)")).isEqualTo("a");
    assertThat(ConventionalCommitParser.commitDescription("feat: a (refs JE-12)")).isEqualTo("a");
  }

  @Test
  public void testThatScopeCanBeParsed() {
    assertThat(ConventionalCommitParser.commitScopes("feat(lang): add polish language"))
        .containsOnly("lang");
    assertThat(ConventionalCommitParser.commitScopes("feat(la-ng): add polish language"))
        .containsOnly("la-ng");
    assertThat(ConventionalCommitParser.commitScopes("feat(l): add polish language"))
        .containsOnly("l");
    assertThat(ConventionalCommitParser.commitScopes("feat(123): add polish language"))
        .containsOnly("123");
    assertThat(ConventionalCommitParser.commitScopes("feat(org.test): add polish language"))
        .containsOnly("org.test");
  }
}
