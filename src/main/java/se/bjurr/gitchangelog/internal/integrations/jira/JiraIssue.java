package se.bjurr.gitchangelog.internal.integrations.jira;

public class JiraIssue {
  private final String title;
  private final String link;
  private final String issue;
  private final String type;

  public JiraIssue(String title, String link, String issue, String type) {
    this.title = title;
    this.link = link;
    this.issue = issue;
    this.type = type;
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

  public String getType() {
    return type;
  }
}
