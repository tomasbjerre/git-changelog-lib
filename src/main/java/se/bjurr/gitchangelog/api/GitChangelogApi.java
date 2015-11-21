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
import java.util.List;

import org.eclipse.jgit.lib.ObjectId;

import se.bjurr.gitchangelog.api.model.Changelog;
import se.bjurr.gitchangelog.internal.git.GitRepo;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;
import se.bjurr.gitchangelog.internal.issues.IssueParser;
import se.bjurr.gitchangelog.internal.mediawiki.MediaWikiClient;
import se.bjurr.gitchangelog.internal.model.ParsedIssue;
import se.bjurr.gitchangelog.internal.model.Transformer;
import se.bjurr.gitchangelog.internal.settings.CustomIssue;
import se.bjurr.gitchangelog.internal.settings.Settings;

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

 public GitChangelogApi withSettings(URL url) {
  settings = fromFile(url);
  return this;
 }

 public static GitChangelogApi gitChangelogApiBuilder() {
  return new GitChangelogApi();
 }

 public GitChangelogApi withFromRepo(String fromRepo) {
  settings.setFromRepo(fromRepo);
  return this;
 }

 public GitChangelogApi withFromRef(String fromBranch) {
  settings.setFromRef(fromBranch);
  return this;
 }

 public GitChangelogApi withToRef(String toBranch) {
  settings.setToRef(toBranch);
  return this;
 }

 public Changelog getChangelog() {
  if (fakeGitRepo == null) {
   return getChangelog(new GitRepo(new File(settings.getFromRepo())));
  } else {
   return getChangelog(fakeGitRepo);
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

 public GitChangelogApi withFromCommit(String fromCommit) {
  settings.setFromCommit(fromCommit);
  return this;
 }

 public GitChangelogApi withToCommit(String toCommit) {
  settings.setToCommit(toCommit);
  return this;
 }

 public GitChangelogApi withUntaggedName(String untaggedName) {
  settings.setUntaggedName(untaggedName);
  return this;
 }

 public GitChangelogApi withJiraIssuePattern(String jiraIssuePattern) {
  settings.setJiraIssuePattern(jiraIssuePattern);
  return this;
 }

 public GitChangelogApi withJiraServer(String jiraServer) {
  settings.setJiraServer(jiraServer);
  return this;
 }

 public GitChangelogApi withIgnoreCommitsWithMesssage(String ignoreCommitsIfMessageMatches) {
  settings.setIgnoreCommitsIfMessageMatches(ignoreCommitsIfMessageMatches);
  return this;
 }

 public GitChangelogApi withCustomIssue(String name, String pattern, String link) {
  settings.addCustomIssue(new CustomIssue(name, pattern, link));
  return this;
 }

 public GitChangelogApi withTimeZone(String timeZone) {
  settings.setTimeZone(timeZone);
  return this;
 }

 public void toFile(String filePath) {
  try {
   File file = new File(filePath);
   createParentDirs(file);
   write(render().getBytes(), file);
  } catch (IOException e) {
   propagate(e);
  }
 }

 public void toMediaWiki(String username, String password, String url, String title) {
  new MediaWikiClient(url, title, render()) //
    .withUser(username, password) //
    .createMediaWikiPage();
 }

 public GitChangelogApi withTemplatePath(String templatePath) {
  settings.setTemplatePath(templatePath);
  return this;
 }

 public GitChangelogApi withDateFormat(String dateFormat) {
  settings.setDateFormat(dateFormat);
  return this;
 }

 public GitChangelogApi withNoIssueName(String noIssueName) {
  settings.setNoIssueName(noIssueName);
  return this;
 }

 public GitChangelogApi withReadableTagName(String readableTagName) {
  settings.setReadableTagName(readableTagName);
  return this;
 }

 public GitChangelogApi withRemoveIssueFromMessageArgument(boolean removeIssueFromMessage) {
  settings.setRemoveIssueFromMessage(removeIssueFromMessage);
  return this;
 }

 public GitChangelogApi withTemplateContent(String templateContent) {
  this.templateContent = templateContent;
  return this;
 }

 public String render() {
  try {
   MustacheFactory mf = new DefaultMustacheFactory();
   String templateContent = checkNotNull(getTemplateContent(), "No template!");
   StringReader reader = new StringReader(templateContent);
   Mustache mustache = mf.compile(reader, settings.getTemplatePath());
   StringWriter writer = new StringWriter();
   mustache.execute(writer, this.getChangelog()).flush();
   return writer.toString();
  } catch (IOException e) {
   throw propagate(e);
  }
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
