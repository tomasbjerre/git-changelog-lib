package se.bjurr.gitchangelog.api.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;

import java.util.List;

import se.bjurr.gitchangelog.api.model.interfaces.IAuthors;
import se.bjurr.gitchangelog.api.model.interfaces.ICommits;

public class Issue implements ICommits, IAuthors {
 private final List<Commit> commits;
 private final List<Author> authors;
 private final String name;
 private final boolean hasIssue;
 private final String title;
 private final boolean hasTitle;
 private final String issue;
 private final boolean hasLink;
 private final String link;

 public Issue(List<Commit> commits, List<Author> authors, String name, String title, String issue, String link) {
  checkState(!commits.isEmpty(), "commits");
  this.commits = commits;
  this.authors = checkNotNull(authors, "authors");
  this.name = checkNotNull(name, "name");
  this.title = nullToEmpty(title);
  this.hasTitle = !isNullOrEmpty(title);
  this.issue = nullToEmpty(issue);
  this.hasIssue = !isNullOrEmpty(issue);
  this.link = nullToEmpty(link);
  this.hasLink = !isNullOrEmpty(link);
 }

 public String getTitle() {
  return title;
 }

 public boolean hasTitle() {
  return hasTitle;
 }

 public boolean hasIssue() {
  return hasIssue;
 }

 public boolean hasLink() {
  return hasLink;
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
