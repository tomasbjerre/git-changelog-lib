package se.bjurr.gitchangelog.internal.settings;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.GITHUB;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.JIRA;

import java.util.List;

public class IssuesUtil {
 private final Settings settings;

 public IssuesUtil(Settings settings) {
  this.settings = settings;
 }

 public List<SettingsIssue> getIssues() {
  List<SettingsIssue> issues = newArrayList(settings.getCustomIssues());
  addJira(issues);
  addGitHub(issues);
  return issues;
 }

 private void addGitHub(List<SettingsIssue> issues) {
  if (!isNullOrEmpty(settings.getGitHubIssuePattern())) {
   if (settings.getGitHubApi().isPresent()) {
    issues.add(new SettingsIssue(GITHUB, "GitHub", settings.getGitHubIssuePattern(), null));
   }
  }
 }

 private void addJira(List<SettingsIssue> issues) {
  if (!isNullOrEmpty(settings.getJiraIssuePattern())) {
   if (settings.getJiraServer().isPresent()) {
    issues.add(new SettingsIssue(JIRA, "Jira", settings.getJiraIssuePattern(), settings.getJiraServer().or("")
      + "/browse/${PATTERN_GROUP}"));
   } else {
    issues.add(new SettingsIssue(JIRA, "Jira", settings.getJiraIssuePattern(), settings.getJiraServer().orNull()));
   }
  }
 }
}
