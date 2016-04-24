package se.bjurr.gitchangelog.internal.settings;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.Resources.getResource;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_DATEFORMAT;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_FILE;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_GITHUB_ISSUE_PATTERN;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_IGNORE_COMMITS_REGEXP;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_JIRA_ISSUE_PATTEN;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_NO_ISSUE_NAME;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_READABLE_TAG_NAME;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_REMOVE_ISSUE;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_TIMEZONE;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_UNTAGGED_NAME;

import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.bjurr.gitchangelog.api.model.Changelog;
import se.bjurr.gitchangelog.api.model.Issue;

import com.google.common.base.Optional;
import com.google.common.io.Resources;
import com.google.gson.Gson;

public class Settings implements Serializable {
 private static final long serialVersionUID = 4565886594381385244L;

 private static Gson gson = new Gson();

 /**
  * Folder where repo lives.
  */
 private String fromRepo;
 /**
  * Include all commits from here. Any tag or branch name.
  */
 private String fromRef;
 /**
  * Include all commits to this reference. Any tag or branch name. There is a
  * constant for master here: reference{GitChangelogApiConstants#REF_MASTER}.
  */
 private String toRef;
 /**
  * Include all commits from here. Any commit hash. There is a constant pointing
  * at the first commit here: reference{GitChangelogApiConstants#ZERO_COMMIT}.
  */
 private String fromCommit;
 /**
  * Include all commits to here. Any commit hash.
  */
 private String toCommit;
 /**
  * A regular expression that is evaluated on each tag. If it matches, the tag
  * will be filtered out and not included in the changelog.
  */
 private String ignoreTagsIfNameMatches;
 /**
  * A regular expression that is evaluated on the commit message of each commit.
  * If it matches, the commit will be filtered out and not included in the
  * changelog.<br>
  * <br>
  * To ignore tags creted by Maven and Gradle release plugins, perhaps you want
  * this: <br>
  * <code>
  * ^\[maven-release-plugin\].*|^\[Gradle Release Plugin\].*|^Merge.*
  * </code><br>
  * <br>
  * Remember to escape it, if added to the json-file it would look like this:<br>
  * <code>
  * ^\\[maven-release-plugin\\].*|^\\[Gradle Release Plugin\\].*|^Merge.*
  * </code>
  */
 private String ignoreCommitsIfMessageMatches;
 /**
  * Some commits may not be included in any tag. Commits that not released yet
  * may not be tagged. This is a "virtual tag", added to
  * {@link Changelog#getTags()}, that includes those commits. A fitting value
  * may be "Next release".
  */
 private String untaggedName;
 /**
  * Path of template-file to use. It is a Mustache (https://mustache.github.io/)
  * template. Supplied with the context of {@link Changelog}.
  */
 private String templatePath;
 /**
  * Your tags may look something like
  * <code>git-changelog-maven-plugin-1.6</code>. But in the changelog you just
  * want <code>1.6</code>. With this regular expression, the numbering can be
  * extracted from the tag name.<br>
  * <code>/([^/]+?)$</code>
  */
 private String readableTagName;
 /**
  * Format of dates, see {@link SimpleDateFormat}.
  */
 private String dateFormat;
 /**
  * This is a "virtual issue" that is added to {@link Changelog#getIssues()}. It
  * contains all commits that has no issue in the commit comment. This could be
  * used as a "wall of shame" listing commiters that did not tag there commits
  * with an issue.
  */
 private String noIssueName;
 /**
  * When date of commits are translated to a string, this timezone is used.<br>
  * <code>UTC</code>
  */
 private String timeZone;
 /**
  * If true, the changelog will not contain the issue in the commit comment. If
  * your changelog is grouped by issues, you may want this to be true. If not
  * grouped by issue, perhaps false.
  */
 private boolean removeIssueFromMessage;
 /**
  * URL pointing at your JIRA server. When configured, the
  * {@link Issue#getTitle()} will be populated with title from JIRA.<br>
  * <code>https://jiraserver/jira</code>
  */
 private String jiraServer;
 /**
  * Pattern to recognize JIRA:s. <code>\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\b</code><br>
  * <br>
  * Or escaped if added to json-file:<br>
  * <code>\\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\\b</code>
  */
 private String jiraIssuePattern;
 /**
  * Authenticate to JIRA.
  */
 private String jiraUsername;
 /**
  * Authenticate to JIRA.
  */
 private String jiraPassword;
 /**
  * URL pointing at GitHub API. When configured, the {@link Issue#getTitle()}
  * will be populated with title from GitHub.<br>
  * <code>https://api.github.com/repos/tomasbjerre/git-changelog-lib</code>
  */
 private String gitHubApi;
 /**
  * GitHub authentication token. Configure to avoid low rate limits imposed by
  * GitHub in case you have a lot of issues and/or pull requests.<br>
  * <code>https://api.github.com/repos/tomasbjerre/git-changelog-lib</code>
  */
 private String gitHubToken;
 /**
  * Pattern to recognize GitHub:s. <code>#([0-9]+)</code>
  */
 private String gitHubIssuePattern;
 /**
  * Custom issues are added to support any kind of issue management, perhaps
  * something that is internal to your project. See {@link SettingsIssue}.
  */
 private List<SettingsIssue> customIssues;
 /**
  * Extended variables is simply a key-value mapping of variables that are made
  * available in the template. Is used, for example, by the Bitbucket plugin to
  * supply some internal variables to the changelog context.
  */
 private Map<String, Object> extendedVariables;

 private boolean ignoreCommitsWithoutIssue;

 public Settings() {
 }

 public void setCustomIssues(List<SettingsIssue> customIssues) {
  this.customIssues = customIssues;
 }

 public void setFromRef(String fromRef) {
  this.fromRef = fromRef;
 }

 public void setToRef(String toRef) {
  this.toRef = toRef;
 }

 public Optional<String> getFromRef() {
  return fromNullable(emptyToNull(fromRef));
 }

 public Optional<String> getToRef() {
  return fromNullable(emptyToNull(toRef));
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

 public void setIgnoreTagsIfNameMatches(String ignoreTagsIfNameMatches) {
  this.ignoreTagsIfNameMatches = ignoreTagsIfNameMatches;
 }

 public void setJiraIssuePattern(String jiraIssuePattern) {
  this.jiraIssuePattern = jiraIssuePattern;
 }

 public void setJiraServer(String jiraServer) {
  this.jiraServer = jiraServer;
 }

 public void addCustomIssue(SettingsIssue customIssue) {
  if (customIssues == null) {
   customIssues = newArrayList();
  }
  customIssues.add(customIssue);
 }

 public List<SettingsIssue> getCustomIssues() {
  return firstNonNull(customIssues, new ArrayList<SettingsIssue>());
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
  return fromNullable(emptyToNull(fromCommit));
 }

 public Optional<String> getToCommit() {
  return fromNullable(emptyToNull(toCommit));
 }

 public String getUntaggedName() {
  return fromNullable(untaggedName).or(DEFAULT_UNTAGGED_NAME);
 }

 public Optional<String> getIgnoreTagsIfNameMatches() {
  return fromNullable(ignoreTagsIfNameMatches);
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

 public Optional<String> getGitHubApi() {
  return fromNullable(gitHubApi);
 }

 public Optional<String> getGitHubToken() {
  return fromNullable(gitHubToken);
 }

 public void setGitHubApi(String gitHubApi) {
  this.gitHubApi = gitHubApi;
 }

 public void setGitHubToken(String gitHubToken) {
  this.gitHubToken = gitHubToken;
 }

 public void setGitHubIssuePattern(String gitHubIssuePattern) {
  this.gitHubIssuePattern = gitHubIssuePattern;
 }

 public String getGitHubIssuePattern() {
  return fromNullable(gitHubIssuePattern).or(DEFAULT_GITHUB_ISSUE_PATTERN);
 }

 public Optional<String> getJiraUsername() {
  return fromNullable(jiraUsername);
 }

 public void setJiraPassword(String jiraPassword) {
  this.jiraPassword = jiraPassword;
 }

 public void setJiraUsername(String jiraUsername) {
  this.jiraUsername = jiraUsername;
 }

 public Optional<String> getJiraPassword() {
  return fromNullable(jiraPassword);
 }

 public void setExtendedVariables(Map<String, Object> extendedVariables) {
  this.extendedVariables = extendedVariables;
 }

 public Map<String, Object> getExtendedVariables() {
  return extendedVariables;
 }

 public void setIgnoreCommitsWithoutIssue(boolean ignoreCommitsWithoutIssue) {
  this.ignoreCommitsWithoutIssue = ignoreCommitsWithoutIssue;
 }

 public boolean ignoreCommitsWithoutIssue() {
  return ignoreCommitsWithoutIssue;
 }
}
