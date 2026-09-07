package se.bjurr.gitchangelog.internal.issues;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.model.ParsedIssue;
import se.bjurr.gitchangelog.internal.settings.Settings;
import se.bjurr.gitchangelog.internal.settings.SettingsIssue;

public class IssueParserTest {

  private static GitCommit commit(final String message) {
    return new GitCommit("Name", "name@example.com", new Date(0), message, "abc123", false);
  }

  @Test
  public void testThatGitLabIssuesAreParsedWithoutAProjectName() {
    final Settings settings = new Settings();
    settings.setGitLabEnabled(true);
    settings.setGitLabServer("https://gitlab.com/");
    // No project name. It is only derived from the origin URL when no server is given.

    final List<ParsedIssue> actual =
        new IssueParser(settings, List.of(commit("Fixing #1234"))).parseForIssues(true);

    assertThat(actual).hasSize(1);
    assertThat(actual.get(0).getIssue()).isEqualTo("1234");
    assertThat(actual.get(0).getName()).isEqualTo("GitLab");
  }

  @Test
  public void testThatIssuesContainingDollarSignsCanBeRendered() {
    final Settings settings = new Settings();
    settings.setCustomIssues(
        List.of(
            new SettingsIssue(
                "Incident",
                "\\$[A-Z]+-[0-9]+",
                "http://inc/${PATTERN_GROUP}",
                "Incident ${PATTERN_GROUP_0}")));

    final List<ParsedIssue> actual =
        new IssueParser(settings, List.of(commit("Fixing $ABC-1 today"))).parseForIssues(false);

    assertThat(actual).hasSize(1);
    assertThat(actual.get(0).getLink()).isEqualTo("http://inc/$ABC-1");
    assertThat(actual.get(0).getTitle()).contains("Incident $ABC-1");
  }
}
