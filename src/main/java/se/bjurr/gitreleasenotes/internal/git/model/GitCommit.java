package se.bjurr.gitreleasenotes.internal.git.model;

import static com.google.common.base.MoreObjects.toStringHelper;

import java.util.Date;

public class GitCommit {
 private final String authorName;
 private final String authorEmailAddress;
 private final Date commitTime;
 private final String message;
 private final String hash;

 public GitCommit(String authorName, String authorEmailAddress, Date commitTime, String message, String hash) {
  this.authorEmailAddress = authorEmailAddress;
  this.authorName = authorName;
  this.commitTime = commitTime;
  this.message = message;
  this.hash = hash;
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

 @Override
 public String toString() {
  return toStringHelper(this)//
    .add("authorName", authorName)//
    .add("authorEmailAddress", authorEmailAddress)//
    .add("commitTime", commitTime)//
    .add("message", message)//
    .toString();
 }
}
