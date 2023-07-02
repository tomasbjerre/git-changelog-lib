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
      final String title,
      final String description,
      final String link,
      final String issue,
      final String issueType,
      final List<String> linkedIssues,
      final List<String> labels,
      final Map<String, Object> additionalFields) {
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
    return this.issue;
  }

  public String getLink() {
    return this.link;
  }

  public String getTitle() {
    return this.title;
  }

  public String getIssueType() {
    return this.issueType;
  }

  public List<String> getLinkedIssues() {
    return this.linkedIssues;
  }

  public List<String> getLabels() {
    return this.labels;
  }

  public String getDescription() {
    return this.description;
  }

  public Map<String, Object> getAdditionalFields() {
    return this.additionalFields;
  }

  @Override
  public String toString() {
    return "JiraIssue [title="
        + this.title
        + ", link="
        + this.link
        + ", issue="
        + this.issue
        + ", issueType="
        + this.issueType
        + ", description="
        + this.description
        + ", linkedIssues="
        + this.linkedIssues
        + ", labels="
        + this.labels
        + ", additionalFields="
        + this.additionalFields
        + "]";
  }
}
