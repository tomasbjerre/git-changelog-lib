package se.bjurr.gitchangelog.api.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.CUSTOM;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.GITHUB;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.GITLAB;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.JIRA;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.NOISSUE;

import java.io.Serializable;
import java.util.List;
import se.bjurr.gitchangelog.internal.settings.SettingsIssueType;

public class IssueType implements Serializable {

  private static final long serialVersionUID = 8850522973130773607L;
  private final String name;
  private final List<Issue> issues;
  private final SettingsIssueType type;

  public IssueType(List<Issue> issues, String name) {
    this.name = checkNotNull(name, "name");
    this.issues = checkNotNull(issues, "issues");
    checkState(!issues.isEmpty(), "Issues empty!");
    this.type = issues.get(0).getIssueType();
  }

  public SettingsIssueType getType() {
    return type;
  }

  public boolean isJira() {
    return type == JIRA;
  }

  public boolean isGitHub() {
    return type == GITHUB;
  }

  public boolean isGitLab() {
    return type == GITLAB;
  }

  public boolean isCustom() {
    return type == CUSTOM;
  }

  public boolean isNoIssue() {
    return type == NOISSUE;
  }

  public String getName() {
    return name;
  }

  public List<Issue> getIssues() {
    return issues;
  }

  @Override
  public String toString() {
    return "IssueType: " + name;
  }
}
