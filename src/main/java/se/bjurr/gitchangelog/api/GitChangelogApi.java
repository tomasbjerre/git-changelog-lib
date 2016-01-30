package se.bjurr.gitchangelog.api;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.io.Files.createParentDirs;
import static com.google.common.io.Files.write;
import static com.google.common.io.Resources.getResource;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_MASTER;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;
import static se.bjurr.gitchangelog.internal.settings.Settings.fromFile;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.ObjectId;

import se.bjurr.gitchangelog.api.model.Changelog;
import se.bjurr.gitchangelog.api.model.Issue;
import se.bjurr.gitchangelog.internal.git.GitRepo;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;
import se.bjurr.gitchangelog.internal.integrations.mediawiki.MediaWikiClient;
import se.bjurr.gitchangelog.internal.issues.IssueParser;
import se.bjurr.gitchangelog.internal.model.ParsedIssue;
import se.bjurr.gitchangelog.internal.model.Transformer;
import se.bjurr.gitchangelog.internal.settings.Settings;
import se.bjurr.gitchangelog.internal.settings.SettingsIssue;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.io.Files;
import com.google.common.io.Resources;

public class GitChangelogApi {

 private Settings settings;

 private String templateContent;

 private static GitRepo fakeGitRepo;

 @VisibleForTesting
 static void setFakeGitRepo(GitRepo fakeGitRepo) {
  GitChangelogApi.fakeGitRepo = fakeGitRepo;
 }

 public static GitChangelogApi gitChangelogApiBuilder() {
  return new GitChangelogApi();
 }

 /**
  * {@link Settings}.
  */
 public GitChangelogApi withSettings(URL url) {
  settings = fromFile(url);
  return this;
 }

 /**
  * Folder where repo lives.
  */
 public GitChangelogApi withFromRepo(String fromRepo) {
  settings.setFromRepo(fromRepo);
  return this;
 }

 /**
  * Include all commits from here. Any tag or branch name.
  */
 public GitChangelogApi withFromRef(String fromBranch) {
  settings.setFromRef(fromBranch);
  return this;
 }

 /**
  * Include all commits to this reference. Any tag or branch name. There is a
  * constant for master here: reference{GitChangelogApiConstants#REF_MASTER}.
  */
 public GitChangelogApi withToRef(String toBranch) {
  settings.setToRef(toBranch);
  return this;
 }

 /**
  * Include all commits from here. Any commit hash. There is a constant pointing
  * at the first commit here: reference{GitChangelogApiConstants#ZERO_COMMIT}.
  */
 public GitChangelogApi withFromCommit(String fromCommit) {
  settings.setFromCommit(fromCommit);
  return this;
 }

 /**
  * Include all commits to here. Any commit hash.
  */
 public GitChangelogApi withToCommit(String toCommit) {
  settings.setToCommit(toCommit);
  return this;
 }

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
 public GitChangelogApi withIgnoreCommitsWithMesssage(String ignoreCommitsIfMessageMatches) {
  settings.setIgnoreCommitsIfMessageMatches(ignoreCommitsIfMessageMatches);
  return this;
 }

 /**
  * Some commits may not be included in any tag. Commits that not released yet
  * may not be tagged. This is a "virtual tag", added to
  * {@link Changelog#getTags()}, that includes those commits. A fitting value
  * may be "Next release".
  */
 public GitChangelogApi withUntaggedName(String untaggedName) {
  settings.setUntaggedName(untaggedName);
  return this;
 }

 /**
  * Path of template-file to use. It is a Mustache (https://mustache.github.io/)
  * template. Supplied with the context of {@link Changelog}.
  */
 public GitChangelogApi withTemplatePath(String templatePath) {
  settings.setTemplatePath(templatePath);
  return this;
 }

 /**
  * Use string as template. {@link #withTemplatePath}.
  */
 public GitChangelogApi withTemplateContent(String templateContent) {
  this.templateContent = templateContent;
  return this;
 }

 /**
  * Your tags may look something like
  * <code>git-changelog-maven-plugin-1.6</code>. But in the changelog you just
  * want <code>1.6</code>. With this regular expression, the numbering can be
  * extracted from the tag name.<br>
  * <code>/([^/]+?)$</code>
  */
 public GitChangelogApi withReadableTagName(String readableTagName) {
  settings.setReadableTagName(readableTagName);
  return this;
 }

 /**
  * Format of dates, see {@link SimpleDateFormat}.
  */
 public GitChangelogApi withDateFormat(String dateFormat) {
  settings.setDateFormat(dateFormat);
  return this;
 }

 /**
  * This is a "virtual issue" that is added to {@link Changelog#getIssues()}. It
  * contains all commits that has no issue in the commit comment. This could be
  * used as a "wall of shame" listing commiters that did not tag there commits
  * with an issue.
  */
 public GitChangelogApi withNoIssueName(String noIssueName) {
  settings.setNoIssueName(noIssueName);
  return this;
 }

 /**
  * When date of commits are translated to a string, this timezone is used.<br>
  * <code>UTC</code>
  */
 public GitChangelogApi withTimeZone(String timeZone) {
  settings.setTimeZone(timeZone);
  return this;
 }

 /**
  * If true, the changelog will not contain the issue in the commit comment. If
  * your changelog is grouped by issues, you may want this to be true. If not
  * grouped by issue, perhaps false.
  */
 public GitChangelogApi withRemoveIssueFromMessageArgument(boolean removeIssueFromMessage) {
  settings.setRemoveIssueFromMessage(removeIssueFromMessage);
  return this;
 }

 /**
  * URL pointing at your JIRA server. When configured, the
  * {@link Issue#getTitle()} will be populated with title from JIRA.<br>
  * <code>https://jiraserver/jira</code>
  */
 public GitChangelogApi withJiraServer(String jiraServer) {
  settings.setJiraServer(jiraServer);
  return this;
 }

 /**
  * Pattern to recognize JIRA:s. <code>\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\b</code><br>
  * <br>
  * Or escaped if added to json-file:<br>
  * <code>\\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\\b</code>
  */
 public GitChangelogApi withJiraIssuePattern(String jiraIssuePattern) {
  settings.setJiraIssuePattern(jiraIssuePattern);
  return this;
 }

 /**
  * Authenticate to JIRA.
  */
 public GitChangelogApi withJiraUsername(String string) {
  settings.setJiraUsername(string);
  return this;
 }

 /**
  * Authenticate to JIRA.
  */
 public GitChangelogApi withJiraPassword(String string) {
  settings.setJiraPassword(string);
  return this;
 }

 /**
  * URL pointing at GitHub API. When configured, the {@link Issue#getTitle()}
  * will be populated with title from GitHub.<br>
  * <code>https://api.github.com/repos/tomasbjerre/git-changelog-lib</code>
  */
 public GitChangelogApi withGitHubApi(String gitHubApi) {
  settings.setGitHubApi(gitHubApi);
  return this;
 }

 /**
  * Pattern to recognize GitHub:s. <code>#([0-9]+)</code>
  */
 public GitChangelogApi withGitHubIssuePattern(String gitHubIssuePattern) {
  settings.setGitHubIssuePattern(gitHubIssuePattern);
  return this;
 }

 /**
  * Custom issues are added to support any kind of issue management, perhaps
  * something that is internal to your project. See {@link SettingsIssue}.
  */
 public GitChangelogApi withCustomIssue(String name, String pattern, String link) {
  settings.addCustomIssue(new SettingsIssue(name, pattern, link));
  return this;
 }

 /**
  * Extended variables is simply a key-value mapping of variables that are made
  * available in the template. Is used, for example, by the Bitbucket plugin to
  * supply some internal variables to the changelog context.
  */
 public GitChangelogApi withExtendedVariables(Map<String, Object> extendedVariables) {
  settings.setExtendedVariables(extendedVariables);
  return this;
 }

 /**
  * Write changelog to file.
  */
 public void toFile(String filePath) {
  try {
   File file = new File(filePath);
   createParentDirs(file);
   write(render().getBytes(), file);
  } catch (IOException e) {
   propagate(e);
  }
 }

 /**
  * Create MediaWiki page with changelog.
  */
 public void toMediaWiki(String username, String password, String url, String title) {
  new MediaWikiClient(url, title, render()) //
    .withUser(username, password) //
    .createMediaWikiPage();
 }

 /**
  * Get the changelog as data object.
  */
 public Changelog getChangelog() {
  if (fakeGitRepo == null) {
   return getChangelog(new GitRepo(new File(settings.getFromRepo())));
  } else {
   return getChangelog(fakeGitRepo);
  }
 }

 /**
  * Get the changelog as rendered string.
  */
 public String render() {
  try {
   MustacheFactory mf = new DefaultMustacheFactory();
   String templateContent = checkNotNull(getTemplateContent(), "No template!");
   StringReader reader = new StringReader(templateContent);
   Mustache mustache = mf.compile(reader, settings.getTemplatePath());
   StringWriter writer = new StringWriter();
   mustache.execute(writer, //
     new Object[] { this.getChangelog(), settings.getExtendedVariables() } //
     ).flush();
   return writer.toString();
  } catch (IOException e) {
   throw propagate(e);
  }
 }

 private Changelog getChangelog(GitRepo gitRepo) {
  ObjectId fromId = getId(gitRepo, settings.getFromRef(), settings.getFromCommit()) //
    .or(gitRepo.getCommit(ZERO_COMMIT));
  ObjectId toId = getId(gitRepo, settings.getToRef(), settings.getToCommit()) //
    .or(gitRepo.getRef(REF_MASTER));
  List<GitCommit> diff = gitRepo.getDiff(fromId, toId);
  List<GitTag> tags = gitRepo.getTags();
  List<ParsedIssue> issues = new IssueParser(settings, diff).parseForIssues();
  Transformer transformer = new Transformer(settings);
  return new Changelog(//
    transformer.toCommits(diff), //
    transformer.toTags(diff, tags), //
    transformer.toAuthors(diff), //
    transformer.toIssues(issues));
 }

 private String getTemplateContent() {
  if (templateContent != null) {
   return templateContent;
  }
  checkArgument(settings.getTemplatePath() != null, "You must specify a template!");
  try {
   return Resources.toString(getResource(settings.getTemplatePath()), UTF_8);
  } catch (Exception e) {
   File file = null;
   try {
    file = new File(settings.getTemplatePath());
    return Files.toString(file, UTF_8);
   } catch (IOException e2) {
    throw new RuntimeException("Cannot find on classpath (" + settings.getTemplatePath() + ") or filesystem ("
      + file.getAbsolutePath() + ").", e2);
   }
  }
 }

 private Optional<ObjectId> getId(GitRepo gitRepo, Optional<String> ref, Optional<String> commit) {
  if (ref.isPresent()) {
   return of(gitRepo.getRef(ref.get()));
  }
  if (commit.isPresent()) {
   return of(gitRepo.getCommit(commit.get()));
  }
  return absent();
 }

 private GitChangelogApi() {
  settings = new Settings();
 }

 private GitChangelogApi(Settings settings) {
  this.settings = settings;
 }
}
