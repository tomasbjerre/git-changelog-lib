package se.bjurr.gitreleasenotes.internal.model;

import static com.google.common.base.Preconditions.checkNotNull;
import se.bjurr.gitreleasenotes.internal.git.model.GitCommit;

public class ParsedIssue {
 private final GitCommit gitCommit;
 private final ParsedIssueType parsedIssueType;
 private final String name;
 private final String description;

 public ParsedIssue(GitCommit gitCommit, ParsedIssueType parsedIssueType, String name, String description) {
  this.gitCommit = checkNotNull(gitCommit, "gitCommit");
  this.parsedIssueType = checkNotNull(parsedIssueType, "parsedIssueType");
  this.name = checkNotNull(name, "name");
  this.description = description;
 }

 public GitCommit getGitCommit() {
  return gitCommit;
 }

 public String getDescription() {
  return description;
 }

 public String getName() {
  return name;
 }

 public ParsedIssueType getParsedIssueType() {
  return parsedIssueType;
 }
}
