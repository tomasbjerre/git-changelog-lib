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
  private final List<String> urlParts;

  public Changelog(
      final List<Commit> commits,
      final List<Tag> tags,
      final List<Author> authors,
      final List<Issue> issues,
      final List<IssueType> issueTypes,
      final String ownerName,
      final String repoName,
      final List<String> urlParts) {
    this.commits = checkNotNull(commits, "commits");
    this.tags = checkNotNull(tags, "tags");
    this.authors = checkNotNull(authors, "authors");
    this.issues = checkNotNull(issues, "issues");
    this.issueTypes = checkNotNull(issueTypes, "issueTypes");
    this.ownerName = ownerName;
    this.repoName = repoName;
    this.urlParts = urlParts;
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

  public List<String> getUrlParts() {
    return this.urlParts;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.authors == null) ? 0 : this.authors.hashCode());
    result = prime * result + ((this.commits == null) ? 0 : this.commits.hashCode());
    result = prime * result + ((this.issueTypes == null) ? 0 : this.issueTypes.hashCode());
    result = prime * result + ((this.issues == null) ? 0 : this.issues.hashCode());
    result = prime * result + ((this.ownerName == null) ? 0 : this.ownerName.hashCode());
    result = prime * result + ((this.repoName == null) ? 0 : this.repoName.hashCode());
    result = prime * result + ((this.tags == null) ? 0 : this.tags.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final Changelog other = (Changelog) obj;
    if (this.authors == null) {
      if (other.authors != null) {
        return false;
      }
    } else if (!this.authors.equals(other.authors)) {
      return false;
    }
    if (this.commits == null) {
      if (other.commits != null) {
        return false;
      }
    } else if (!this.commits.equals(other.commits)) {
      return false;
    }
    if (this.issueTypes == null) {
      if (other.issueTypes != null) {
        return false;
      }
    } else if (!this.issueTypes.equals(other.issueTypes)) {
      return false;
    }
    if (this.issues == null) {
      if (other.issues != null) {
        return false;
      }
    } else if (!this.issues.equals(other.issues)) {
      return false;
    }
    if (this.ownerName == null) {
      if (other.ownerName != null) {
        return false;
      }
    } else if (!this.ownerName.equals(other.ownerName)) {
      return false;
    }
    if (this.repoName == null) {
      if (other.repoName != null) {
        return false;
      }
    } else if (!this.repoName.equals(other.repoName)) {
      return false;
    }
    if (this.tags == null) {
      if (other.tags != null) {
        return false;
      }
    } else if (!this.tags.equals(other.tags)) {
      return false;
    }
    return true;
  }
}
