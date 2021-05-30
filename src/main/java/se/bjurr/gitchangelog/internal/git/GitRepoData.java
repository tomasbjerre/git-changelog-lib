package se.bjurr.gitchangelog.internal.git;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newTreeSet;
import static java.util.Optional.empty;

import com.google.common.base.Splitter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

public class GitRepoData {
  private final List<GitCommit> gitCommits;
  private final List<GitTag> gitTags;
  private final String originCloneUrl;

  public GitRepoData(final String originCloneUrl, final List<GitTag> gitTags) {
    final Set<GitCommit> gitCommitsSorted = newTreeSet();
    for (final GitTag gitTag : gitTags) {
      gitCommitsSorted.addAll(gitTag.getGitCommits());
    }
    this.originCloneUrl = originCloneUrl;
    this.gitCommits = newArrayList(gitCommitsSorted);
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
    if (this.originCloneUrl == null) {
      return empty();
    }
    final String sequence = this.originCloneUrl.replaceAll("\\.git$", "");
    final Pattern pattern = Pattern.compile("[/:]");
    final Iterable<String> parts = Splitter.on(pattern).split(sequence);
    final List<String> partsList = newArrayList(parts);
    if (partsList.size() > i) {
      return Optional.of(partsList.get(partsList.size() - i - 1));
    }
    return empty();
  }
}
