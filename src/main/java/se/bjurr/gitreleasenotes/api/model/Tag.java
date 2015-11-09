package se.bjurr.gitreleasenotes.api.model;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

public class Tag {
 private final List<Commit> commits;
 private final List<Author> authors;
 private final List<Issue> issues;
 private final String name;

 public Tag(String name, List<Commit> commits, List<Author> authors, List<Issue> issues) {
  checkArgument(!commits.isEmpty(), "commits is empty!");
  checkArgument(!authors.isEmpty(), "authors is empty!");
  checkArgument(!issues.isEmpty(), "issues is empty!");
  this.commits = commits;
  this.authors = authors;
  this.issues = issues;
  this.name = name;
 }

 public List<Issue> getIssues() {
  return issues;
 }

 public List<Author> getAuthors() {
  return authors;
 }

 public String getName() {
  return name;
 }

 public Commit getCommit() {
  return commits.get(0);
 }

 public List<Commit> getCommits() {
  return commits;
 }
}
