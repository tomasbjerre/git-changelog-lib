package se.bjurr.gitchangelog.api.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;

import java.io.Serializable;
import java.util.List;
import se.bjurr.gitchangelog.api.model.interfaces.IAuthors;
import se.bjurr.gitchangelog.api.model.interfaces.ICommits;

public class Issue implements ICommits, IAuthors, Serializable {
  private static final long serialVersionUID = -7571341639024417199L;
  private final List<Commit> commits;
  private final List<Author> authors;
  /** Like JIRA, or GitHub. */
  private final String name;

  /** Like the title of a Jira. */
  private final String title;

  private final boolean hasTitle;

  /** Like the actual Jira, JIR-ABC. */
  private final String issue;

  private final boolean hasIssue;

  /** A link to the issue, http://..... */
  private final String link;

  private final boolean hasLink;

  /** Type of issue, perhaps Story, Bug and etc */
  private final String type;

  private final boolean hasType;

  /** Labels on the issue, for GitHub it may be bug, enhancement, ... */
  private final List<String> labels;

  private final boolean hasLabels;

  public Issue(
      List<Commit> commits,
      List<Author> authors,
      String name,
      String title,
      String issue,
      String link,
      String type,
      List<String> labels) {
    checkState(!commits.isEmpty(), "commits");
    this.commits = commits;
    this.authors = checkNotNull(authors, "authors");
    this.name = checkNotNull(name, "name");
    this.title = nullToEmpty(title);
    this.hasTitle = !isNullOrEmpty(title);
    this.issue = nullToEmpty(issue);
    this.hasIssue = !isNullOrEmpty(issue);
    this.link = nullToEmpty(link);
    this.hasLink = !isNullOrEmpty(link);
    this.hasType = !isNullOrEmpty(type);
    this.type = nullToEmpty(type);
    this.hasLabels = labels != null && !labels.isEmpty();
    this.labels = labels;
  }

  public String getTitle() {
    return title;
  }

  public boolean hasTitle() {
    return hasTitle;
  }

  public boolean hasIssue() {
    return hasIssue;
  }

  public boolean hasLabels() {
    return hasLabels;
  }

  public boolean hasType() {
    return hasType;
  }

  public boolean hasLink() {
    return hasLink;
  }

  public String getIssue() {
    return issue;
  }

  public String getLink() {
    return link;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  @Override
  public List<Author> getAuthors() {
    return authors;
  }

  @Override
  public List<Commit> getCommits() {
    return commits;
  }

  public List<String> getLabels() {
    return labels;
  }

  @Override
  public String toString() {
    return "Issue: " + issue + " Title: " + title;
  }
}
