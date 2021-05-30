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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.annotation == null) ? 0 : this.annotation.hashCode());
    result = prime * result + ((this.authors == null) ? 0 : this.authors.hashCode());
    result = prime * result + ((this.commits == null) ? 0 : this.commits.hashCode());
    result = prime * result + (this.hasTagTime ? 1231 : 1237);
    result = prime * result + ((this.issueTypes == null) ? 0 : this.issueTypes.hashCode());
    result = prime * result + ((this.issues == null) ? 0 : this.issues.hashCode());
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.tagTime == null) ? 0 : this.tagTime.hashCode());
    result = prime * result + ((this.tagTimeLong == null) ? 0 : this.tagTimeLong.hashCode());
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
    final Tag other = (Tag) obj;
    if (this.annotation == null) {
      if (other.annotation != null) {
        return false;
      }
    } else if (!this.annotation.equals(other.annotation)) {
      return false;
    }
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
    if (this.hasTagTime != other.hasTagTime) {
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
    if (this.name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!this.name.equals(other.name)) {
      return false;
    }
    if (this.tagTime == null) {
      if (other.tagTime != null) {
        return false;
      }
    } else if (!this.tagTime.equals(other.tagTime)) {
      return false;
    }
    if (this.tagTimeLong == null) {
      if (other.tagTimeLong != null) {
        return false;
      }
    } else if (!this.tagTimeLong.equals(other.tagTimeLong)) {
      return false;
    }
    return true;
  }
}
