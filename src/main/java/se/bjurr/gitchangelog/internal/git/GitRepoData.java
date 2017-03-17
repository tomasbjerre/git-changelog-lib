package se.bjurr.gitchangelog.internal.git;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newTreeSet;

import java.util.List;
import java.util.Set;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

public class GitRepoData {
  private final List<GitCommit> gitCommits;
  private final List<GitTag> gitTags;

  public GitRepoData(List<GitTag> gitTags) {
    Set<GitCommit> gitCommitsSorted = newTreeSet();
    for (GitTag gitTag : gitTags) {
      gitCommitsSorted.addAll(gitTag.getGitCommits());
    }
    this.gitCommits = newArrayList(gitCommitsSorted);
    this.gitTags = gitTags;
  }

  public List<GitCommit> getGitCommits() {
    return gitCommits;
  }

  public List<GitTag> getGitTags() {
    return gitTags;
  }
}
