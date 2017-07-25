package se.bjurr.gitchangelog.internal.git;

import static com.google.common.base.Optional.absent;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newTreeSet;

import java.util.List;
import java.util.Set;

import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

import com.google.common.base.Optional;

public class GitRepoData {
  private final List<GitCommit> gitCommits;
  private final List<GitTag> gitTags;
  private final String originCloneUrl;

  public GitRepoData(String originCloneUrl, List<GitTag> gitTags) {
    Set<GitCommit> gitCommitsSorted = newTreeSet();
    for (GitTag gitTag : gitTags) {
      gitCommitsSorted.addAll(gitTag.getGitCommits());
    }
    this.originCloneUrl = originCloneUrl;
    this.gitCommits = newArrayList(gitCommitsSorted);
    this.gitTags = gitTags;
  }

  public String getOriginCloneUrl() {
    return originCloneUrl;
  }

  public List<GitCommit> getGitCommits() {
    return gitCommits;
  }

  public List<GitTag> getGitTags() {
    return gitTags;
  }

  public Optional<String> findGitHubApi() {
    if (originCloneUrl == null) {
      return absent();
    }
    if (!originCloneUrl.contains("github.com")) {
      return absent();
    }
    return Optional.of(
        "https://api.github.com/repos/" + findOwnerName().orNull() + "/" + findRepoName().orNull());
  }

  public Optional<String> findGitLabServer() {
    if (originCloneUrl == null) {
      return absent();
    }
    if (!originCloneUrl.contains("gitlab.com")) {
      return absent();
    }
    return Optional.of("https://gitlab.com/");
  }

  public Optional<String> findOwnerName() {
    return repoUrlPartFromEnd(1);
  }

  public Optional<String> findRepoName() {
    return repoUrlPartFromEnd(0);
  }

  private Optional<String> repoUrlPartFromEnd(int i) {
    if (originCloneUrl == null) {
      return absent();
    }
    String[] parts = this.originCloneUrl//
    		.replaceAll("\\.git$", "")//
    		.split("[/:\\.]");
    if (parts.length > i) {
      return Optional.of(parts[parts.length - i - 1]);
    }
    return absent();
  }
}
