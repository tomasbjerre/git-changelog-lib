package se.bjurr.gitchangelog.api;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.hash.Hashing.sha1;
import static org.eclipse.jgit.lib.ObjectId.fromString;
import static se.bjurr.gitchangelog.api.FakeGitRepo.DAY_ZERO;
import static se.bjurr.gitchangelog.api.FakeGitRepo.TIME_DAY;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

public class FakeRepo {
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
      new GitCommit("Tomas B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 1), "Adding stuff", hashes.get(0)))
    .withCommit(
      new GitCommit("T B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 2), "Adding stuff #20", hashes.get(1)))
    .withCommit(
      new GitCommit("Tomas B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 3), "Adding stuff \n  JIR-5262",
        hashes.get(2)))
    .withCommit(
      new GitCommit("T B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 4), "Adding stuff", hashes.get(3)))
    .withCommit(
      new GitCommit("Tomas B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 5), "Adding stuff #12", hashes.get(4)))
    .withCommit(
      new GitCommit("T B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 6), "This is 1.0 tagged commit", hashes
        .get(5)))
    .withCommit(
      new GitCommit("Tomas B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 7), "Adding stuff", hashes.get(6)))
    .withCommit(
      new GitCommit("T B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 8), "Adding stuff\n #30", hashes.get(7)))
    .withCommit(
      new GitCommit("Tomas B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 9), "Adding stuff JIR-1234", hashes
        .get(8)))
    .withCommit(
      new GitCommit("T B", "tomas.b@example", new Date(DAY_ZERO + TIME_DAY * 10), "More stuff #bug", hashes.get(9)));

  fakeGitRepo.withTag(new GitTag("refs/tags/1.0", fakeGitRepo.getDiff(fromString(ZERO_COMMIT),
    fromString(hashes.get(5)))));

  System.out.println("Created fake repo for testing:");
  for (GitCommit gitCommit : fakeGitRepo.getDiff(fromString(ZERO_COMMIT), fakeGitRepo.getRef("refs/heads/master"))) {
   printTag(gitCommit.getHash(), fakeGitRepo.getTags());
   System.out.println(" " + gitCommit.getHash() + " " + gitCommit.getMessage());
  }

  return fakeGitRepo;
 }

 private static void printTag(String hash, List<GitTag> tags) {
  for (GitTag gitTag : tags) {
   if (gitTag.getGitCommit().getHash().equals(hash)) {
    System.out.println("Tag: " + gitTag.getName());
   }
  }
 }
}
