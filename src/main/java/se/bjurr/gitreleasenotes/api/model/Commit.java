package se.bjurr.gitreleasenotes.api.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

public class Commit {
 private final String authorName;
 private final String authorEmailAddress;
 private final Date commitTime;
 private final String message;
 private final String hash;

 public Commit(String authorName, String authorEmailAddress, Date commitTime, String message, String hash) {
  this.authorName = checkNotNull(authorName, "authorName");
  this.authorEmailAddress = checkNotNull(authorEmailAddress, "authorEmailAddress");
  this.message = checkNotNull(message, "message");
  this.commitTime = checkNotNull(commitTime, "commitTime");
  this.hash = checkNotNull(hash, "hash");
 }

 public String getHash() {
  return hash;
 }

 public String getAuthorEmailAddress() {
  return authorEmailAddress;
 }

 public String getAuthorName() {
  return authorName;
 }

 public Date getCommitTime() {
  return commitTime;
 }

 public String getMessage() {
  return message;
 }
}
