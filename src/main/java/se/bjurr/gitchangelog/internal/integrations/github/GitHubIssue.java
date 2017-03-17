package se.bjurr.gitchangelog.internal.integrations.github;

public class GitHubIssue {
  public final String title;
  public final String html_url;
  public final String number;

  public GitHubIssue(String title, String link, String number) {
    this.title = title;
    this.html_url = link;
    this.number = number;
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
}
