package se.bjurr.gitchangelog.api.model;

import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.CUSTOM;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.GITHUB;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.GITLAB;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.JIRA;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.NOISSUE;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.REDMINE;
import static se.bjurr.gitchangelog.internal.util.Preconditions.checkNotNull;
import static se.bjurr.gitchangelog.internal.util.Preconditions.checkState;
import static se.bjurr.gitchangelog.internal.util.Preconditions.isNullOrEmpty;
import static se.bjurr.gitchangelog.internal.util.Preconditions.nullToEmpty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.bjurr.gitchangelog.api.model.interfaces.IAuthors;
import se.bjurr.gitchangelog.api.model.interfaces.ICommits;
import se.bjurr.gitchangelog.internal.settings.SettingsIssueType;

public class Issue implements ICommits, IAuthors, Serializable {

  private static final long serialVersionUID = -7571341639024417199L;
  private final List<Commit> commits;
  private final List<Author> authors;
  /** Like JIRA, or GitHub. */
  private final String name;

  /** Like the title of a Jira. */
  private final String title;

  private final boolean hasTitle;

  /** Like the actual Jira, JIR-ABC. */
  private final String issue;

  private final boolean hasIssue;

  /** A link to the issue, http://..... */
  private final String link;

  private final boolean hasLink;

  /** Type of issue, perhaps Story, Bug and etc */
  private final String type;

  private final boolean hasType;

  private final boolean hasDescription;
  private final String description;
  /** Labels on the issue, for GitHub it may be bug, enhancement, ... */
  private final List<String> labels;

  private final boolean hasLabels;
  private final boolean hasLinkedIssues;
  private final SettingsIssueType issueType;
  private final List<String> linkedIssues;

  private final boolean hasAdditionalFields;
  private final Map<String, Object> additionalFields;

  public Issue(
      final List<Commit> commits,
      final List<Author> authors,
      final String name,
      final String title,
      final String issue,
      final SettingsIssueType issueType,
      final String description,
      final String link,
      final String type,
      final List<String> linkedIssues,
      final List<String> labels,
      final Map<String, Object> additionalFields) {
    checkState(!commits.isEmpty(), "commits");
    this.commits = commits;
    this.authors = checkNotNull(authors, "authors");
    this.name = checkNotNull(name, "name");
    this.title = nullToEmpty(title);
    this.hasTitle = !isNullOrEmpty(title);
    this.description = nullToEmpty(description);
    this.hasDescription = !isNullOrEmpty(description);
    this.issue = nullToEmpty(issue);
    this.issueType = checkNotNull(issueType, "issueType");
    this.hasIssue = !isNullOrEmpty(issue);
    this.link = nullToEmpty(link);
    this.hasLink = !isNullOrEmpty(link);
    this.hasType = !isNullOrEmpty(type);
    this.type = nullToEmpty(type);
    this.hasLabels = labels != null && !labels.isEmpty();
    this.hasLinkedIssues = linkedIssues != null && !linkedIssues.isEmpty();
    this.linkedIssues = linkedIssues;
    this.labels = labels;
    this.hasAdditionalFields = additionalFields != null && !additionalFields.isEmpty();
    this.additionalFields = additionalFields;
  }

  public SettingsIssueType getIssueType() {
    return this.issueType;
  }

  public boolean isJira() {
    return this.issueType == JIRA;
  }

  public boolean isRedmine() {
    return this.issueType == REDMINE;
  }

  public boolean isGitHub() {
    return this.issueType == GITHUB;
  }

  public boolean isGitLab() {
    return this.issueType == GITLAB;
  }

  public boolean isCustom() {
    return this.issueType == CUSTOM;
  }

  public boolean isNoIssue() {
    return this.issueType == NOISSUE;
  }

  public String getTitle() {
    return this.title;
  }

  public boolean getHasTitle() {
    return this.hasTitle;
  }

  public boolean getHasIssue() {
    return this.hasIssue;
  }

  public boolean getHasLabels() {
    return this.hasLabels;
  }

  public boolean getHasType() {
    return this.hasType;
  }

  public boolean getHasLink() {
    return this.hasLink;
  }

  public String getIssue() {
    return this.issue;
  }

  public String getLink() {
    return this.link;
  }

  public String getName() {
    return this.name;
  }

  public String getType() {
    return this.type;
  }

  public boolean getHasDescription() {
    return this.hasDescription;
  }

  public String getDescription() {
    return this.description;
  }

  @Override
  public List<Author> getAuthors() {
    return this.authors;
  }

  @Override
  public List<Commit> getCommits() {
    return this.commits;
  }

  public List<String> getLabels() {
    return this.labels;
  }

  public List<String> getLinkedIssues() {
    return this.linkedIssues;
  }

  public boolean getHasLinkedIssues() {
    return this.hasLinkedIssues;
  }

  public boolean getHasAdditionalFields() {
    return this.hasAdditionalFields;
  }

  public Set<Map.Entry<String,Object>> getAdditionalFields() {
    return this.additionalFields.entrySet();
  }

  public Map<String,Object> getAdditionalFieldsMap() {
    return this.additionalFields;
  }

  @Override
  public String toString() {
    return "Issue: " + this.issue + " Title: " + this.title;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.authors == null) ? 0 : this.authors.hashCode());
    result = prime * result + ((this.commits == null) ? 0 : this.commits.hashCode());
    result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
    result = prime * result + (this.hasDescription ? 1231 : 1237);
    result = prime * result + (this.hasIssue ? 1231 : 1237);
    result = prime * result + (this.hasLabels ? 1231 : 1237);
    result = prime * result + (this.hasLink ? 1231 : 1237);
    result = prime * result + (this.hasLinkedIssues ? 1231 : 1237);
    result = prime * result + (this.hasTitle ? 1231 : 1237);
    result = prime * result + (this.hasType ? 1231 : 1237);
    result = prime * result + (this.hasAdditionalFields ? 1231 : 1237);
    result = prime * result + ((this.issue == null) ? 0 : this.issue.hashCode());
    result = prime * result + ((this.issueType == null) ? 0 : this.issueType.hashCode());
    result = prime * result + ((this.labels == null) ? 0 : this.labels.hashCode());
    result = prime * result + ((this.link == null) ? 0 : this.link.hashCode());
    result = prime * result + ((this.linkedIssues == null) ? 0 : this.linkedIssues.hashCode());
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.title == null) ? 0 : this.title.hashCode());
    result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
    result = prime * result + ((this.additionalFields == null) ? 0 : this.additionalFields.hashCode());
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
    final Issue other = (Issue) obj;
    if (this.authors == null) {
      if (other.authors != null) {
        return false;
      }
    } else if (!this.authors.equals(other.authors)) {
      return false;
    }
    if (this.commits == null) {
      if (other.commits != null) {
        return false;
      }
    } else if (!this.commits.equals(other.commits)) {
      return false;
    }
    if (this.description == null) {
      if (other.description != null) {
        return false;
      }
    } else if (!this.description.equals(other.description)) {
      return false;
    }
    if (this.hasDescription != other.hasDescription) {
      return false;
    }
    if (this.hasIssue != other.hasIssue) {
      return false;
    }
    if (this.hasLabels != other.hasLabels) {
      return false;
    }
    if (this.hasLink != other.hasLink) {
      return false;
    }
    if (this.hasLinkedIssues != other.hasLinkedIssues) {
      return false;
    }
    if (this.hasTitle != other.hasTitle) {
      return false;
    }
    if (this.hasType != other.hasType) {
      return false;
    }
    if (this.hasAdditionalFields != other.hasAdditionalFields) {
      return false;
    }
    if (this.issue == null) {
      if (other.issue != null) {
        return false;
      }
    } else if (!this.issue.equals(other.issue)) {
      return false;
    }
    if (this.issueType != other.issueType) {
      return false;
    }
    if (this.labels == null) {
      if (other.labels != null) {
        return false;
      }
    } else if (!this.labels.equals(other.labels)) {
      return false;
    }
    if (this.link == null) {
      if (other.link != null) {
        return false;
      }
    } else if (!this.link.equals(other.link)) {
      return false;
    }
    if (this.linkedIssues == null) {
      if (other.linkedIssues != null) {
        return false;
      }
    } else if (!this.linkedIssues.equals(other.linkedIssues)) {
      return false;
    }
    if (this.name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!this.name.equals(other.name)) {
      return false;
    }
    if (this.title == null) {
      if (other.title != null) {
        return false;
      }
    } else if (!this.title.equals(other.title)) {
      return false;
    }
    if (this.type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!this.type.equals(other.type)) {
      return false;
    }
    if (this.additionalFields == null) {
      if (other.additionalFields != null) {
        return false;
      }
    } else if (!this.additionalFields.equals(other.additionalFields)) {
      return false;
    }
    return true;
  }
}
