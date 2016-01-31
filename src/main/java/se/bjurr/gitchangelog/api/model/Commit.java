package se.bjurr.gitchangelog.api.model;

import static com.google.common.base.Preconditions.checkNotNull;

public class Commit {
 private final String authorName;
 private final String authorEmailAddress;
 private final String commitTime;
 private final String message;
 private final String hash;

 public Commit(String authorName, String authorEmailAddress, String commitTime, String message, String hash) {
  this.authorName = checkNotNull(authorName, "authorName");
  this.authorEmailAddress = checkNotNull(authorEmailAddress, "authorEmailAddress");
  this.message = checkNotNull(message, "message").trim();
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

 public String getCommitTime() {
  return commitTime;
 }

 public String getMessage() {
  return message;
 }

 @Override
 public String toString() {
  return "hash: " + hash + " message: " + message;
 }
}
