package se.bjurr.gitchangelog.internal.model;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import com.google.common.base.Optional;
import com.google.common.io.Resources;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.bjurr.gitchangelog.api.model.Commit;
import se.bjurr.gitchangelog.internal.git.GitRepo;
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
    gitRepo = new GitRepo(new File(Resources.getResource("github-issues.json").getFile()));
    commits =
        newArrayList(
            gitRepo
                .getGitRepoData(
                    gitRepo.getCommit(ZERO_COMMIT),
                    gitRepo.getCommit(LATEST_COMMIT_HASH),
                    null,
                    Optional.of(""))
                .getGitCommits());
    settings =
        Settings.fromFile(getResource("settings/git-changelog-test-settings.json").toURI().toURL());
    date2017 = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").parse("2017-01-01 00:00:00");
  }

  @Test
  public void testThatFilterWithIgnoreCommitsWithMessageWorks() throws Exception {
    settings.setIgnoreCommitsIfMessageMatches(TEST_COMMIT_MESSAGE_PATTERN);
    List<String> transformedCommits = hashes(new Transformer(settings).toCommits(commits));
    assertThat(transformedCommits).contains(NONMATCHING_COMMIT_HASH);
    assertThat(transformedCommits).doesNotContain(MATCHING_COMMIT_1_HASH, MATCHING_COMMIT_2_HASH);
  }

  @Test
  public void testThatFilterWithIgnoreCommitsOlderThanWorks() throws Exception {
    settings.setIgnoreCommitsIfMessageMatches("");
    settings.setIgnoreCommitsIfOlderThan(date2017);
    List<String> transformedCommits = hashes(new Transformer(settings).toCommits(commits));
    assertThat(transformedCommits).doesNotContain(LATEST_2016_COMMIT_HASH);
    assertThat(transformedCommits)
        .contains(FIRST_2017_COMMIT_HASH, A_2017_COMMIT_HASH_MATCHING_MESSAGE_PATTERN);
  }

  @Test
  public void testThatFilterWithIgnoreCommitsWithMessageAndOlderThanWorks() throws Exception {
    settings.setIgnoreCommitsIfOlderThan(date2017);
    settings.setIgnoreCommitsIfMessageMatches(TEST_COMMIT_MESSAGE_PATTERN);
    List<String> transformedCommits = hashes(new Transformer(settings).toCommits(commits));
    assertThat(transformedCommits).contains(FIRST_2017_COMMIT_HASH);
    assertThat(transformedCommits)
        .doesNotContain(LATEST_2016_COMMIT_HASH, A_2017_COMMIT_HASH_MATCHING_MESSAGE_PATTERN);
  }

  @After
  public void after() throws Exception {
    gitRepo.close();
  }

  private List<String> hashes(List<Commit> commits) {
    List<String> hashes = newArrayList();
    for (Commit c : commits) {
      hashes.add(c.getHashFull());
    }
    return hashes;
  }
}
