package se.bjurr.gitchangelog.internal.model;

import static se.bjurr.gitchangelog.internal.util.Preconditions.checkNotNull;
import static se.bjurr.gitchangelog.internal.util.Preconditions.emptyToNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.model.interfaces.IGitCommitReferer;
import se.bjurr.gitchangelog.internal.settings.SettingsIssueType;

public class ParsedIssue implements IGitCommitReferer {
  private final List<GitCommit> gitCommits = new ArrayList<>();
  private final String name;
  private final String title;
  private final String link;
  private final String issue;
  private final String issueType;
  private final List<String> linkedIssues;
  private final List<String> labels;
  private final String description;
  private final SettingsIssueType settingsIssueType;
  private final Map<String, Object> additionalFields;

  public ParsedIssue(
      final SettingsIssueType settingsIssueType,
      final String name,
      final String issue,
      final String description,
      final String link,
      final String title,
      final String issueType,
      final List<String> linkedIssues,
      final List<String> labels,
      final Map<String, Object> additionalFields) {
    this.name = checkNotNull(name, "name");
    this.title = emptyToNull(title);
    this.issue = issue;
    this.link = link;
    this.issueType = issueType;
    this.settingsIssueType = settingsIssueType;
    this.linkedIssues = linkedIssues;
    this.labels = labels;
    this.description = description;
    this.additionalFields = additionalFields;
  }

  public SettingsIssueType getSettingsIssueType() {
    return this.settingsIssueType;
  }

  public Optional<String> getTitle() {
    return Optional.ofNullable(this.title);
  }

  @Override
  public GitCommit getGitCommit() {
    return checkNotNull(this.gitCommits.get(0), this.name);
  }

  public List<GitCommit> getGitCommits() {
    return this.gitCommits;
  }

  public String getLink() {
    return this.link;
  }

  public String getIssueType() {
    return this.issueType;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String toString() {
    return this.name + this.issue;
  }

  public void addCommit(final GitCommit gitCommit) {
    this.gitCommits.add(gitCommit);
  }

  public String getIssue() {
    return this.issue;
  }

  public String getDescription() {
    return this.description;
  }

  public void addCommits(final List<GitCommit> commits) {
    this.gitCommits.addAll(commits);
  }

  public List<String> getLabels() {
    return this.labels;
  }

  public List<String> getLinkedIssues() {
    return this.linkedIssues;
  }

  public Map<String, Object> getAdditionalFields() {
    return this.additionalFields;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name);
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
    final ParsedIssue other = (ParsedIssue) obj;
    return Objects.equals(this.name, other.name);
  }
}
