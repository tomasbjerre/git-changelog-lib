package se.bjurr.gitchangelog.test;

import static com.google.common.base.Stopwatch.createStarted;
import static java.util.concurrent.TimeUnit.SECONDS;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;

import se.bjurr.gitchangelog.api.GitChangelogApi;
import se.bjurr.gitchangelog.internal.git.GitRepo;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

import com.google.common.base.Stopwatch;

public class LibPerformanceTest {
 private static final String GIT_REPO_DIR = "/home/bjerre/workspace/spring-framework";
 private Stopwatch stopwatch;
 private GitChangelogApi gitChangelogApiBuilder;
 private GitRepo gitRepo;
 private static final Logger LOG = Logger.getLogger(LibPerformanceTest.class.getSimpleName());

 @Before
 public void before() {
  gitChangelogApiBuilder = gitChangelogApiBuilder()//
    .withFromRepo(GIT_REPO_DIR);
  gitRepo = new GitRepo(new File(GIT_REPO_DIR));
  this.stopwatch = createStarted();
 }

 @After
 public void after() {
  long elapsedSeconds = stopwatch.elapsed(SECONDS);
  LOG.info("Took: " + elapsedSeconds + "s");
 }

 // @Test
 public void testThatGimmitsBetweenTagsCanBeFound() {
  List<GitTag> allTags = gitRepo.getTags();
  int i = 0;
  for (GitTag from : allTags) {
   for (GitTag to : allTags) {
    i++;
    LOG.info(i + "/" + allTags.size() * allTags.size() + " in " + stopwatch.elapsed(SECONDS) + "s, now testing "
      + from.getName() + " -> " + to.getName());
    if (from.getName().equals(to.getName())) {
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
