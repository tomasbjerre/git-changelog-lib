package se.bjurr.gitchangelog.internal.git.model;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;
import java.util.Date;
import java.util.List;
import se.bjurr.gitchangelog.internal.model.interfaces.IGitCommitReferer;

public class GitTag implements IGitCommitReferer {

  private final String annotation;
  private final List<GitCommit> gitCommits;
  private final String name;
  private final Date tagTime;

  public GitTag(String name, String annotation, List<GitCommit> gitCommits, Date tagTime) {
    checkArgument(!gitCommits.isEmpty(), "No commits in " + name);
    this.name = checkNotNull(name, "name");
    this.annotation = annotation;
    this.gitCommits = gitCommits;
    this.tagTime = tagTime;
  }

  public Optional<String> findAnnotation() {
    return fromNullable(this.annotation);
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
    return "Tag: " + this.name + " Annotation: " + this.annotation + ", " + getGitCommit();
  }
}
