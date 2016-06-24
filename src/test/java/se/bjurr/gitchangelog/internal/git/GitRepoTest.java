package se.bjurr.gitchangelog.internal.git;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.reverse;
import static com.google.common.collect.Maps.newTreeMap;
import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_MASTER;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.Before;
import org.junit.Test;

import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

import com.google.common.base.Optional;
import com.google.common.io.Resources;

public class GitRepoTest {
 private static final String FIRST_COMMIT_HASH = "a1aa5ff";
 private static final String FIRST_COMMIT_HASH_FULL = "a1aa5ff5b625e63aa5ad7b59367ec7f75658afb8";
 private static final String TAG_1_0_HASH = "01484ce71bbc76e1af75ebb07a52844145ce99dc";
 private File gitRepoFile;

 @Before
 public void before() {
  this.gitRepoFile = new File(Resources.getResource("github-issues.json").getFile());
 }

 @Test
 public void testThatCommitsBetweenCommitAndCommitCanBeListed() throws Exception {
  GitRepo gitRepo = getGitRepo();
  ObjectId firstCommit = gitRepo.getCommit(FIRST_COMMIT_HASH_FULL);
  ObjectId lastCommit = gitRepo.getCommit("e3766e2d4bc6d206475c5d2ed96b3f967a6e157e");
  List<GitCommit> diff = gitRepo.getGitRepoData(firstCommit, lastCommit, "No tag",
    Optional.of(".*tag-in-test-feature$")).getGitCommits();
  assertThat(diff).isNotEmpty();
  assertThat(reverse(diff).get(0).getHash())//
    .as("first")//
    .startsWith("5aaeb90");
  assertThat(diff.get(0).getHash())//
    .as("last")//
    .startsWith("e3766e2d4bc6d20");
 }

 @Test
 public void testThatCommitsBetweenCommitAndReferenceCanBeListed() throws Exception {
  GitRepo gitRepo = getGitRepo();
  ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
  ObjectId lastCommit = gitRepo.getRef(REF_MASTER);
  List<GitCommit> diff = gitRepo.getGitRepoData(firstCommit, lastCommit, "No tag",
    Optional.of(".*tag-in-test-feature$")).getGitCommits();
  assertThat(diff.size()).isGreaterThan(10);
  assertThat(reverse(diff).get(0).getHash()).startsWith("5aae");
 }

 @Test
 public void testThatCommitsBetweenZeroCommitAndCommitCanBeListed() throws Exception {
  GitRepo gitRepo = getGitRepo();
  ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
  ObjectId lastCommit = gitRepo.getCommit(TAG_1_0_HASH);
  GitRepoData gitRepoData = gitRepo.getGitRepoData(firstCommit, lastCommit, "No tag",
    Optional.of(".*tag-in-test-feature$"));
  assertThat(gitRepoData.getGitCommits()).as("Commits in first release.")//
    .hasSize(5);
  assertThat(gitRepoData.getGitTags()).as("Tags in first release.")//
    .hasSize(1);
  assertThat(reverse(gitRepoData.getGitCommits()).get(0).getHash())//
    .startsWith("5aae");
 }

 @Test
 public void testThatCommitsCanBeRetrieved() throws Exception {
  GitRepo gitRepo = getGitRepo();
  assertThat(gitRepo.getCommit(FIRST_COMMIT_HASH_FULL)).isNotNull();
  assertThat(gitRepo.getCommit(FIRST_COMMIT_HASH_FULL).name()).isEqualTo(FIRST_COMMIT_HASH_FULL);
  assertThat(gitRepo.getCommit(TAG_1_0_HASH)).isNotNull();
  assertThat(gitRepo.getCommit(TAG_1_0_HASH).name()).isEqualTo(TAG_1_0_HASH);
 }

 @Test
 public void testThatCommitsSecondReleaseCommitCanBeListed() throws Exception {
  GitRepo gitRepo = getGitRepo();
  ObjectId firstRelease = gitRepo.getRef("refs/tags/1.0");
  ObjectId secondRelease = gitRepo.getRef("refs/tags/1.1");
  List<GitCommit> diff = gitRepo.getGitRepoData(firstRelease, secondRelease, "No tag",
    Optional.of(".*tag-in-test-feature$")).getGitCommits();
  assertThat(diff)//
    .as("Commits in second release from 1.0.")//
    .hasSize(8);
  assertThat(diff.get(7).getHash())//
    .startsWith("3950c64");
  assertThat(diff.get(0).getHash())//
    .startsWith(secondRelease.getName().substring(0, 10));

  ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
  diff = gitRepo.getGitRepoData(firstCommit, secondRelease, "No tag", Optional.of(".*tag-in-test-feature$"))
    .getGitCommits();
  assertThat(diff)//
    .as("Commits in second release from zero commit.")//
    .hasSize(13);
  assertThat(diff.get(6).getHash())//
    .startsWith("ba9d");
  assertThat(diff.get(0).getHash())//
    .startsWith(secondRelease.getName().substring(0, 10));
 }

 @Test
 public void testThatRepoCanBeFound() throws Exception {
  GitRepo gitRepo = getGitRepo();
  assertThat(gitRepo).isNotNull();
 }

 @Test
 public void testThatShortHashCanBeUsedToFindCommits() throws Exception {
  GitRepo gitRepo = getGitRepo();
  assertThat(gitRepo.getCommit("5a50ad3672c9f5a273c04711ed9b3daebf1f9b07").getName())//
    .isEqualTo("5a50ad3672c9f5a273c04711ed9b3daebf1f9b07");
  assertThat(gitRepo.getCommit("5a50ad3").getName())//
    .isEqualTo("5a50ad3672c9f5a273c04711ed9b3daebf1f9b07");
 }

 @Test
 public void testThatTagCanBeIgnored() throws Exception {
  GitRepo gitRepo = getGitRepo();
  ObjectId from = gitRepo.getCommit("87c0d72888961712d4d63dd6298c24c1133a6b51");
  ObjectId to = gitRepo.getRef("test");

  GitRepoData gitRepoData = gitRepo.getGitRepoData(from, to, "No tag", Optional.of(".*tag-in-test-feature$"));
  Map<String, GitTag> perTag = perTag(gitRepoData.getGitTags());
  assertThat(gitRepoData.getGitTags())//
    .hasSize(1);
  assertThat(gitRepoData.getGitTags())//
    .hasSize(1);
  assertThat(perTag.keySet())//
    .hasSize(1)//
    .contains("No tag");
  GitTag noTagTag = perTag.get("No tag");

  List<String> noTagTagMessages = messages(noTagTag.getGitCommits());
  assertThat(noTagTagMessages)//
    .containsExactly(//
      "Some stuff in test again",//
      "Merge branch 'test-feature' into test",//
      "Some stuff in test-feature",//
      "some stuff in test branch");
 }

 @Test
 public void testThatTagInFeatureBranchAndMainBranchIsNotMixed() throws Exception {
  GitRepo gitRepo = getGitRepo();
  ObjectId from = gitRepo.getCommit("87c0d72888961712d4d63dd6298c24c1133a6b51");
  ObjectId to = gitRepo.getRef("test");

  GitRepoData gitRepoData = gitRepo.getGitRepoData(from, to, "No tag", Optional.<String> absent());
  Map<String, GitTag> perTag = perTag(gitRepoData.getGitTags());
  assertThat(perTag.keySet())//
    .hasSize(2)//
    .containsExactly(//
      "No tag",//
      "refs/tags/tag-in-test-feature");
  GitTag noTagTag = perTag.get("No tag");
  List<String> noTagNames = messages(noTagTag.getGitCommits());
  assertThat(noTagNames)//
    .containsExactly(//
      "Some stuff in test again",//
      "Merge branch 'test-feature' into test",//
      "some stuff in test branch");

  GitTag testFeatureTag = perTag.get("refs/tags/tag-in-test-feature");
  List<String> testFeatureTagMessages = messages(testFeatureTag.getGitCommits());
  assertThat(testFeatureTagMessages)//
    .containsExactly(//
      "Some stuff in test-feature");
 }

 @Test
 public void testThatTagInFeatureBranchDoesNotIncludeNewUnmergedCommitsInItsMainBranchWhenFeatureLaterMerged()
   throws Exception {
  GitRepo gitRepo = getGitRepo();
  ObjectId from = gitRepo.getCommit("090e7f4");
  ObjectId to = gitRepo.getCommit("8371342");

  GitRepoData gitRepoData = gitRepo.getGitRepoData(from, to, "No tag", Optional.<String> absent());
  Map<String, GitTag> perTag = perTag(gitRepoData.getGitTags());
  assertThat(perTag.keySet())//
    .hasSize(1)//
    .containsExactly(//
      "No tag");
  GitTag noTagTag = perTag.get("No tag");
  List<String> noTagNames = messages(noTagTag.getGitCommits());
  assertThat(noTagNames)//
    .containsExactly(//
      "Some stuff in test again",//
      "Merge branch 'test-feature' into test",//
      "some stuff in test branch");
 }

 @Test
 public void testThatTagsCanBeListed() throws Exception {
  GitRepo gitRepo = getGitRepo();
  GitRepoData gitRepoData = gitRepo.getGitRepoData(gitRepo.getCommit(ZERO_COMMIT), gitRepo.getRef(REF_MASTER),
    "No tag", Optional.of(".*tag-in-test-feature$"));
  assertThat(gitRepoData.getGitTags()).isNotEmpty();
 }

 @Test
 public void testThatZeroCommitCanBeRetrieved() throws Exception {
  GitRepo gitRepo = getGitRepo();
  ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
  assertThat(firstCommit).as(gitRepo.toString()).isNotNull();
  assertThat(firstCommit.name()).as(gitRepo.toString()).startsWith(FIRST_COMMIT_HASH);
 }

 private GitRepo getGitRepo() throws Exception {
  return new GitRepo(this.gitRepoFile);
 }

 private List<String> messages(List<GitCommit> gitCommits) {
  List<String> messages = newArrayList();
  for (GitCommit gc : gitCommits) {
   messages.add(gc.getMessage().trim());
  }
  return messages;
 }

 private Map<String, GitTag> perTag(List<GitTag> gitTags) {
  Map<String, GitTag> map = newTreeMap();
  for (GitTag gitTag : gitTags) {
   map.put(gitTag.getName(), gitTag);
  }
  return map;
 }
}
