package se.bjurr.gitchangelog.internal.git;

import java.util.List;

import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

public class GitRepoData {
 private final List<GitCommit> gitCommits;
 private final List<GitTag> gitTags;

 public GitRepoData(List<GitCommit> gitCommits, List<GitTag> gitTags) {
  this.gitCommits = gitCommits;
  this.gitTags = gitTags;
 }

 public List<GitCommit> getGitCommits() {
  return gitCommits;
 }

 public List<GitTag> getGitTags() {
  return gitTags;
 }
}
