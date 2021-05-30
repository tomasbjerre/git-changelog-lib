package se.bjurr.gitchangelog.api.model;

import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.CUSTOM;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.GITHUB;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.GITLAB;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.JIRA;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.NOISSUE;
import static se.bjurr.gitchangelog.internal.util.Preconditions.checkNotNull;
import static se.bjurr.gitchangelog.internal.util.Preconditions.checkState;
import static se.bjurr.gitchangelog.internal.util.Preconditions.isNullOrEmpty;
import static se.bjurr.gitchangelog.internal.util.Preconditions.nullToEmpty;

import java.io.Serializable;
import java.util.List;
import se.bjurr.gitchangelog.api.model.interfaces.IAuthors;
import se.bjurr.gitchangelog.api.model.interfaces.ICommits;
import se.bjurr.gitchangelog.internal.settings.SettingsIssueType;

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

  private final boolean hasDescription;
  private final String description;
  /** Labels on the issue, for GitHub it may be bug, enhancement, ... */
  private final List<String> labels;

  private final boolean hasLabels;
  private final boolean hasLinkedIssues;
  private final SettingsIssueType issueType;
  private final List<String> linkedIssues;

  public Issue(
      final List<Commit> commits,
      final List<Author> authors,
      final String name,
      final String title,
      final String issue,
      final SettingsIssueType issueType,
      final String description,
      final String link,
      final String type,
      final List<String> linkedIssues,
      final List<String> labels) {
    checkState(!commits.isEmpty(), "commits");
    this.commits = commits;
    this.authors = checkNotNull(authors, "authors");
    this.name = checkNotNull(name, "name");
    this.title = nullToEmpty(title);
    this.hasTitle = !isNullOrEmpty(title);
    this.description = nullToEmpty(description);
    this.hasDescription = !isNullOrEmpty(description);
    this.issue = nullToEmpty(issue);
    this.issueType = checkNotNull(issueType, "issueType");
    this.hasIssue = !isNullOrEmpty(issue);
    this.link = nullToEmpty(link);
    this.hasLink = !isNullOrEmpty(link);
    this.hasType = !isNullOrEmpty(type);
    this.type = nullToEmpty(type);
    this.hasLabels = labels != null && !labels.isEmpty();
    this.hasLinkedIssues = linkedIssues != null && !linkedIssues.isEmpty();
    this.linkedIssues = linkedIssues;
    this.labels = labels;
  }

  public SettingsIssueType getIssueType() {
    return this.issueType;
  }

  public boolean isJira() {
    return this.issueType == JIRA;
  }

  public boolean isGitHub() {
    return this.issueType == GITHUB;
  }

  public boolean isGitLab() {
    return this.issueType == GITLAB;
  }

  public boolean isCustom() {
    return this.issueType == CUSTOM;
  }

  public boolean isNoIssue() {
    return this.issueType == NOISSUE;
  }

  public String getTitle() {
    return this.title;
  }

  public boolean getHasTitle() {
    return this.hasTitle;
  }

  public boolean getHasIssue() {
    return this.hasIssue;
  }

  public boolean getHasLabels() {
    return this.hasLabels;
  }

  public boolean getHasType() {
    return this.hasType;
  }

  public boolean getHasLink() {
    return this.hasLink;
  }

  public String getIssue() {
    return this.issue;
  }

  public String getLink() {
    return this.link;
  }

  public String getName() {
    return this.name;
  }

  public String getType() {
    return this.type;
  }

  public boolean getHasDescription() {
    return this.hasDescription;
  }

  public String getDescription() {
    return this.description;
  }

  @Override
  public List<Author> getAuthors() {
    return this.authors;
  }

  @Override
  public List<Commit> getCommits() {
    return this.commits;
  }

  public List<String> getLabels() {
    return this.labels;
  }

  public List<String> getLinkedIssues() {
    return this.linkedIssues;
  }

  public boolean getHasLinkedIssues() {
    return this.hasLinkedIssues;
  }

  @Override
  public String toString() {
    return "Issue: " + this.issue + " Title: " + this.title;
  }
}
