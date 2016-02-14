package se.bjurr.gitchangelog.api;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.hash.Hashing.sha1;
import static org.eclipse.jgit.lib.ObjectId.fromString;
import static org.slf4j.LoggerFactory.getLogger;
import static se.bjurr.gitchangelog.api.FakeGitRepo.DAY_ZERO;
import static se.bjurr.gitchangelog.api.FakeGitRepo.TIME_DAY;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;

import se.bjurr.gitchangelog.internal.git.GitRepoData;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

public class FakeRepo {
 private static final Logger logger = getLogger(FakeRepo.class);

 private static String nextHash(AtomicInteger atomicInteger) {
  return sha1().hashInt(atomicInteger.incrementAndGet()).toString();
 }

 public static FakeGitRepo fakeRepo() {

  AtomicInteger atomicInteger = new AtomicInteger();
  Map<Integer, String> hashes = newHashMap();
  for (Integer i = 0; i < 100; i++) {
   hashes.put(i, nextHash(atomicInteger));
  }

  FakeGitRepo fakeGitRepo = new FakeGitRepo()
    .withCommit(
      new GitCommit("Tomas B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 1), "Adding stuff without issue",
        hashes.get(0)))
    .withCommit(
      new GitCommit("T B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 2), "Adding stuff with gh 20 #20", hashes
        .get(1)))
    .withCommit(
      new GitCommit("Tomas B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 3),
        "Adding stuff with a jira \n  JIR-5262", hashes.get(2)))
    .withCommit(
      new GitCommit("T B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 4), "Adding stuff, no issue", hashes
        .get(3)))
    .withCommit(
      new GitCommit("Tomas B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 5), "Adding stuff #12 gh 12", hashes
        .get(4)))
    .withCommit(
      new GitCommit("T B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 6), "This is 1.0 tagged commit", hashes
        .get(5)))
    .withCommit(
      new GitCommit("Tomas B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 7), "Adding cq stuff with CQ CQ1234",
        hashes.get(6)))
    .withCommit(
      new GitCommit("T B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 8), "Adding stuff\n with gh again #30",
        hashes.get(7)))
    .withCommit(
      new GitCommit("Tomas B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 9),
        "Adding stuff with a jira JIR-1234", hashes.get(8)))
    .withCommit(
      new GitCommit("T B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 10), "More stuff tagged with bug #bug",
        hashes.get(9)));

  fakeGitRepo//
    .withTag(
      new GitTag("No tag", //
        fakeGitRepo.getGitRepoData(fromString(hashes.get(6)), fromString(hashes.get(hashes.size() - 1)), "No tag")
          .getGitCommits()))//
    .withTag(new GitTag("refs/tags/1.0", //
      fakeGitRepo.getGitRepoData(fromString(ZERO_COMMIT), fromString(hashes.get(5)), "No tag").getGitCommits()));

  logger.debug("Created fake repo for testing:");
  GitRepoData gitRepoData = fakeGitRepo.getGitRepoData(fromString(ZERO_COMMIT),
    fakeGitRepo.getRef("refs/heads/master"), "Untagged Name");
  for (GitCommit gitCommit : gitRepoData.getGitCommits()) {
   logTag(gitCommit.getHash(), gitRepoData.getGitTags());
   logger.debug(" " + gitCommit.getHash() + " " + gitCommit.getMessage());
  }

  return fakeGitRepo;
 }

 private static void logTag(String hash, List<GitTag> tags) {
  for (GitTag gitTag : tags) {
   if (gitTag.getGitCommit().getHash().equals(hash)) {
    logger.debug("Tag: " + gitTag.getName());
   }
  }
 }
}
