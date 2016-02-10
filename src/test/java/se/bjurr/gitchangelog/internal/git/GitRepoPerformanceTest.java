package se.bjurr.gitchangelog.internal.git;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import se.bjurr.gitchangelog.internal.git.model.GitTag;

import com.google.common.base.Stopwatch;

public class GitRepoPerformanceTest {
 private Stopwatch stopwatch;
 private static final Logger LOG = Logger.getLogger(GitRepoPerformanceTest.class.getSimpleName());
 private GitRepo gitRepo;

 @BeforeClass
 public static void beforeClass() {
 }

 @Before
 public void before() {
  File gitRepoFile = new File("/home/bjerre/workspace/spring-framework");
  gitRepo = new GitRepo(gitRepoFile);
  LOG.info("Using repo:\n" + gitRepo);
  this.stopwatch = Stopwatch.createStarted();
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
    ObjectId fromCommit = gitRepo.getRef(from.getName());
    ObjectId toCommit = gitRepo.getRef(to.getName());
    String str = from.getName() + " -> " + to.getName();
    LOG.info(str);
    gitRepo.getDiff(fromCommit, toCommit);
   }
  }
 }
}
