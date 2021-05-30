package se.bjurr.gitchangelog.api.model;

import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.CUSTOM;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.GITHUB;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.GITLAB;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.JIRA;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.NOISSUE;
import static se.bjurr.gitchangelog.internal.util.Preconditions.checkNotNull;
import static se.bjurr.gitchangelog.internal.util.Preconditions.checkState;

import java.io.Serializable;
import java.util.List;
import se.bjurr.gitchangelog.internal.settings.SettingsIssueType;

public class IssueType implements Serializable {

  private static final long serialVersionUID = 8850522973130773606L;
  private final String name;
  private final List<Issue> issues;
  private final SettingsIssueType type;

  public IssueType(final List<Issue> issues, final String name) {
    this.name = checkNotNull(name, "name");
    this.issues = checkNotNull(issues, "issues");
    checkState(!issues.isEmpty(), "Issues empty!");
    this.type = issues.get(0).getIssueType();
  }

  public SettingsIssueType getType() {
    return this.type;
  }

  public boolean isJira() {
    return this.type == JIRA;
  }

  public boolean isGitHub() {
    return this.type == GITHUB;
  }

  public boolean isGitLab() {
    return this.type == GITLAB;
  }

  public boolean isCustom() {
    return this.type == CUSTOM;
  }

  public boolean isNoIssue() {
    return this.type == NOISSUE;
  }

  public String getName() {
    return this.name;
  }

  public List<Issue> getIssues() {
    return this.issues;
  }

  @Override
  public String toString() {
    return "IssueType: " + this.name;
  }
}
