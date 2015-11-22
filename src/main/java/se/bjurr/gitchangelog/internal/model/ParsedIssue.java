package se.bjurr.gitchangelog.internal.model;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.model.interfaces.IGitCommitReferer;

import com.google.common.base.Optional;

public class ParsedIssue implements IGitCommitReferer {
 private final List<GitCommit> gitCommits = newArrayList();
 private final String name;
 private final String title;
 private final String link;
 private final String issue;

 public ParsedIssue(String name, String issue, String link) {
  this.name = checkNotNull(name, "name");
  this.title = null;
  this.issue = issue;
  this.link = link;
 }

 public ParsedIssue(String name, String title, String issue, String link) {
  this.name = checkNotNull(name, "name");
  this.title = emptyToNull(title);
  this.issue = issue;
  this.link = link;
 }

 public Optional<String> getTitle() {
  return fromNullable(title);
 }

 @Override
 public GitCommit getGitCommit() {
  return checkNotNull(gitCommits.get(0), name);
 }

 public List<GitCommit> getGitCommits() {
  return gitCommits;
 }

 public String getLink() {
  return link;
 }

 @Override
 public String getName() {
  return name;
 }

 @Override
 public int hashCode() {
  return toString().hashCode();
 }

 @Override
 public String toString() {
  return name + issue;
 }

 @Override
 public boolean equals(Object obj) {
  if (obj.getClass() != ParsedIssue.class) {
   return false;
  }
  return name.equals(((ParsedIssue) obj).getName());
 }

 public void addCommit(GitCommit gitCommit) {
  this.gitCommits.add(gitCommit);
 }

 public String getIssue() {
  return issue;
 }
}
