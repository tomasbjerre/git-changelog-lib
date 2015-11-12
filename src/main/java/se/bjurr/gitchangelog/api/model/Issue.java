package se.bjurr.gitchangelog.api.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;

import java.util.List;

public class Issue {
 private final String name;
 private final String link;
 private final List<Commit> commits;
 private final List<Author> authors;

 public Issue(List<Commit> commits, List<Author> authors, String name, String link) {
  this.commits = checkNotNull(commits, "commits");
  this.authors = checkNotNull(authors, "authors");
  this.name = checkNotNull(name, "name");
  this.link = nullToEmpty(link);
 }

 public String getLink() {
  return link;
 }

 public String getName() {
  return name;
 }

 public List<Author> getAuthors() {
  return authors;
 }

 public List<Commit> getCommits() {
  return commits;
 }
}
