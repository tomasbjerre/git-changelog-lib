package se.bjurr.gitchangelog.internal.integrations.jira;

import java.util.List;

public class JiraIssue {

  private final String title;
  private final String link;
  private final String issue;
  private final String issueType;
  private final String description;
  private final List<String> labels;

  public JiraIssue(
      String title,
      String description,
      String link,
      String issue,
      String issueType,
      List<String> labels) {
    this.title = title;
    this.link = link;
    this.issue = issue;
    this.issueType = issueType;
    this.labels = labels;
    this.description = description;
  }

  public String getIssue() {
    return issue;
  }

  public String getLink() {
    return link;
  }

  public String getTitle() {
    return title;
  }

  public String getIssueType() {
    return issueType;
  }

  public List<String> getLabels() {
    return labels;
  }

  public String getDescription() {

    return description;
  }

  @Override
  public String toString() {
    return "JiraIssue [title="
        + title
        + ", link="
        + link
        + ", issue="
        + issue
        + ", issueType="
        + issueType
        + ", labels="
        + labels
        + "]";
  }
}
