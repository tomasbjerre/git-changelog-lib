package se.bjurr.gitchangelog.api.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.nullToEmpty;

import java.util.List;

import se.bjurr.gitchangelog.api.model.interfaces.IAuthors;
import se.bjurr.gitchangelog.api.model.interfaces.ICommits;

public class Issue implements ICommits, IAuthors {
 private final String name;
 private final String issue;
 private final String link;
 private final List<Commit> commits;
 private final List<Author> authors;

 public Issue(List<Commit> commits, List<Author> authors, String name, String issue, String link) {
  checkState(!commits.isEmpty(), "commits");
  this.commits = commits;
  this.authors = checkNotNull(authors, "authors");
  this.name = checkNotNull(name, "name");
  this.issue = issue;
  this.link = nullToEmpty(link);
 }

 public String getIssue() {
  return issue;
 }

 public String getLink() {
  return link;
 }

 public String getName() {
  return name;
 }

 @Override
 public List<Author> getAuthors() {
  return authors;
 }

 @Override
 public List<Commit> getCommits() {
  return commits;
 }
}
