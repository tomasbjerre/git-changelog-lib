package se.bjurr.gitchangelog.internal.settings;

import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.GITHUB;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.GITLAB;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.JIRA;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.REDMINE;
import static se.bjurr.gitchangelog.internal.util.Preconditions.isNullOrEmpty;

import java.util.ArrayList;
import java.util.List;

public class IssuesUtil {
  private final Settings settings;

  public IssuesUtil(final Settings settings) {
    this.settings = settings;
  }

  public List<SettingsIssue> getIssues() {
    final List<SettingsIssue> issues = new ArrayList<>(this.settings.getCustomIssues());
    this.addJira(issues);
    this.addGitHub(issues);
    this.addGitLab(issues);
    this.addRedmine(issues);
    return issues;
  }

  private void addGitHub(final List<SettingsIssue> issues) {
    if (!isNullOrEmpty(this.settings.getGitHubIssuePattern())) {
      if (this.settings.getGitHubApi().isPresent()) {
        issues.add(
            new SettingsIssue(GITHUB, "GitHub", this.settings.getGitHubIssuePattern(), null, null));
      }
    }
  }

  private void addGitLab(final List<SettingsIssue> issues) {
    if (!isNullOrEmpty(this.settings.getGitLabIssuePattern())) {
      if (this.settings.getGitLabServer().isPresent()) {
        issues.add(
            new SettingsIssue(GITLAB, "GitLab", this.settings.getGitLabIssuePattern(), null, null));
      }
    }
  }

  private void addJira(final List<SettingsIssue> issues) {
    if (!isNullOrEmpty(this.settings.getJiraIssuePattern())) {
      if (this.settings.getJiraServer().isPresent()) {
        issues.add(
            new SettingsIssue(
                JIRA,
                "Jira",
                this.settings.getJiraIssuePattern(),
                this.settings.getJiraServer().orElse("") + "/browse/${PATTERN_GROUP}",
                null));
      } else {
        issues.add(
            new SettingsIssue(
                JIRA,
                "Jira",
                this.settings.getJiraIssuePattern(),
                this.settings.getJiraServer().orElse(null),
                null));
      }
    }
  }

  private void addRedmine(final List<SettingsIssue> issues) {
    if (!isNullOrEmpty(this.settings.getRedmineIssuePattern())) {
      if (this.settings.getRedmineServer().isPresent()) {
        issues.add(
            new SettingsIssue(
                REDMINE,
                "Redmine",
                this.settings.getRedmineIssuePattern(),
                this.settings.getRedmineServer().orElse("") + "/issues/${PATTERN_GROUP}",
                null));
      } else {
        issues.add(
            new SettingsIssue(
                REDMINE,
                "Redmine",
                this.settings.getRedmineIssuePattern(),
                this.settings.getRedmineServer().orElse(null),
                null));
      }
    }
  }
}
