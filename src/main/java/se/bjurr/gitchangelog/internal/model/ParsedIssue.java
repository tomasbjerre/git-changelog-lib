package se.bjurr.gitchangelog.internal.model;

import static com.google.common.base.Preconditions.checkNotNull;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;

public class ParsedIssue {
 private final GitCommit gitCommit;
 private final String name;
 private final String link;

 public ParsedIssue(GitCommit gitCommit, String link, String name) {
  this.gitCommit = checkNotNull(gitCommit, "gitCommit");
  this.name = checkNotNull(name, "name");
  this.link = link;
 }

 public GitCommit getGitCommit() {
  return gitCommit;
 }

 public String getLink() {
  return link;
 }

 public String getName() {
  return name;
 }

}
