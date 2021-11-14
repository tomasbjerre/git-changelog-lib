package se.bjurr.gitchangelog.api.model;

import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.CUSTOM;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.GITHUB;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.GITLAB;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.JIRA;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.REDMINE;
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

  public boolean isRedmine() {
    return this.type == REDMINE;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.issues == null) ? 0 : this.issues.hashCode());
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final IssueType other = (IssueType) obj;
    if (this.issues == null) {
      if (other.issues != null) {
        return false;
      }
    } else if (!this.issues.equals(other.issues)) {
      return false;
    }
    if (this.name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!this.name.equals(other.name)) {
      return false;
    }
    if (this.type != other.type) {
      return false;
    }
    return true;
  }
}
