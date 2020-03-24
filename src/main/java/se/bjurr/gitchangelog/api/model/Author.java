package se.bjurr.gitchangelog.api.model;

import java.io.Serializable;
import java.util.List;
import se.bjurr.gitchangelog.api.model.interfaces.ICommits;

public class Author implements ICommits, Serializable {
  private static final long serialVersionUID = -672028657732998143L;
  private final List<Commit> commits;
  private final String authorName;
  private final String authorEmail;

  public Author(String authorName, String authorEmail, List<Commit> commits) {
    this.authorName = authorName;
    this.authorEmail = authorEmail;
    this.commits = commits;
  }

  public String getAuthorEmail() {
    return authorEmail;
  }

  public String getAuthorName() {
    return authorName;
  }

  @Override
  public List<Commit> getCommits() {
    return commits;
  }

  @Override
  public String toString() {
    return "Author: " + authorName;
  }
}
