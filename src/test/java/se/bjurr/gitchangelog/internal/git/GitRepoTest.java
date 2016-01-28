package se.bjurr.gitchangelog.internal.git;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_MASTER;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.io.File;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Resources;

public class GitRepoTest {
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
 }

 @Test
 public void testThatCommitsBetween2ReferencesCanBeListed() {
  GitRepo gitRepo = getGitRepo();
  ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
  ObjectId lastCommit = gitRepo.getRef(REF_MASTER);
  assertThat(gitRepo.getDiff(firstCommit, lastCommit)).isNotEmpty();
 }

 private GitRepo getGitRepo() {
  return new GitRepo(gitRepoFile);
 }
}
