package se.bjurr.gitchangelog.api.model;

import java.io.Serializable;
import java.util.List;

import se.bjurr.gitchangelog.api.model.interfaces.IAuthors;
import se.bjurr.gitchangelog.api.model.interfaces.ICommits;
import se.bjurr.gitchangelog.api.model.interfaces.IIssues;

public class Tag implements ICommits, IAuthors, IIssues, Serializable {
 private static final long serialVersionUID = 2140208294219785889L;
 private final List<Commit> commits;
 private final List<Author> authors;
 private final List<Issue> issues;
 private final List<IssueType> issueTypes;
 private final String name;

 public Tag(String name, List<Commit> commits, List<Author> authors, List<Issue> issues, List<IssueType> issueTypes) {
  this.commits = commits;
  this.authors = authors;
  this.issues = issues;
  this.name = name;
  this.issueTypes = issueTypes;
 }

 @Override
 public List<Issue> getIssues() {
  return issues;
 }

 @Override
 public List<Author> getAuthors() {
  return authors;
 }

 public String getName() {
  return name;
 }

 public Commit getCommit() {
  return commits.get(0);
 }

 @Override
 public List<Commit> getCommits() {
  return commits;
 }

 @Override
 public String toString() {
  return "name: " + name;
 }

 public List<IssueType> getIssueTypes() {
  return issueTypes;
 }
}
