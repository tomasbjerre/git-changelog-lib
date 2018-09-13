package se.bjurr.gitchangelog.internal.model;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Lists.newArrayList;

import com.google.common.base.Optional;
import java.util.List;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.model.interfaces.IGitCommitReferer;

public class ParsedIssue implements IGitCommitReferer {
  private final List<GitCommit> gitCommits = newArrayList();
  private final String name;
  private final String title;
  private final String link;
  private final String issue;
  private final String issueType;
  private final List<String> labels;
  private final String description;

  public ParsedIssue(
      String name,
      String issue,
      String description,
      String link,
      String title,
      String issueType,
      List<String> labels) {
    this.name = checkNotNull(name, "name");
    this.title = emptyToNull(title);
    this.issue = issue;
    this.link = link;
    this.issueType = issueType;
    this.labels = labels;
    this.description = description;
  }

  public Optional<String> getTitle() {
    return fromNullable(title);
  }

  @Override
  public GitCommit getGitCommit() {
    return checkNotNull(gitCommits.get(0), name);
  }

  public List<GitCommit> getGitCommits() {
    return gitCommits;
  }

  public String getLink() {
    return link;
  }

  public String getIssueType() {
    return issueType;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  public String toString() {
    return name + issue;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj.getClass() != ParsedIssue.class) {
      return false;
    }
    return name.equals(((ParsedIssue) obj).getName());
  }

  public void addCommit(GitCommit gitCommit) {
    this.gitCommits.add(gitCommit);
  }

  public String getIssue() {
    return issue;
  }

  public String getDescription() {
    return description;
  }

  public void addCommits(List<GitCommit> commits) {
    this.gitCommits.addAll(commits);
  }

  public List<String> getLabels() {
    return labels;
  }
}
