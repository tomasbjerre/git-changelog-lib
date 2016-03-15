package se.bjurr.gitchangelog.test;

import static com.google.common.base.Stopwatch.createStarted;
import static java.util.concurrent.TimeUnit.SECONDS;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_MASTER;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.bjurr.gitchangelog.api.GitChangelogApi;
import se.bjurr.gitchangelog.internal.git.GitRepo;
import se.bjurr.gitchangelog.internal.git.GitRepoData;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

import com.google.common.base.Stopwatch;

public class LibPerformanceTest {
 private static final String UNTAGGED_NAME = "Untagged Name";
 private static final String GIT_REPO_DIR = "/home/bjerre/workspace/spring-framework";
 private Stopwatch stopwatch;
 private GitChangelogApi gitChangelogApiBuilder;
 private GitRepo gitRepo;
 private static final Logger LOG = Logger.getLogger(LibPerformanceTest.class.getSimpleName());

 @Before
 public void before() throws Exception {
  gitChangelogApiBuilder = gitChangelogApiBuilder()//
    .withFromRepo(GIT_REPO_DIR);
  this.stopwatch = createStarted();
  File file = new File(GIT_REPO_DIR);
  if (file.exists()) {
   gitRepo = new GitRepo(file);
  } else {
   LOG.info("Did not find " + GIT_REPO_DIR + " will not run performance test.");
  }
 }

 @After
 public void after() {
  long elapsedSeconds = stopwatch.elapsed(SECONDS);
  LOG.info("Took: " + elapsedSeconds + "s");
 }

 @Test
 public void testThatGimmitsBetweenTagsCanBeFound() throws Exception {
  if (gitRepo == null) {
   return;
  }

  ObjectId fromId = gitRepo.getCommit(ZERO_COMMIT);
  ObjectId toId = gitRepo.getRef(REF_MASTER);
  GitRepoData gitRepoData = gitRepo.getGitRepoData(fromId, toId, UNTAGGED_NAME);
  List<GitTag> allTags = gitRepoData.getGitTags();
  int i = 0;
  for (GitTag from : allTags) {
   for (GitTag to : allTags) {
    i++;
    LOG.info(i + "/" + allTags.size() * allTags.size() + " in " + stopwatch.elapsed(SECONDS) + "s, now testing "
      + from.getName() + " -> " + to.getName());
    if (from.getName().equals(to.getName()) || from.getName().equals(UNTAGGED_NAME)
      || to.getName().equals(UNTAGGED_NAME)) {
     continue;
    }
    String str = from.getName() + " -> " + to.getName();
    LOG.info(str);
    gitChangelogApiBuilder//
      .withFromRef(from.getName())//
      .withToRef(to.getName())//
      .render();
   }
  }
 }
}
