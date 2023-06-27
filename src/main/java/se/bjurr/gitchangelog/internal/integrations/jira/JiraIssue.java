package se.bjurr.gitchangelog.internal.integrations.jira;

import java.util.List;
import java.util.Map;

public class JiraIssue {

  private final String title;
  private final String link;
  private final String issue;
  private final String issueType;
  private final String description;
  private final List<String> linkedIssues;
  private final List<String> labels;
  private final Map<String, Object> additionalFields;

  public JiraIssue(
      String title,
      String description,
      String link,
      String issue,
      String issueType,
      List<String> linkedIssues,
      List<String> labels,
      Map<String, Object> additionalFields) {
    this.title = title;
    this.link = link;
    this.issue = issue;
    this.issueType = issueType;
    this.linkedIssues = linkedIssues;
    this.labels = labels;
    this.description = description;
    this.additionalFields = additionalFields;
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

  public List<String> getLinkedIssues() {
    return linkedIssues;
  }

  public List<String> getLabels() {
    return labels;
  }

  public String getDescription() {
    return description;
  }

  public Map<String, Object> getAdditionalFields() {
    return additionalFields;
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
        + ", linkedIssues="
        + linkedIssues
        + ", labels="
        + labels
        + "]";
  }
}
