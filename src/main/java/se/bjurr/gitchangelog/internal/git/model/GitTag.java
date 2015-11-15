package se.bjurr.gitchangelog.internal.git.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import se.bjurr.gitchangelog.internal.model.interfaces.IGitCommitReferer;

public class GitTag implements IGitCommitReferer {

 private final String name;
 private final List<GitCommit> gitCommits;

 public GitTag(String name, List<GitCommit> gitCommits) {
  checkArgument(!gitCommits.isEmpty(), "commits");
  this.name = name;
  this.gitCommits = gitCommits;
 }

 @Override
 public GitCommit getGitCommit() {
  return checkNotNull(gitCommits.get(0), name);
 }

 @Override
 public String getName() {
  return name;
 }

 public List<GitCommit> getGitCommits() {
  return gitCommits;
 }
}
