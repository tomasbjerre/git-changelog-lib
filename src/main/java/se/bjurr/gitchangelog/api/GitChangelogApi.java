package se.bjurr.gitchangelog.api;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.Files.createParentDirs;
import static com.google.common.io.Resources.getResource;
import static se.bjurr.gitchangelog.internal.settings.Settings.fromFile;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.eclipse.jgit.lib.ObjectId;

import se.bjurr.gitchangelog.api.model.Changelog;
import se.bjurr.gitchangelog.internal.git.GitRepo;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;
import se.bjurr.gitchangelog.internal.issues.IssueParser;
import se.bjurr.gitchangelog.internal.model.ParsedIssue;
import se.bjurr.gitchangelog.internal.model.Transformer;
import se.bjurr.gitchangelog.internal.settings.CustomIssue;
import se.bjurr.gitchangelog.internal.settings.Settings;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.io.Files;

public class GitChangelogApi {
 public static final String ZERO_COMMIT = "0000000000000000000000000000000000000000";

 private Settings settings;

 private GitChangelogApi() {
  URL resource = null;
  try {
   resource = getResource("git-changelog-settings.json");
   settings = fromFile(resource.toURI());
  } catch (URISyntaxException e) {
   throw new RuntimeException("Cannot find default config in " + resource, e);
  }
 }

 public GitChangelogApi withSettings(String filePath) {
  settings = fromFile(new File(filePath).toURI());
  return this;
 }

 public GitChangelogApi fromRepo(String fromRepo) {
  settings.setFromRepo(fromRepo);
  return this;
 }

 public GitChangelogApi fromRef(String fromBranch) {
  settings.setFromRef(fromBranch);
  return this;
 }

 public GitChangelogApi toRef(String toBranch) {
  settings.setToRef(toBranch);
  return this;
 }

 public Changelog getChangelog() {
  GitRepo gitRepo = new GitRepo(new File(settings.getFromRepo()));
  ObjectId fromId = getId(gitRepo, settings.getFromRef(), settings.getFromCommit());
  ObjectId toId = getId(gitRepo, settings.getToRef(), settings.getToCommit());
  List<GitCommit> diff = gitRepo.getDiff(fromId, toId);
  List<GitTag> tags = gitRepo.getTags();
  List<ParsedIssue> issues = new IssueParser(settings, diff).parseForIssues();
  Transformer transformer = new Transformer(settings);
  return new Changelog(//
    transformer.toCommits(diff), //
    transformer.toTags(diff, tags, issues), //
    transformer.toAuthors(diff), //
    transformer.toIssues(diff, issues));
 }

 private ObjectId getId(GitRepo gitRepo, String ref, String commit) {
  if (!isNullOrEmpty(ref)) {
   return gitRepo.getRef(ref);
  }
  if (!isNullOrEmpty(commit)) {
   return gitRepo.getCommit(commit);
  }
  throw new RuntimeException("Reference or commit must be defined!");
 }

 public static GitChangelogApi gitChangelogApiBuilder() {
  return new GitChangelogApi();
 }

 public GitChangelogApi fromCommit(String fromCommit) {
  settings.setFromCommit(fromCommit);
  return this;
 }

 public GitChangelogApi toCommit(String toCommit) {
  settings.setToCommit(toCommit);
  return this;
 }

 public GitChangelogApi untaggedName(String untaggedName) {
  settings.setUntaggedName(untaggedName);
  return this;
 }

 public GitChangelogApi jiraIssuePattern(String jiraIssuePattern) {
  settings.setJiraIssuePattern(jiraIssuePattern);
  return this;
 }

 public GitChangelogApi jiraServer(String jiraServer) {
  settings.setJiraServer(jiraServer);
  return this;
 }

 public GitChangelogApi ignoreCommitsWithMesssage(String ignoreCommitsIfMessageMatches) {
  settings.setIgnoreCommitsIfMessageMatches(ignoreCommitsIfMessageMatches);
  return this;
 }

 public GitChangelogApi githubIssuePattern(String githubIssuePattern) {
  settings.setGithubIssuePattern(githubIssuePattern);
  return this;
 }

 public GitChangelogApi githubServer(String githubServer) {
  settings.setGithubServer(githubServer);
  return this;
 }

 public GitChangelogApi customIssues(String name, String pattern, String link) {
  settings.setCustomIssues(newArrayList(new CustomIssue(name, pattern, link)));
  return this;
 }

 public void toStdout() {
  String rendered = render();
  System.out.println(rendered);
 }

 private String render() {
  MustacheFactory mf = new DefaultMustacheFactory();
  Mustache mustache = mf.compile("git-changelog-template.mustache");
  StringWriter writer = new StringWriter();
  try {
   mustache.execute(writer, this.getChangelog()).flush();
  } catch (IOException e) {
   propagate(e);
  }
  return writer.toString();
 }

 public void toFile(String filePath) {
  try {
   File file = new File(filePath);
   createParentDirs(file);
   Files.write(render().getBytes(), file);
  } catch (IOException e) {
   propagate(e);
  }
 }

 public GitChangelogApi templatePath(String templatePath) {
  settings.setTemplatePath(templatePath);
  return this;
 }
}
