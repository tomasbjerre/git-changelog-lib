package se.bjurr.gitchangelog.api.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

public class Changelog {
 private final List<Commit> commits;
 private final List<Tag> tags;
 private final List<Author> authors;
 private final List<Issue> issues;

 public Changelog(List<Commit> commits, List<Tag> tags, List<Author> authors, List<Issue> issues) {
  this.commits = checkNotNull(commits, "commits");
  this.tags = checkNotNull(tags, "tags");
  this.authors = checkNotNull(authors, "authors");
  this.issues = checkNotNull(issues, "issues");
 }

 public List<Issue> getIssues() {
  return issues;
 }

 public List<Author> getAuthors() {
  return authors;
 }

 public List<Commit> getCommits() {
  return commits;
 }

 public List<Tag> getTags() {
  return tags;
 }
}
