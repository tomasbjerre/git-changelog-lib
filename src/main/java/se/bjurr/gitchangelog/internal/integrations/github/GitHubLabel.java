package se.bjurr.gitchangelog.internal.integrations.github;

public class GitHubLabel {
  private final String name;

  public GitHubLabel(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "GitHubLabel [name=" + name + "]";
  }
}
