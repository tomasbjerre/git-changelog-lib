package se.bjurr.gitchangelog.internal.git;

import static java.util.Optional.empty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

public class GitRepoData {
  private final List<GitCommit> gitCommits;
  private final List<GitTag> gitTags;
  private final String originCloneUrl;

  public GitRepoData(final String originCloneUrl, final List<GitTag> gitTags) {
    final Set<GitCommit> gitCommitsSorted = new TreeSet<>();
    for (final GitTag gitTag : gitTags) {
      gitCommitsSorted.addAll(gitTag.getGitCommits());
    }
    this.originCloneUrl = originCloneUrl;
    this.gitCommits = new ArrayList<>(gitCommitsSorted);
    this.gitTags = gitTags;
  }

  public String getOriginCloneUrl() {
    return this.originCloneUrl;
  }

  public List<GitCommit> getGitCommits() {
    return this.gitCommits;
  }

  public List<GitTag> getGitTags() {
    return this.gitTags;
  }

  public Optional<String> findGitHubApi() {
    if (this.originCloneUrl == null) {
      return empty();
    }
    if (!this.originCloneUrl.contains("github.com")) {
      return empty();
    }
    return Optional.of(
        "https://api.github.com/repos/"
            + this.findOwnerName().orElse(null)
            + "/"
            + this.findRepoName().orElse(null));
  }

  public Optional<String> findGitLabServer() {
    if (this.originCloneUrl == null) {
      return empty();
    }
    if (!this.originCloneUrl.contains("gitlab.com")) {
      return empty();
    }
    return Optional.of("https://gitlab.com/");
  }

  public Optional<String> findOwnerName() {
    return this.repoUrlPartFromEnd(1);
  }

  public Optional<String> findRepoName() {
    return this.repoUrlPartFromEnd(0);
  }

  private Optional<String> repoUrlPartFromEnd(final int i) {
    final List<String> partsList = this.getUrlPartsList();
    if (partsList.size() > i) {
      return Optional.of(partsList.get(i));
    }
    return empty();
  }

  public List<String> getUrlPartsList() {
    if (this.originCloneUrl == null) {
      return new ArrayList<>();
    }
    final String sequence = this.originCloneUrl.replaceAll("\\.git$", "");
    final String pattern = "[/:]";
    final String[] parts = sequence.split(pattern);
    final List<String> list = Arrays.asList(parts);
    Collections.reverse(list);
    return list;
  }
}
