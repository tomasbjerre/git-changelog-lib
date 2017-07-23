package se.bjurr.gitchangelog.api.model;

import static com.google.common.base.Preconditions.checkNotNull;

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
      List<Commit> commits,
      List<Tag> tags,
      List<Author> authors,
      List<Issue> issues,
      List<IssueType> issueTypes,
      String ownerName,
      String repoName) {
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
    return issues;
  }

  @Override
  public List<Author> getAuthors() {
    return authors;
  }

  @Override
  public List<Commit> getCommits() {
    return commits;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public String getRepoName() {
    return repoName;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public List<IssueType> getIssueTypes() {
    return issueTypes;
  }
}
