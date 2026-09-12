package se.bjurr.gitchangelog.internal.integrations.gitlab;

import java.util.List;

public class GitLabIssue {
  public final String title;
  public final String web_url;
  private final List<String> labels;

  public GitLabIssue(final String title, final String web_url, final List<String> labels) {
    this.title = title;
    this.web_url = web_url;
    this.labels = labels;
  }

  public List<String> getLabels() {
    return this.labels;
  }

  public String getLink() {
    return this.web_url;
  }

  public String getTitle() {
    return this.title;
  }

  @Override
  public String toString() {
    return "GitLabIssue [title="
        + this.title
        + ", web_url="
        + this.web_url
        + ", labels="
        + this.labels
        + "]";
  }
}
