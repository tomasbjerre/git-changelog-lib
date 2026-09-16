package se.bjurr.gitchangelog.internal.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.bjurr.gitchangelog.api.model.Commit;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.settings.Settings;
import se.bjurr.gitchangelog.internal.settings.SettingsIssue;

public class TransformerTest {

  private List<SettingsIssue> noIssues;
  private List<SettingsIssue> oneIssue;
  private Transformer transformer;
  private final String message =
      "INC123 message title\n\n * INC456 The first item\n * The second item";

  @BeforeEach
  public void before() {
    this.noIssues = Arrays.asList();
    final SettingsIssue issue1 =
        new SettingsIssue("Issue Name", "INC[0-9]*", "http://inc/${PATTERN_GROUP}", null);
    this.oneIssue = Arrays.asList(issue1);
    this.transformer = new Transformer(new Settings());
  }

  @Test
  public void testThatMessageCanBeTransformedWithIssues() {
    assertThat(this.transformer.toMessage(false, this.oneIssue, this.message)) //
        .isEqualTo(this.message);
    assertThat(this.transformer.toMessage(false, this.noIssues, this.message)) //
        .isEqualTo(this.message);
  }

  @Test
  public void testThatMessageCanBeTransformedWithoutIssues() {
    assertThat(this.transformer.toMessage(true, this.oneIssue, this.message)) //
        .isEqualTo(" message title\n\n *  The first item\n * The second item");
    assertThat(this.transformer.toMessage(true, this.noIssues, this.message)) //
        .isEqualTo(this.message);
  }

  @Test
  public void testThatMessageNotesArePropagatedToCommit() {
    final GitCommit gitCommit =
        new GitCommit(
            "Author",
            "author@example.com",
            new Date(),
            "A commit message",
            "abc1234567890abcdef",
            false,
            "A note on the commit",
            Collections.emptyList(),
            null);

    final List<Commit> commits = this.transformer.toCommits(Arrays.asList(gitCommit));

    assertThat(commits).hasSize(1);
    assertThat(commits.get(0).getMessageNotes()).isEqualTo("A note on the commit");
  }

  @Test
  public void testThatFilesArePropagatedToCommit() {
    final GitCommit gitCommit =
        new GitCommit(
            "Author",
            "author@example.com",
            new Date(),
            "A commit message",
            "abc1234567890abcdef",
            false,
            "",
            Arrays.asList("first.txt", "second.txt"),
            null);

    final List<Commit> commits = this.transformer.toCommits(Arrays.asList(gitCommit));

    assertThat(commits).hasSize(1);
    assertThat(commits.get(0).getFiles()) //
        .containsExactly("first.txt", "second.txt");
  }
}
