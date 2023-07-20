package se.bjurr.gitchangelog.internal.model;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.eclipse.jgit.lib.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.bjurr.gitchangelog.api.InclusivenessStrategy;
import se.bjurr.gitchangelog.api.model.Commit;
import se.bjurr.gitchangelog.internal.git.GitRepo;
import se.bjurr.gitchangelog.internal.git.RevisionBoundary;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.settings.Settings;

public class CommitFilterTest {
  private static final String LATEST_COMMIT_HASH = "0fc7cacfdf677dcb25f0ba5996f2906f69b0ccac";

  private static final String TEST_COMMIT_MESSAGE_PATTERN = "^\\[Gradle Release Plugin\\].*$";
  private static final String MATCHING_COMMIT_1_HASH = "921b472b2db9b39ed5e6c63c6f5ca4bd80df8eb5";
  private static final String MATCHING_COMMIT_2_HASH = "2cd70e03b7e2d5af63a9428d78d0ddcfaffde7e3";
  private static final String NONMATCHING_COMMIT_HASH = "92fca3cfbb690aee22a9d6909cf0f823b878ebbf";

  private static final String LATEST_2016_COMMIT_HASH = "4c554557c16f68d27214956db3280492c2bd53dc";
  private static final String FIRST_2017_COMMIT_HASH = "e569b0c8dfe5957c3552ea241a21033b24ad1d97";
  private static final String A_2017_COMMIT_HASH_MATCHING_MESSAGE_PATTERN =
      "4f8d3e3c7bcc24f53aff2ebf696a2eaab5217126";
  private GitRepo gitRepo;
  private List<GitCommit> commits;
  private Settings settings;

  private Date date2017;

  @Before
  public void before() throws Exception {
    this.gitRepo =
        new GitRepo(
            Paths.get(CommitFilterTest.class.getResource("/github-issues.json").toURI()).toFile());
    this.commits =
        this.gitRepo
            .getGitRepoData(
                new RevisionBoundary<ObjectId>(
                    this.gitRepo.getCommit(ZERO_COMMIT), InclusivenessStrategy.DEFAULT),
                new RevisionBoundary<ObjectId>(
                    this.gitRepo.getCommit(LATEST_COMMIT_HASH), InclusivenessStrategy.DEFAULT),
                null,
                Optional.of(""))
            .getGitCommits();
    this.settings =
        Settings.fromFile(
            CommitFilterTest.class
                .getResource("/settings/git-changelog-test-settings.json")
                .toURI()
                .toURL());
    this.date2017 = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").parse("2017-01-01 00:00:00");
  }

  @Test
  public void testThatFilterWithIgnoreCommitsWithMessageWorks() throws Exception {
    this.settings.setIgnoreCommitsIfMessageMatches(TEST_COMMIT_MESSAGE_PATTERN);
    final List<String> transformedCommits =
        this.hashes(new Transformer(this.settings).toCommits(this.commits));
    assertThat(transformedCommits).contains(NONMATCHING_COMMIT_HASH);
    assertThat(transformedCommits).doesNotContain(MATCHING_COMMIT_1_HASH, MATCHING_COMMIT_2_HASH);
  }

  @Test
  public void testThatFilterWithIgnoreCommitsOlderThanWorks() throws Exception {
    this.settings.setIgnoreCommitsIfMessageMatches("");
    this.settings.setIgnoreCommitsIfOlderThan(this.date2017);
    final List<String> transformedCommits =
        this.hashes(new Transformer(this.settings).toCommits(this.commits));
    assertThat(transformedCommits).doesNotContain(LATEST_2016_COMMIT_HASH);
    assertThat(transformedCommits)
        .contains(FIRST_2017_COMMIT_HASH, A_2017_COMMIT_HASH_MATCHING_MESSAGE_PATTERN);
  }

  @Test
  public void testThatFilterWithIgnoreCommitsWithMessageAndOlderThanWorks() throws Exception {
    this.settings.setIgnoreCommitsIfOlderThan(this.date2017);
    this.settings.setIgnoreCommitsIfMessageMatches(TEST_COMMIT_MESSAGE_PATTERN);
    final List<String> transformedCommits =
        this.hashes(new Transformer(this.settings).toCommits(this.commits));
    assertThat(transformedCommits).contains(FIRST_2017_COMMIT_HASH);
    assertThat(transformedCommits)
        .doesNotContain(LATEST_2016_COMMIT_HASH, A_2017_COMMIT_HASH_MATCHING_MESSAGE_PATTERN);
  }

  @After
  public void after() throws Exception {
    this.gitRepo.close();
  }

  private List<String> hashes(final List<Commit> commits) {
    final List<String> hashes = new ArrayList<>();
    for (final Commit c : commits) {
      hashes.add(c.getHashFull());
    }
    return hashes;
  }
}
