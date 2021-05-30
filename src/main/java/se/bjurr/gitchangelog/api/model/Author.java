package se.bjurr.gitchangelog.api.model;

import java.io.Serializable;
import java.util.List;
import se.bjurr.gitchangelog.api.model.interfaces.ICommits;

public class Author implements ICommits, Serializable {
  private static final long serialVersionUID = -672028657732998142L;
  private final List<Commit> commits;
  private final String authorName;
  private final String authorEmail;

  public Author(final String authorName, final String authorEmail, final List<Commit> commits) {
    this.authorName = authorName;
    this.authorEmail = authorEmail;
    this.commits = commits;
  }

  public String getAuthorEmail() {
    return this.authorEmail;
  }

  public String getAuthorName() {
    return this.authorName;
  }

  @Override
  public List<Commit> getCommits() {
    return this.commits;
  }

  @Override
  public String toString() {
    return "Author: " + this.authorName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.authorEmail == null) ? 0 : this.authorEmail.hashCode());
    result = prime * result + ((this.authorName == null) ? 0 : this.authorName.hashCode());
    result = prime * result + ((this.commits == null) ? 0 : this.commits.hashCode());
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
    final Author other = (Author) obj;
    if (this.authorEmail == null) {
      if (other.authorEmail != null) {
        return false;
      }
    } else if (!this.authorEmail.equals(other.authorEmail)) {
      return false;
    }
    if (this.authorName == null) {
      if (other.authorName != null) {
        return false;
      }
    } else if (!this.authorName.equals(other.authorName)) {
      return false;
    }
    if (this.commits == null) {
      if (other.commits != null) {
        return false;
      }
    } else if (!this.commits.equals(other.commits)) {
      return false;
    }
    return true;
  }
}
