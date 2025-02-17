package se.bjurr.gitchangelog.internal.semantic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.Test;

public class ConventionalCommitParserTest {

  @Test
  public void testThatDescriptionCanBeParsed() {
    assertThat(ConventionalCommitParser.commitDescription("feat: a (refs #1)")).isEqualTo("a");
    assertThat(ConventionalCommitParser.commitDescription("feat: a (refs #123)")).isEqualTo("a");
    assertThat(ConventionalCommitParser.commitDescription("feat: a (refs J-1)")).isEqualTo("a");
    assertThat(ConventionalCommitParser.commitDescription("feat: a (refs JE-12)")).isEqualTo("a");
    assertThat(ConventionalCommitParser.commitDescription("feat(123): add polish language"))
        .isEqualTo("add polish language");
    assertThat(ConventionalCommitParser.commitDescription("feat(123) : add polish language"))
        .isEqualTo("add polish language");
  }

  @Test
  public void testThatScopeCanBeParsed() {
    this.test("feat(core/node): add polish language", List.of("core/node"));
    this.test("feat(lang): add polish language", List.of("lang"));
    this.test("feat(la-ng): add polish language", List.of("la-ng"));
    this.test("feat(l): add polish language", List.of("l"));
    this.test("feat(123): add polish language", List.of("123"));
    this.test("feat(org.test): add polish language", List.of("org.test"));
    this.test("feat(org-test): add polish language", List.of("org-test"));
    this.test("feat(123) : add polish language", List.of("123"));
    this.test(
        "feat(namespaceA:namespaceB): add polish language", List.of("namespaceA", "namespaceB"));
    this.test(
        "feat(namespaceA: namespaceB): add polish language", List.of("namespaceA", "namespaceB"));
    this.test(
        "feat(namespaceA :namespaceB): add polish language", List.of("namespaceA", "namespaceB"));
    this.test(
        "feat(namespaceA : namespaceB): add polish language", List.of("namespaceA", "namespaceB"));
    this.test(
        "feat(namespaceA,namespaceB): add polish language", List.of("namespaceA", "namespaceB"));
    this.test(
        "feat(namespaceA, namespaceB): add polish language", List.of("namespaceA", "namespaceB"));
    this.test(
        "feat(namespaceA ,namespaceB): add polish language", List.of("namespaceA", "namespaceB"));
    this.test(
        "feat(namespaceA , namespaceB): add polish language", List.of("namespaceA", "namespaceB"));
  }

  private void test(final String given, final List<String> expected) {
    assertThat(ConventionalCommitParser.commitScopes(given)).hasSameElementsAs(expected);
  }
}
