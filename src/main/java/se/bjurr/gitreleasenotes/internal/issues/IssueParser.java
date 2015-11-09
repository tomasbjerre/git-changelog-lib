package se.bjurr.gitreleasenotes.internal.issues;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.regex.Pattern.compile;
import static se.bjurr.gitreleasenotes.internal.model.ParsedIssueType.CUSTOM;
import static se.bjurr.gitreleasenotes.internal.model.ParsedIssueType.JIRA;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.bjurr.gitreleasenotes.internal.git.model.GitCommit;
import se.bjurr.gitreleasenotes.internal.model.ParsedIssue;
import se.bjurr.gitreleasenotes.internal.settings.Settings;
import se.bjurr.gitreleasenotes.internal.settings.SettingsIssue;

public class IssueParser {

 private final List<GitCommit> commits;
 private final Settings settings;

 public IssueParser(Settings settings, List<GitCommit> commits) {
  this.settings = settings;
  this.commits = commits;
 }

 public Settings getSettings() {
  return settings;
 }

 public List<GitCommit> getCommits() {
  return commits;
 }

 public List<ParsedIssue> parseForIssues() {
  List<ParsedIssue> foundIssues = newArrayList();
  for (GitCommit gitCommit : commits) {
   for (SettingsIssue issuePattern : settings.getIssues()) {
    Pattern pattern = compile(issuePattern.getPattern());
    Matcher matcher = pattern.matcher(gitCommit.getMessage());
    if (matcher.find()) {
     String matched = matcher.group();
     if (issuePattern.getType() == JIRA) {
      // TODO: Use Jira REST API
     } else if (issuePattern.getType() == CUSTOM) {
      foundIssues.add(new ParsedIssue(//
        gitCommit, //
        issuePattern.getType(), //
        issuePattern.getName(), //
        matched));//
     }
    }
   }
  }
  return foundIssues;
 }
}
