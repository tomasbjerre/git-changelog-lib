package se.bjurr.gitchangelog.internal.issues;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.regex.Pattern.compile;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.model.ParsedIssue;
import se.bjurr.gitchangelog.internal.settings.CustomIssue;
import se.bjurr.gitchangelog.internal.settings.Settings;

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
   for (CustomIssue issuePattern : settings.getCustomIssues()) {
    Pattern pattern = compile(issuePattern.getPattern());
    Matcher matcher = pattern.matcher(gitCommit.getMessage());
    if (matcher.find()) {
     String matched = matcher.group();
     foundIssues.add(new ParsedIssue(//
       gitCommit, //
       issuePattern.getLink().or("").replaceAll("${PATTERN_GROUP}", matched), //
       issuePattern.getName()));//
    }
   }
  }
  return foundIssues;
 }
}
