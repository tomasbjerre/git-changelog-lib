package se.bjurr.gitchangelog.internal.settings;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.Resources.getResource;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_DATEFORMAT;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_FILE;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_GITHUB_ISSUE_PATTERN;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_GITLAB_ISSUE_PATTERN;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_IGNORE_COMMITS_REGEXP;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_JIRA_ISSUE_PATTEN;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_NO_ISSUE_NAME;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_READABLE_TAG_NAME;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_REMOVE_ISSUE;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_TIMEZONE;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_UNTAGGED_NAME;

import com.google.common.base.Optional;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import se.bjurr.gitchangelog.api.model.Changelog;
import se.bjurr.gitchangelog.api.model.Issue;

public class Settings implements Serializable {
  private static final long serialVersionUID = 4565886594381385244L;

  private static Gson gson = new Gson();

  /** Folder where repo lives. */
  private String fromRepo;
  /** Include all commits from here. Any tag or branch name. */
  private String fromRef;
  /**
   * Include all commits to this reference. Any tag or branch name. There is a constant for master
   * here: reference{GitChangelogApiConstants#REF_MASTER}.
   */
  private String toRef;
  /**
   * Include all commits from here. Any commit hash. There is a constant pointing at the first
   * commit here: reference{GitChangelogApiConstants#ZERO_COMMIT}.
   */
  private String fromCommit;
  /** Include all commits to here. Any commit hash. */
  private String toCommit;
  /**
   * A regular expression that is evaluated on each tag. If it matches, the tag will be filtered out
   * and not included in the changelog.
   */
  private String ignoreTagsIfNameMatches;
  /**
   * A regular expression that is evaluated on the commit message of each commit. If it matches, the
   * commit will be filtered out and not included in the changelog.<br>
   * <br>
   * To ignore tags creted by Maven and Gradle release plugins, perhaps you want this: <br>
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
   * A date that is evaluated on the commit time of each commit. If the commit is older than the
   * point in time given, then it will be filtered out and not included in the changelog. <br>
   * See {@link SimpleDateFormat}.
   */
  private Date ignoreCommitsIfOlderThan;
  /**
   * Some commits may not be included in any tag. Commits that not released yet may not be tagged.
   * This is a "virtual tag", added to {@link Changelog#getTags()}, that includes those commits. A
   * fitting value may be "Next release".
   */
  private String untaggedName;
  /**
   * Path of template-file to use. It is a Mustache (https://mustache.github.io/) template. Supplied
   * with the context of {@link Changelog}.
   */
  private String templatePath;
  /**
   * Your tags may look something like <code>git-changelog-maven-plugin-1.6</code>. But in the
   * changelog you just want <code>1.6</code>. With this regular expression, the numbering can be
   * extracted from the tag name.<br>
   * <code>/([^-]+?)$</code>
   */
  private String readableTagName;
  /** Format of dates, see {@link SimpleDateFormat}. */
  private String dateFormat;
  /**
   * This is a "virtual issue" that is added to {@link Changelog#getIssues()}. It contains all
   * commits that has no issue in the commit comment. This could be used as a "wall of shame"
   * listing commiters that did not tag there commits with an issue.
   */
  private String noIssueName;
  /**
   * When date of commits are translated to a string, this timezone is used.<br>
   * <code>UTC</code>
   */
  private String timeZone;
  /**
   * If true, the changelog will not contain the issue in the commit comment. If your changelog is
   * grouped by issues, you may want this to be true. If not grouped by issue, perhaps false.
   */
  private boolean removeIssueFromMessage;
  /**
   * URL pointing at your JIRA server. When configured, the {@link Issue#getTitle()} will be
   * populated with title from JIRA.<br>
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
  /** Authenticate to JIRA. */
  private String jiraUsername;
  /** Authenticate to JIRA. */
  private String jiraPassword;
  /** Authenticate to JIRA. */
  private String jiraToken;
  /**
   * URL pointing at GitHub API. When configured, the {@link Issue#getTitle()} will be populated
   * with title from GitHub.<br>
   * <code>https://api.github.com/repos/tomasbjerre/git-changelog-lib</code>
   */
  private String gitHubApi;
  /**
   * GitHub authentication token. Configure to avoid low rate limits imposed by GitHub in case you
   * have a lot of issues and/or pull requests.<br>
   * <code>https://api.github.com/repos/tomasbjerre/git-changelog-lib</code>
   */
  private String gitHubToken;
  /** Pattern to recognize GitHub:s. <code>#([0-9]+)</code> */
  private String gitHubIssuePattern;
  /**
   * Custom issues are added to support any kind of issue management, perhaps something that is
   * internal to your project. See {@link SettingsIssue}.
   */
  private List<SettingsIssue> customIssues;
  /**
   * Extended variables is simply a key-value mapping of variables that are made available in the
   * template. Is used, for example, by the Bitbucket plugin to supply some internal variables to
   * the changelog context.
   */
  private Map<String, Object> extendedVariables = new HashMap<>();

  /**
   * Extended headers is simply a key-value mapping of headers that will be passed to REST request.
   * Is used, for example, to bypass 2-factor authentication.
   */
  private Map<String, String> extendedRestHeaders;

  /** Commits that don't have any issue in their commit message will not be included. */
  private boolean ignoreCommitsWithoutIssue;

  /** GitLab server URL, like https://gitlab.com/. */
  private String gitLabServer;

  private String gitLabToken;

  /** Pattern to recognize GitLab:s. <code>#([0-9]+)</code> */
  private String gitLabIssuePattern;

  /**
   * Like: tomas.bjerre85/violations-test for this repo:
   * https://gitlab.com/tomas.bjerre85/violations-test
   */
  private String gitLabProjectName;

  /** Regular expression to use when determining next semantic version based on commits. */
  private String semanticMajorPattern;

  /** Regular expression to use when determining next semantic version based on commits. */
  private String semanticMinorPattern;

  public String getSubDirFilter() {
    return fromNullable(this.subDirFilter).or("");
  }

  public void setPathFilter(final String subDirFilter) {
    this.subDirFilter = subDirFilter;
  }

  private String subDirFilter;

  public Settings() {}

  public void setCustomIssues(final List<SettingsIssue> customIssues) {
    this.customIssues = customIssues;
  }

  public void setFromRef(final String fromRef) {
    if (fromRef == null || fromRef.trim().isEmpty()) {
      this.fromRef = null;
    } else {
      this.fromRef = fromRef.trim();
    }
  }

  public void setToRef(final String toRef) {
    if (toRef == null || toRef.trim().isEmpty()) {
      this.toRef = null;
    } else {
      this.toRef = toRef.trim();
    }
  }

  public Optional<String> getFromRef() {
    return fromNullable(this.fromRef);
  }

  public Optional<String> getToRef() {
    return fromNullable(this.toRef);
  }

  public void setFromRepo(final String fromRepo) {
    this.fromRepo = fromRepo;
  }

  public String getFromRepo() {
    return fromNullable(this.fromRepo).or(".");
  }

  public void setIgnoreCommitsIfMessageMatches(final String ignoreCommitsIfMessageMatches) {
    this.ignoreCommitsIfMessageMatches = ignoreCommitsIfMessageMatches;
  }

  public void setIgnoreTagsIfNameMatches(final String ignoreTagsIfNameMatches) {
    this.ignoreTagsIfNameMatches = ignoreTagsIfNameMatches;
  }

  public void setIgnoreCommitsIfOlderThan(final Date ignoreCommitsIfOlderThan) {
    if (ignoreCommitsIfOlderThan != null) {
      this.ignoreCommitsIfOlderThan = new Date(ignoreCommitsIfOlderThan.getTime());
    } else {
      this.ignoreCommitsIfOlderThan = null;
    }
  }

  public void setJiraIssuePattern(final String jiraIssuePattern) {
    this.jiraIssuePattern = jiraIssuePattern;
  }

  public void setJiraServer(final String jiraServer) {
    this.jiraServer = jiraServer;
  }

  public void addCustomIssue(final SettingsIssue customIssue) {
    if (this.customIssues == null) {
      this.customIssues = newArrayList();
    }
    this.customIssues.add(customIssue);
  }

  public List<SettingsIssue> getCustomIssues() {
    if (this.customIssues == null) {
      return new ArrayList<>();
    } else {
      return this.customIssues;
    }
  }

  public String getIgnoreCommitsIfMessageMatches() {
    return fromNullable(this.ignoreCommitsIfMessageMatches).or(DEFAULT_IGNORE_COMMITS_REGEXP);
  }

  public Optional<Date> getIgnoreCommitsIfOlderThan() {
    return fromNullable(this.ignoreCommitsIfOlderThan);
  }

  public String getJiraIssuePattern() {
    return fromNullable(this.jiraIssuePattern).or(DEFAULT_JIRA_ISSUE_PATTEN);
  }

  public Optional<String> getJiraServer() {
    return fromNullable(this.jiraServer);
  }

  public static Settings fromFile(final URL url) {
    try {
      return gson.fromJson(Resources.toString(url, UTF_8), Settings.class);
    } catch (final Exception e) {
      throw new RuntimeException("Cannot read " + url, e);
    }
  }

  public void setFromCommit(final String fromCommit) {
    this.fromCommit = fromCommit;
  }

  public void setToCommit(final String toCommit) {
    this.toCommit = toCommit;
  }

  public Optional<String> getFromCommit() {
    return fromNullable(emptyToNull(this.fromCommit));
  }

  public Optional<String> getToCommit() {
    return fromNullable(emptyToNull(this.toCommit));
  }

  public String getUntaggedName() {
    return fromNullable(this.untaggedName).or(DEFAULT_UNTAGGED_NAME);
  }

  public Optional<String> getIgnoreTagsIfNameMatches() {
    return fromNullable(this.ignoreTagsIfNameMatches);
  }

  public void setUntaggedName(final String untaggedName) {
    this.untaggedName = untaggedName;
  }

  public String getTemplatePath() {
    return fromNullable(this.templatePath).or("changelog.mustache");
  }

  public void setTemplatePath(final String templatePath) {
    this.templatePath = templatePath;
  }

  public String getReadableTagName() {
    return fromNullable(this.readableTagName).or(DEFAULT_READABLE_TAG_NAME);
  }

  public String getDateFormat() {
    return fromNullable(this.dateFormat).or(DEFAULT_DATEFORMAT);
  }

  public void setDateFormat(final String dateFormat) {
    this.dateFormat = dateFormat;
  }

  public void setNoIssueName(final String noIssueName) {
    this.noIssueName = noIssueName;
  }

  public void setReadableTagName(final String readableTagName) {
    this.readableTagName = readableTagName;
  }

  public String getNoIssueName() {
    return fromNullable(this.noIssueName).or(DEFAULT_NO_ISSUE_NAME);
  }

  public void setTimeZone(final String timeZone) {
    this.timeZone = timeZone;
  }

  public String getTimeZone() {
    return fromNullable(this.timeZone).or(DEFAULT_TIMEZONE);
  }

  public static Settings defaultSettings() {
    URL resource = null;
    try {
      resource = getResource(DEFAULT_FILE);
      return fromFile(resource.toURI().toURL());
    } catch (final Exception e) {
      throw new RuntimeException("Cannot find default config in " + resource, e);
    }
  }

  public void setRemoveIssueFromMessage(final boolean removeIssueFromMessage) {
    this.removeIssueFromMessage = removeIssueFromMessage;
  }

  public Boolean removeIssueFromMessage() {
    return fromNullable(this.removeIssueFromMessage).or(DEFAULT_REMOVE_ISSUE);
  }

  public Optional<String> getGitHubApi() {
    return fromNullable(this.gitHubApi);
  }

  public Optional<String> getGitHubToken() {
    return fromNullable(this.gitHubToken);
  }

  public void setGitHubApi(final String gitHubApi) {
    this.gitHubApi = gitHubApi;
  }

  public void setGitHubToken(final String gitHubToken) {
    this.gitHubToken = gitHubToken;
  }

  public void setGitHubIssuePattern(final String gitHubIssuePattern) {
    this.gitHubIssuePattern = gitHubIssuePattern;
  }

  public String getGitHubIssuePattern() {
    return fromNullable(this.gitHubIssuePattern).or(DEFAULT_GITHUB_ISSUE_PATTERN);
  }

  public Optional<String> getJiraUsername() {
    return fromNullable(this.jiraUsername);
  }

  public void setJiraPassword(final String jiraPassword) {
    this.jiraPassword = jiraPassword;
  }

  public void setJiraToken(final String jiraToken) {
    this.jiraToken = jiraToken;
  }

  public void setJiraUsername(final String jiraUsername) {
    this.jiraUsername = jiraUsername;
  }

  public Optional<String> getJiraPassword() {
    return fromNullable(this.jiraPassword);
  }

  public Optional<String> getJiraToken() {
    return fromNullable(this.jiraToken);
  }

  public void setExtendedVariables(final Map<String, Object> extendedVariables) {
    this.extendedVariables = extendedVariables;
  }

  public Map<String, Object> getExtendedVariables() {
    return this.extendedVariables;
  }

  public Map<String, String> getExtendedRestHeaders() {
    return this.extendedRestHeaders;
  }

  public void setExtendedRestHeaders(final Map<String, String> extendedRestHeaders) {
    this.extendedRestHeaders = extendedRestHeaders;
  }

  public void setIgnoreCommitsWithoutIssue(final boolean ignoreCommitsWithoutIssue) {
    this.ignoreCommitsWithoutIssue = ignoreCommitsWithoutIssue;
  }

  public boolean ignoreCommitsWithoutIssue() {
    return this.ignoreCommitsWithoutIssue;
  }

  public void setGitLabIssuePattern(final String gitLabIssuePattern) {
    this.gitLabIssuePattern = gitLabIssuePattern;
  }

  public void setGitLabProjectName(final String gitLabProjectName) {
    this.gitLabProjectName = gitLabProjectName;
  }

  public void setGitLabServer(final String gitLabServer) {
    this.gitLabServer = gitLabServer;
  }

  public void setGitLabToken(final String gitLabToken) {
    this.gitLabToken = gitLabToken;
  }

  public Optional<String> getGitLabServer() {
    return fromNullable(this.gitLabServer);
  }

  public Optional<String> getGitLabToken() {
    return fromNullable(this.gitLabToken);
  }

  public String getGitLabIssuePattern() {
    return fromNullable(this.gitLabIssuePattern).or(DEFAULT_GITLAB_ISSUE_PATTERN);
  }

  public Optional<String> getGitLabProjectName() {
    return fromNullable(this.gitLabProjectName);
  }

  public Optional<String> getSemanticMajorPattern() {
    return fromNullable(this.semanticMajorPattern);
  }

  public void setSemanticMajorPattern(final String semanticMajorPattern) {
    this.semanticMajorPattern = this.isRegexp(semanticMajorPattern, "semanticMajorPattern");
  }

  public Optional<String> getSemanticMinorPattern() {
    return fromNullable(this.semanticMinorPattern);
  }

  public void setSemanticMinorPattern(final String semanticMinorPattern) {
    this.semanticMinorPattern = this.isRegexp(semanticMinorPattern, "semanticMinorPattern");
  }

  private String isRegexp(final String pattern, final String string) {
    try {
      Pattern.compile(pattern);
    } catch (final PatternSyntaxException e) {
      throw new RuntimeException(pattern + " in " + string + " is not valid regexp.");
    }
    return pattern;
  }
}
