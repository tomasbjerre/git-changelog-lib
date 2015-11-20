package se.bjurr.gitchangelog.internal.settings;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.io.Resources.getResource;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.io.Resources;
import com.google.gson.Gson;

public class Settings {
 public static final String DEFAULT_FILE = "git-changelog-settings.json";
 public static final boolean DEFAULT_REMOVE_ISSUE = true;
 public static final String DEFAULT_TIMEZONE = "UTC";
 public static final String DEFAULT_DATEFORMAT = "YYYY-MM-dd HH:mm:ss";
 public static final String DEFAULT_IGNORE_COMMITS_REGEXP = "^\\[maven-release-plugin\\].*|^\\[Gradle Release Plugin\\].*|^Merge.*";
 public static final String DEFAULT_UNTAGGED_NAME = "Unreleased";
 public static final String DEFAULT_READABLE_TAG_NAME = "/([^/]+?)$";
 public static final String DEFAULT_NO_ISSUE_NAME = "No issue";
 public static final String DEFAULT_GITHUB_ISSUE_PATTERN = "#[0-9]+";
 public static final String DEFAULT_JIRA_ISSUE_PATTEN = "\\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\\b";

 private static Gson gson = new Gson();

 private String fromRepo;
 private String fromRef;
 private String toRef;
 private String ignoreCommitsIfMessageMatches;
 private String jiraServer;
 private String jiraIssuePattern;
 private String toCommit;
 private String fromCommit;
 private String untaggedName;
 private String templatePath;
 private String readableTagName;
 private String dateFormat;
 private String noIssueName;
 private String timeZone;
 private boolean removeIssueFromMessage;
 private List<CustomIssue> customIssues;

 public Settings() {
 }

 public void setCustomIssues(List<CustomIssue> customIssues) {
  this.customIssues = customIssues;
 }

 public void setFromRef(String fromRef) {
  this.fromRef = fromRef;
 }

 public void setToRef(String toRef) {
  this.toRef = toRef;
 }

 public Optional<String> getFromRef() {
  return fromNullable(fromRef);
 }

 public Optional<String> getToRef() {
  return fromNullable(toRef);
 }

 public void setFromRepo(String fromRepo) {
  this.fromRepo = fromRepo;
 }

 public String getFromRepo() {
  return fromNullable(fromRepo).or(".");
 }

 public void setIgnoreCommitsIfMessageMatches(String ignoreCommitsIfMessageMatches) {
  this.ignoreCommitsIfMessageMatches = ignoreCommitsIfMessageMatches;
 }

 public void setJiraIssuePattern(String jiraIssuePattern) {
  this.jiraIssuePattern = jiraIssuePattern;
 }

 public void setJiraServer(String jiraServer) {
  this.jiraServer = jiraServer;
 }

 public List<CustomIssue> getCustomIssues() {
  return firstNonNull(customIssues, new ArrayList<CustomIssue>());
 }

 public String getIgnoreCommitsIfMessageMatches() {
  return fromNullable(ignoreCommitsIfMessageMatches).or(DEFAULT_IGNORE_COMMITS_REGEXP);
 }

 public String getJiraIssuePattern() {
  return fromNullable(jiraIssuePattern).or(DEFAULT_JIRA_ISSUE_PATTEN);
 }

 public Optional<String> getJiraServer() {
  return fromNullable(jiraServer);
 }

 public static Settings fromFile(URL url) {
  try {
   return gson.fromJson(Resources.toString(url, UTF_8), Settings.class);
  } catch (Exception e) {
   throw new RuntimeException("Cannot read " + url, e);
  }
 }

 public void setFromCommit(String fromCommit) {
  this.fromCommit = fromCommit;
 }

 public void setToCommit(String toCommit) {
  this.toCommit = toCommit;
 }

 public Optional<String> getFromCommit() {
  return fromNullable(fromCommit);
 }

 public Optional<String> getToCommit() {
  return fromNullable(toCommit);
 }

 public String getUntaggedName() {
  return fromNullable(untaggedName).or(DEFAULT_UNTAGGED_NAME);
 }

 public void setUntaggedName(String untaggedName) {
  this.untaggedName = untaggedName;
 }

 public String getTemplatePath() {
  return fromNullable(templatePath).or("changelog.mustache");
 }

 public void setTemplatePath(String templatePath) {
  this.templatePath = templatePath;
 }

 public String getReadableTagName() {
  return fromNullable(readableTagName).or(DEFAULT_READABLE_TAG_NAME);
 }

 public String getDateFormat() {
  return fromNullable(dateFormat).or(DEFAULT_DATEFORMAT);
 }

 public void setDateFormat(String dateFormat) {
  this.dateFormat = dateFormat;
 }

 public void setNoIssueName(String noIssueName) {
  this.noIssueName = noIssueName;
 }

 public void setReadableTagName(String readableTagName) {
  this.readableTagName = readableTagName;
 }

 public String getNoIssueName() {
  return fromNullable(noIssueName).or(DEFAULT_NO_ISSUE_NAME);
 }

 public void setTimeZone(String timeZone) {
  this.timeZone = timeZone;
 }

 public String getTimeZone() {
  return fromNullable(timeZone).or(DEFAULT_TIMEZONE);
 }

 public static Settings defaultSettings() {
  URL resource = null;
  try {
   resource = getResource(DEFAULT_FILE);
   return fromFile(resource.toURI().toURL());
  } catch (Exception e) {
   throw new RuntimeException("Cannot find default config in " + resource, e);
  }
 }

 public void setRemoveIssueFromMessage(boolean removeIssueFromMessage) {
  this.removeIssueFromMessage = removeIssueFromMessage;
 }

 public Boolean removeIssueFromMessage() {
  return fromNullable(removeIssueFromMessage).or(DEFAULT_REMOVE_ISSUE);
 }
}
