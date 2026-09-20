package se.bjurr.gitchangelog.internal.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.bjurr.gitchangelog.api.model.Commit;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.settings.Settings;

/**
 * Verifies {@link Transformer}'s commit filtering against a small set of synthetic {@link
 * GitCommit}s, so each test spells out exactly which commits are involved rather than relying on
 * hashes from this project's own git history.
 */
public class CommitFilterTest {
  private static final String RELEASE_PLUGIN_MESSAGE_PATTERN = "^\\[Gradle Release Plugin\\].*$";

  private static final String RELEASE_COMMIT_2018_A_HASH = "release-commit-2018-a";
  private static final String RELEASE_COMMIT_2018_B_HASH = "release-commit-2018-b";
  private static final String REGULAR_COMMIT_2018_HASH = "regular-commit-2018";

  private static final String OLD_COMMIT_2016_HASH = "regular-commit-2016";
  private static final String NEW_COMMIT_2017_HASH = "regular-commit-2017";
  private static final String NEW_RELEASE_COMMIT_2017_HASH = "release-commit-2017";

  private List<GitCommit> commits;
  private Settings settings;
  private Date year2017;

  @BeforeEach
  public void before() throws Exception {
    this.commits =
        Arrays.asList(
            gitCommit(REGULAR_COMMIT_2018_HASH, "Some regular change", "2018-01-01"),
            gitCommit(
                RELEASE_COMMIT_2018_A_HASH, "[Gradle Release Plugin] release 1.0", "2018-02-01"),
            gitCommit(
                RELEASE_COMMIT_2018_B_HASH, "[Gradle Release Plugin] release 1.1", "2018-03-01"),
            gitCommit(OLD_COMMIT_2016_HASH, "Some old change", "2016-06-01"),
            gitCommit(NEW_COMMIT_2017_HASH, "Some new change", "2017-06-01"),
            gitCommit(
                NEW_RELEASE_COMMIT_2017_HASH, "[Gradle Release Plugin] release 0.9", "2017-07-01"));
    this.settings = new Settings();
    this.year2017 = new SimpleDateFormat("yyyy-MM-dd").parse("2017-01-01");
  }

  @Test
  public void testThatFilterWithIgnoreCommitsWithMessageWorks() throws Exception {
    this.settings.setIgnoreCommitsIfMessageMatches(RELEASE_PLUGIN_MESSAGE_PATTERN);

    final List<String> transformedCommits =
        this.hashes(new Transformer(this.settings).toCommits(this.commits));

    assertThat(transformedCommits) //
        .as("commits not matching the release-plugin message pattern are kept")
        .contains(REGULAR_COMMIT_2018_HASH, OLD_COMMIT_2016_HASH, NEW_COMMIT_2017_HASH);
    assertThat(transformedCommits) //
        .as("commits matching the release-plugin message pattern are removed")
        .doesNotContain(
            RELEASE_COMMIT_2018_A_HASH, RELEASE_COMMIT_2018_B_HASH, NEW_RELEASE_COMMIT_2017_HASH);
  }

  @Test
  public void testThatFilterWithIgnoreCommitsOlderThanWorks() throws Exception {
    this.settings.setIgnoreCommitsIfOlderThan(this.year2017);

    final List<String> transformedCommits =
        this.hashes(new Transformer(this.settings).toCommits(this.commits));

    assertThat(transformedCommits) //
        .as("commits older than 2017 are removed")
        .doesNotContain(OLD_COMMIT_2016_HASH);
    assertThat(transformedCommits) //
        .as("commits from 2017 onwards are kept")
        .contains(NEW_COMMIT_2017_HASH, NEW_RELEASE_COMMIT_2017_HASH);
  }

  @Test
  public void testThatFilterWithIgnoreCommitsWithMessageAndOlderThanWorks() throws Exception {
    this.settings.setIgnoreCommitsIfOlderThan(this.year2017);
    this.settings.setIgnoreCommitsIfMessageMatches(RELEASE_PLUGIN_MESSAGE_PATTERN);

    final List<String> transformedCommits =
        this.hashes(new Transformer(this.settings).toCommits(this.commits));

    assertThat(transformedCommits) //
        .as("commit from 2017 onwards, not matching the release-plugin message pattern, is kept")
        .contains(NEW_COMMIT_2017_HASH);
    assertThat(transformedCommits) //
        .as(
            "commit older than 2017, and commit matching the release-plugin message pattern, are"
                + " both removed")
        .doesNotContain(OLD_COMMIT_2016_HASH, NEW_RELEASE_COMMIT_2017_HASH);
  }

  private static GitCommit gitCommit(final String hash, final String message, final String date)
      throws Exception {
    return new GitCommit(
        "Author",
        "author@example.com",
        new SimpleDateFormat("yyyy-MM-dd").parse(date),
        message,
        hash,
        false,
        "",
        Collections.emptyList(),
        null);
  }

  private List<String> hashes(final List<Commit> commits) {
    final List<String> hashes = new ArrayList<>();
    for (final Commit c : commits) {
      hashes.add(c.getHashFull());
    }
    return hashes;
  }
}
