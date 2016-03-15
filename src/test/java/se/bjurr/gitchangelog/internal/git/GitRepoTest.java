package se.bjurr.gitchangelog.internal.git;

import static com.google.common.collect.Lists.reverse;
import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_MASTER;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.Before;
import org.junit.Test;

import se.bjurr.gitchangelog.internal.git.model.GitCommit;

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
 public void testThatRepoCanBeFound() throws Exception {
  GitRepo gitRepo = getGitRepo();
  assertThat(gitRepo).isNotNull();
 }

 @Test
 public void testThatTagsCanBeListed() throws Exception {
  GitRepo gitRepo = getGitRepo();
  GitRepoData gitRepoData = gitRepo
    .getGitRepoData(gitRepo.getCommit(ZERO_COMMIT), gitRepo.getRef(REF_MASTER), "No tag");
  assertThat(gitRepoData.getGitTags()).isNotEmpty();
 }

 @Test
 public void testThatZeroCommitCanBeRetrieved() throws Exception {
  GitRepo gitRepo = getGitRepo();
  ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
  assertThat(firstCommit).as(gitRepo.toString()).isNotNull();
  assertThat(firstCommit.name()).as(gitRepo.toString()).startsWith(FIRST_COMMIT_HASH);
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
 public void testThatCommitsBetweenCommitAndReferenceCanBeListed() throws Exception {
  GitRepo gitRepo = getGitRepo();
  ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
  ObjectId lastCommit = gitRepo.getRef(REF_MASTER);
  List<GitCommit> diff = gitRepo.getGitRepoData(firstCommit, lastCommit, "No tag").getGitCommits();
  assertThat(diff.size()).isGreaterThan(10);
  assertThat(reverse(diff).get(0).getHash()).startsWith(FIRST_COMMIT_HASH);
 }

 @Test
 public void testThatCommitsBetweenZeroCommitAndCommitCanBeListed() throws Exception {
  GitRepo gitRepo = getGitRepo();
  ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
  ObjectId lastCommit = gitRepo.getCommit(TAG_1_0_HASH);
  GitRepoData gitRepoData = gitRepo.getGitRepoData(firstCommit, lastCommit, "No tag");
  assertThat(gitRepoData.getGitCommits()).as("Commits in first release.").hasSize(6);
  assertThat(gitRepoData.getGitTags()).as("Tags in first release.").hasSize(1);
  assertThat(reverse(gitRepoData.getGitCommits()).get(0).getHash()).startsWith(FIRST_COMMIT_HASH);
 }

 @Test
 public void testThatCommitsSecondReleaseCommitCanBeListed() throws Exception {
  GitRepo gitRepo = getGitRepo();
  ObjectId firstRelease = gitRepo.getRef("refs/tags/1.0");
  ObjectId secondRelease = gitRepo.getRef("refs/tags/1.1");
  List<GitCommit> diff = gitRepo.getGitRepoData(firstRelease, secondRelease, "No tag").getGitCommits();
  assertThat(diff).as("Commits in second release.").hasSize(8);
  assertThat(reverse(diff).get(0).getHash()).startsWith("3950");
 }

 @Test
 public void testThatCommitsBetweenCommitAndCommitCanBeListed() throws Exception {
  GitRepo gitRepo = getGitRepo();
  ObjectId firstCommit = gitRepo.getCommit(FIRST_COMMIT_HASH_FULL);
  ObjectId lastCommit = gitRepo.getCommit("e3766e2d4bc6d206475c5d2ed96b3f967a6e157e");
  List<GitCommit> diff = gitRepo.getGitRepoData(firstCommit, lastCommit, "No tag").getGitCommits();
  assertThat(diff).isNotEmpty();
  assertThat(reverse(diff).get(0).getHash())//
    .as("first")//
    .startsWith(FIRST_COMMIT_HASH);
  assertThat(diff.get(0).getHash())//
    .as("last")//
    .startsWith("e3766e2d4bc6d20");
 }

 private GitRepo getGitRepo() throws Exception {
  return new GitRepo(gitRepoFile);
 }
}
