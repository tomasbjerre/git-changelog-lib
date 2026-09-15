package se.bjurr.gitchangelog.internal.semantic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.bjurr.gitchangelog.api.model.Commit;

public class ConventionalCommitParserTest {

  private static Commit commit(final String message) {
    return new Commit(
        "Author", "author@example.com", "2024-01-01", 0L, message, "abc1234567890abcdef", false);
  }

  @Test
  public void testThatContainsTypeAndScopeNotInFindsUnclaimedCommit() {
    final List<Commit> commits =
        List.of(
            commit("chore(deps): bump a"),
            commit("chore(ci): update workflow"),
            commit("chore: tidy up"));

    assertThat(
            ConventionalCommitParser.containsTypeAndScopeNotIn(
                commits, "chore", List.of("deps", "ci")))
        .isTrue();
  }

  @Test
  public void testThatContainsTypeAndScopeNotInIsFalseWhenEveryCommitIsExcluded() {
    final List<Commit> commits =
        List.of(commit("chore(deps): bump a"), commit("chore(ci): update workflow"));

    assertThat(
            ConventionalCommitParser.containsTypeAndScopeNotIn(
                commits, "chore", List.of("deps", "ci")))
        .isFalse();
  }

  @Test
  public void testThatContainsTypeAndScopeNotInIsFalseWhenTypeIsAbsent() {
    final List<Commit> commits = List.of(commit("feat: add thing"));

    assertThat(
            ConventionalCommitParser.containsTypeAndScopeNotIn(
                commits, "chore", List.of("deps", "ci")))
        .isFalse();
  }

  @Test
  public void testThatContainsTypeAndScopeNotInIsTrueWhenNoCommitsAreExcluded() {
    final List<Commit> commits = List.of(commit("chore: tidy up"));

    assertThat(
            ConventionalCommitParser.containsTypeAndScopeNotIn(
                commits, "chore", List.of("deps", "ci")))
        .isTrue();
  }

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
