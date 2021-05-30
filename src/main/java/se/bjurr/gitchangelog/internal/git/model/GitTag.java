package se.bjurr.gitchangelog.internal.git.model;

import static se.bjurr.gitchangelog.internal.util.Preconditions.checkArgument;
import static se.bjurr.gitchangelog.internal.util.Preconditions.checkNotNull;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import se.bjurr.gitchangelog.internal.model.interfaces.IGitCommitReferer;

public class GitTag implements IGitCommitReferer {

  private final String annotation;
  private final List<GitCommit> gitCommits;
  private final String name;
  private final Date tagTime;

  public GitTag(
      final String name,
      final String annotation,
      final List<GitCommit> gitCommits,
      final Date tagTime) {
    checkArgument(!gitCommits.isEmpty(), "No commits in " + name);
    this.name = checkNotNull(name, "name");
    this.annotation = annotation;
    this.gitCommits = gitCommits;
    this.tagTime = tagTime;
  }

  public Optional<String> findAnnotation() {
    return Optional.ofNullable(this.annotation);
  }

  @Override
  public GitCommit getGitCommit() {
    return checkNotNull(this.gitCommits.get(0), this.name);
  }

  public List<GitCommit> getGitCommits() {
    return this.gitCommits;
  }

  @Override
  public String getName() {
    return this.name;
  }

  public Date getTagTime() {
    return this.tagTime;
  }

  @Override
  public String toString() {
    return "Tag: " + this.name + " Annotation: " + this.annotation + ", " + this.getGitCommit();
  }
}
