package se.bjurr.gitchangelog.api;

import static com.google.common.io.Resources.getResource;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class GitChangelogTest {
 private String repoRoot;

 @Before
 public void before() throws Exception {
  repoRoot = getRepoRoot(new File(getResource("github-issues.json").toURI()));
 }

 @Test
 public void createFullChangelog() throws Exception {
  gitChangelogApiBuilder()//
    .withSettings(new File(repoRoot + "/changelog.json").toURI().toURL())//
    .toFile(repoRoot + "/CHANGELOG.md");
 }

 private String getRepoRoot(File file) {
  if (file.getParentFile().getPath().endsWith("git-changelog-lib")) {
   return file.getParentFile().getPath();
  }
  return getRepoRoot(file.getParentFile());
 }
}
