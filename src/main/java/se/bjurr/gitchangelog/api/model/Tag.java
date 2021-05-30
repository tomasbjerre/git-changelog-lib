package se.bjurr.gitchangelog.api.model;

import static se.bjurr.gitchangelog.internal.util.Preconditions.isNullOrEmpty;

import java.io.Serializable;
import java.util.List;
import se.bjurr.gitchangelog.api.model.interfaces.IAuthors;
import se.bjurr.gitchangelog.api.model.interfaces.ICommits;
import se.bjurr.gitchangelog.api.model.interfaces.IIssues;

public class Tag implements ICommits, IAuthors, IIssues, Serializable {
  private static final long serialVersionUID = 2140208294219785889L;
  private final String annotation;
  private final List<Author> authors;
  private final List<Commit> commits;
  private final List<Issue> issues;
  private final List<IssueType> issueTypes;
  private final String name;
  private final String tagTime;
  private final Long tagTimeLong;
  private final boolean hasTagTime;

  public Tag(
      final String name,
      final String annotation,
      final List<Commit> commits,
      final List<Author> authors,
      final List<Issue> issues,
      final List<IssueType> issueTypes,
      final String tagTime,
      final Long tagTimeLong) {
    this.commits = commits;
    this.authors = authors;
    this.issues = issues;
    this.name = name;
    this.annotation = annotation;
    this.issueTypes = issueTypes;
    this.tagTime = tagTime;
    this.tagTimeLong = tagTimeLong;
    this.hasTagTime = !isNullOrEmpty(tagTime);
  }

  public String getAnnotation() {
    return this.annotation;
  }

  @Override
  public List<Author> getAuthors() {
    return this.authors;
  }

  public Commit getCommit() {
    return this.commits.get(0);
  }

  @Override
  public List<Commit> getCommits() {
    return this.commits;
  }

  @Override
  public List<Issue> getIssues() {
    return this.issues;
  }

  public List<IssueType> getIssueTypes() {
    return this.issueTypes;
  }

  public String getName() {
    return this.name;
  }

  public String getTagTime() {
    return this.tagTime;
  }

  public Long getTagTimeLong() {
    return this.tagTimeLong;
  }

  public boolean isHasTagTime() {
    return this.hasTagTime;
  }

  @Override
  public String toString() {
    return "name: " + this.name;
  }
}
