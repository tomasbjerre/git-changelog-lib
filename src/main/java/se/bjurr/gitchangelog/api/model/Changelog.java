package se.bjurr.gitchangelog.api.model;

import static se.bjurr.gitchangelog.internal.util.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;
import se.bjurr.gitchangelog.api.model.interfaces.IAuthors;
import se.bjurr.gitchangelog.api.model.interfaces.ICommits;
import se.bjurr.gitchangelog.api.model.interfaces.IIssues;

public class Changelog implements ICommits, IAuthors, IIssues, Serializable {
  private static final long serialVersionUID = 2193789018496738737L;
  private final List<Commit> commits;
  private final List<Tag> tags;
  private final List<Author> authors;
  private final List<Issue> issues;
  private final List<IssueType> issueTypes;
  private final String ownerName;
  private final String repoName;

  public Changelog(
      final List<Commit> commits,
      final List<Tag> tags,
      final List<Author> authors,
      final List<Issue> issues,
      final List<IssueType> issueTypes,
      final String ownerName,
      final String repoName) {
    this.commits = checkNotNull(commits, "commits");
    this.tags = checkNotNull(tags, "tags");
    this.authors = checkNotNull(authors, "authors");
    this.issues = checkNotNull(issues, "issues");
    this.issueTypes = checkNotNull(issueTypes, "issueTypes");
    this.ownerName = ownerName;
    this.repoName = repoName;
  }

  @Override
  public List<Issue> getIssues() {
    return this.issues;
  }

  @Override
  public List<Author> getAuthors() {
    return this.authors;
  }

  @Override
  public List<Commit> getCommits() {
    return this.commits;
  }

  public String getOwnerName() {
    return this.ownerName;
  }

  public String getRepoName() {
    return this.repoName;
  }

  public List<Tag> getTags() {
    return this.tags;
  }

  public List<IssueType> getIssueTypes() {
    return this.issueTypes;
  }
}
