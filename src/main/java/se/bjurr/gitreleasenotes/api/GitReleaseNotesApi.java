package se.bjurr.gitreleasenotes.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.Resources.getResource;
import static se.bjurr.gitreleasenotes.internal.model.Transformer.toAuthors;
import static se.bjurr.gitreleasenotes.internal.model.Transformer.toCommits;
import static se.bjurr.gitreleasenotes.internal.model.Transformer.toIssues;
import static se.bjurr.gitreleasenotes.internal.model.Transformer.toTags;
import static se.bjurr.gitreleasenotes.internal.settings.Settings.fromFile;

import java.io.File;
import java.util.List;

import se.bjurr.gitreleasenotes.api.model.ReleaseNotes;
import se.bjurr.gitreleasenotes.internal.git.GitRepo;
import se.bjurr.gitreleasenotes.internal.git.model.GitCommit;
import se.bjurr.gitreleasenotes.internal.git.model.GitTags;
import se.bjurr.gitreleasenotes.internal.issues.IssueParser;
import se.bjurr.gitreleasenotes.internal.model.ParsedIssue;
import se.bjurr.gitreleasenotes.internal.settings.Settings;

public class GitReleaseNotesApi {
 private File fromRepo;
 private String fromBranch;
 private String toBranch;
 private Settings settings;

 private GitReleaseNotesApi() {
  withSettings(new File(getResource("git-release-notes-settings.json").getFile()));
 }

 private void withSettings(File file) {
  settings = fromFile(file);
 }

 public GitReleaseNotesApi fromRepo(File fromRepo) {
  this.fromRepo = fromRepo;
  return this;
 }

 public GitReleaseNotesApi fromBranch(String fromBranch) {
  this.fromBranch = fromBranch;
  return this;
 }

 public GitReleaseNotesApi toBranch(String toBranch) {
  this.toBranch = toBranch;
  return this;
 }

 public ReleaseNotes getReleaseNotes() {
  checkNotNull(fromRepo, "fromRepo");
  checkNotNull(fromBranch, "fromBranch");
  checkNotNull(toBranch, "toBranch");
  GitRepo gitRepo = new GitRepo(fromRepo);
  List<GitCommit> diff = gitRepo.getDiff(fromBranch, toBranch);
  GitTags tags = gitRepo.getTags();
  List<ParsedIssue> issues = new IssueParser(settings, diff).parseForIssues();
  return new ReleaseNotes(//
    toCommits(diff), //
    toTags(diff, tags, issues), //
    toAuthors(diff), //
    toIssues(diff, issues));
 }

 public static GitReleaseNotesApi gitReleaseNotesApiBuilder() {
  return new GitReleaseNotesApi();
 }
}
