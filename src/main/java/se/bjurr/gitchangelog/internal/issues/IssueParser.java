package se.bjurr.gitchangelog.internal.issues;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Ordering.usingToString;
import static java.util.regex.Pattern.compile;
import static org.slf4j.LoggerFactory.getLogger;
import static se.bjurr.gitchangelog.internal.integrations.github.GitHubServiceFactory.getGitHubService;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.GITHUB;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.JIRA;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.slf4j.Logger;

import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.integrations.github.GitHubHelper;
import se.bjurr.gitchangelog.internal.integrations.github.GitHubIssue;
import se.bjurr.gitchangelog.internal.integrations.jira.JiraClient;
import se.bjurr.gitchangelog.internal.integrations.jira.JiraClientFactory;
import se.bjurr.gitchangelog.internal.integrations.jira.JiraIssue;
import se.bjurr.gitchangelog.internal.model.ParsedIssue;
import se.bjurr.gitchangelog.internal.settings.IssuesUtil;
import se.bjurr.gitchangelog.internal.settings.Settings;
import se.bjurr.gitchangelog.internal.settings.SettingsIssue;

public class IssueParser {
 private static final Logger LOG = getLogger(IssueParser.class);

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
  Map<String, ParsedIssue> parsedIssuePerIssue = newHashMap();

  GitHubHelper gitHubHelper = createGitHubClient();
  JiraClient jiraClient = createJiraClient();

  List<SettingsIssue> patterns = new IssuesUtil(settings).getIssues();

  for (GitCommit gitCommit : commits) {
   boolean commitMappedToAtLeastOneIssue = false;
   for (SettingsIssue issuePattern : patterns) {
    Matcher issueMatcher = compile(issuePattern.getPattern()).matcher(gitCommit.getMessage());
    while (issueMatcher.find()) {
     String matchedIssue = issueMatcher.group();
     if (!parsedIssuePerIssue.containsKey(matchedIssue)) {
      ParsedIssue parsedIssue = getParsedIssue(gitHubHelper, jiraClient, gitCommit, issuePattern, issueMatcher,
        matchedIssue);
      parsedIssuePerIssue.put(matchedIssue, parsedIssue);
     }
     if (!parsedIssuePerIssue.get(matchedIssue).getGitCommits().contains(gitCommit)) {
      parsedIssuePerIssue.get(matchedIssue).getGitCommits().add(gitCommit);
     }
     commitMappedToAtLeastOneIssue = true;
    }
   }
   if (!commitMappedToAtLeastOneIssue && !settings.ignoreCommitsWithoutIssue()) {
    ParsedIssue noIssue = new ParsedIssue(settings.getNoIssueName(), null, null);
    if (!parsedIssuePerIssue.containsKey(noIssue.getName())) {
     parsedIssuePerIssue.put(noIssue.getName(), noIssue);
    }
    parsedIssuePerIssue.get(noIssue.getName()).addCommit(gitCommit);
   }
  }
  return usingToString().sortedCopy(parsedIssuePerIssue.values());
 }

 private JiraClient createJiraClient() {
  JiraClient jiraClient = null;
  if (settings.getJiraServer().isPresent()) {
   jiraClient = JiraClientFactory.createJiraClient(settings.getJiraServer().get());
   if (settings.getJiraUsername().isPresent()) {
    jiraClient.withBasicCredentials(settings.getJiraUsername().get(), settings.getJiraPassword().get());
   }
  }
  return jiraClient;
 }

 private GitHubHelper createGitHubClient() {
  GitHubHelper gitHubHelper = null;
  if (settings.getGitHubApi().isPresent()) {
   gitHubHelper = new GitHubHelper(getGitHubService(settings.getGitHubApi().get(), settings.getGitHubToken()));
  }
  return gitHubHelper;
 }

 private ParsedIssue getParsedIssue(GitHubHelper gitHubHelper, JiraClient jiraClient, GitCommit gitCommit,
   SettingsIssue issuePattern, Matcher issueMatcher, String matchedIssue) {
  if (issuePattern.getType() == GITHUB) {
   String link = "";
   String title = "";
   try {
    if (gitHubHelper != null && gitHubHelper.getIssueFromAll(matchedIssue).isPresent()) {
     GitHubIssue gitHubIssue = gitHubHelper.getIssueFromAll(matchedIssue).get();
     link = gitHubIssue.getLink();
     title = gitHubIssue.getTitle();
    }
   } catch (GitChangelogIntegrationException e) {
    LOG.error(matchedIssue, e);
   }
   return new ParsedIssue(//
     issuePattern.getName(),//
     matchedIssue,//
     link, //
     title);
  } else if (issuePattern.getType() == JIRA) {
   String link = "";
   String title = "";
   try {
    if (jiraClient != null && jiraClient.getIssue(matchedIssue).isPresent()) {
     JiraIssue jiraIssue = jiraClient.getIssue(matchedIssue).get();
     link = jiraIssue.getLink();
     title = jiraIssue.getTitle();
    }
   } catch (GitChangelogIntegrationException e) {
    LOG.error(matchedIssue, e);
   }
   return new ParsedIssue(//
     issuePattern.getName(),//
     matchedIssue,//
     link, //
     title);
  } else {
   String link = render(issuePattern.getLink().or(""), issueMatcher, matchedIssue);
   String title = render(issuePattern.getTitle().or(""), issueMatcher, matchedIssue);
   return new ParsedIssue(//
     issuePattern.getName(),//
     matchedIssue,//
     link,//
     title);
  }
 }

 private String render(String string, Matcher matcher, String matched) {
  string = string.replaceAll("\\$\\{PATTERN_GROUP\\}", matched);
  for (int i = 0; i <= matcher.groupCount(); i++) {
   string = string.replaceAll("\\$\\{PATTERN_GROUP_" + i + "\\}", firstNonNull(matcher.group(i), ""));
  }
  return string;
 }
}
