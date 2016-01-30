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
 private static final String TAG_1_0_HASH = "01484ce71bbc76e1af75ebb07a52844145ce99dc";
 private File gitRepoFile;

 @Before
 public void before() {
  this.gitRepoFile = new File(Resources.getResource("github-issues.json").getFile());
 }

 @Test
 public void testThatRepoCanBeFound() {
  GitRepo gitRepo = getGitRepo();
  assertThat(gitRepo).isNotNull();
 }

 @Test
 public void testThatTagsCanBeListed() {
  GitRepo gitRepo = getGitRepo();
  assertThat(gitRepo.getTags()).isNotEmpty();
 }

 @Test
 public void testThatCommitsCanBeRetrieved() {
  GitRepo gitRepo = getGitRepo();
  ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
  assertThat(firstCommit).isNotNull();
  assertThat(firstCommit.name()).startsWith(FIRST_COMMIT_HASH);
 }

 @Test
 public void testThatCommitsBetweenCommitAndReferenceCanBeListed() {
  GitRepo gitRepo = getGitRepo();
  ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
  ObjectId lastCommit = gitRepo.getRef(REF_MASTER);
  List<GitCommit> diff = gitRepo.getDiff(firstCommit, lastCommit);
  assertThat(diff).isNotEmpty();
  assertThat(reverse(diff).get(0).getHash()).startsWith(FIRST_COMMIT_HASH);
 }

 @Test
 public void testThatCommitsBetweenCommitAndCommitCanBeListed() {
  GitRepo gitRepo = getGitRepo(); 
  ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
  ObjectId lastCommit = gitRepo.getCommit(TAG_1_0_HASH);
  List<GitCommit> diff = gitRepo.getDiff(firstCommit, lastCommit);
  assertThat(diff).isNotEmpty();
  assertThat(reverse(diff).get(0).getHash()).startsWith(FIRST_COMMIT_HASH);
 }

 private GitRepo getGitRepo() {
  return new GitRepo(gitRepoFile);
 }
}
