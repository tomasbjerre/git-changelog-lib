package se.bjurr.gitchangelog.test;

import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_MASTER;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import org.eclipse.jgit.lib.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.bjurr.gitchangelog.api.GitChangelogApi;
import se.bjurr.gitchangelog.internal.git.GitRepo;
import se.bjurr.gitchangelog.internal.git.GitRepoData;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

public class LibPerformanceTest {
  private static final String GIT_REPO_DIR = "/home/bjerre/workspace/spring-framework";
  private static final Logger LOG = Logger.getLogger(LibPerformanceTest.class.getSimpleName());
  private static final String UNTAGGED_NAME = "Untagged Name";
  private GitChangelogApi gitChangelogApiBuilder;
  private GitRepo gitRepo;

  @After
  public void after() {
    // final long elapsedSeconds = this.stopwatch.elapsed(SECONDS);
    // LOG.info("Took: " + elapsedSeconds + "s");
  }

  @Before
  public void before() throws Exception {
    this.gitChangelogApiBuilder =
        gitChangelogApiBuilder() //
            .withFromRepo(GIT_REPO_DIR);
    // this.stopwatch = createStarted();
    final File file = new File(GIT_REPO_DIR);
    if (file.exists()) {
      this.gitRepo = new GitRepo(file);
    } else {
      LOG.info("Did not find " + GIT_REPO_DIR + " will not run performance test.");
    }
  }

  @Test
  public void testThatGimmitsBetweenTagsCanBeFound() throws Exception {
    if (this.gitRepo == null) {
      return;
    }
    LOG.info("Running performance test");

    final ObjectId fromId = this.gitRepo.getCommit(ZERO_COMMIT);
    final ObjectId toId = this.gitRepo.getRef(REF_MASTER);
    final GitRepoData gitRepoData =
        this.gitRepo.getGitRepoData(fromId, toId, UNTAGGED_NAME, Optional.<String>empty());
    // LOG.info(this.stopwatch.elapsed(SECONDS) + "s. Done zero to master.");
    final List<GitTag> allTags = gitRepoData.getGitTags();
    for (final GitTag from : allTags) {
      // LOG.info(this.stopwatch.elapsed(SECONDS) + "s. From " + from.getName());
      for (final GitTag to : allTags) {
        // LOG.info(this.stopwatch.elapsed(SECONDS) + "s.  --> " + to.getName());
        // LOG.info(
        //    this.stopwatch.elapsed(SECONDS)
        //        + "s.      "
        //        + i
        //        + "/"
        //        + allTags.size() * allTags.size());
        if (from.getName().equals(to.getName())
            || from.getName().equals(UNTAGGED_NAME)
            || to.getName().equals(UNTAGGED_NAME)) {
          continue;
        }
        this.gitChangelogApiBuilder //
            .withFromRef(from.getName()) //
            .withToRef(to.getName()) //
            .render();
        // LOG.info(this.stopwatch.elapsed(SECONDS) + "s.      Done");
      }
    }
  }
}
