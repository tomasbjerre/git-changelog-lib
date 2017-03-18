package se.bjurr.gitchangelog.internal.integrations.github;

import java.util.List;

public class GitHubIssue {
  public final String title;
  public final String html_url;
  public final String number;
  private final List<GitHubLabel> labels;

  public GitHubIssue(String title, String link, String number, List<GitHubLabel> labels) {
    this.title = title;
    this.html_url = link;
    this.number = number;
    this.labels = labels;
  }

  public List<GitHubLabel> getLabels() {
    return labels;
  }

  public String getNumber() {
    return number;
  }

  public String getLink() {
    return html_url;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public String toString() {
    return "GitHubIssue [title="
        + title
        + ", html_url="
        + html_url
        + ", number="
        + number
        + ", labels="
        + labels
        + "]";
  }
}
