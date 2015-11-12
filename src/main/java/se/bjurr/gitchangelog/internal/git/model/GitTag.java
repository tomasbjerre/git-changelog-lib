package se.bjurr.gitchangelog.internal.git.model;

import java.util.List;

public class GitTag {

 private final String name;
 private final List<GitCommit> gitCommits;

 public GitTag(String name, List<GitCommit> gitCommits) {
  this.name = name;
  this.gitCommits = gitCommits;
 }

 public GitCommit getGitCommit() {
  return gitCommits.get(0);
 }

 public String getName() {
  return name;
 }

 public List<GitCommit> getGitCommits() {
  return gitCommits;
 }
}
