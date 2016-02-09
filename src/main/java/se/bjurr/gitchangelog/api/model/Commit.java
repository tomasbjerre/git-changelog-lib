package se.bjurr.gitchangelog.api.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

public class Commit {
 private final String authorName;
 private final String authorEmailAddress;
 private final String commitTime;
 private final Long commitTimeLong;
 private final String message;
 private final String messageTitle;
 private final String messageBody;
 private final List<String> messageBodyItems;
 private final String hash;

 public Commit(String authorName, String authorEmailAddress, String commitTime, Long commitTimeLong, String message,
   String hash, String messageTitle, String messageBody, List<String> messageBodyItems) {
  this.authorName = checkNotNull(authorName, "authorName");
  this.authorEmailAddress = checkNotNull(authorEmailAddress, "authorEmailAddress");
  this.message = checkNotNull(message, "message").trim();
  this.commitTime = checkNotNull(commitTime, "commitTime");
  this.commitTimeLong = checkNotNull(commitTimeLong, "commitTimeLong");
  this.hash = checkNotNull(hash, "hash");
  this.messageTitle = checkNotNull(messageTitle, "messageTitle");
  this.messageBody = checkNotNull(messageBody, "messageBody");
  this.messageBodyItems = checkNotNull(messageBodyItems, "messageBodyItems");
 }

 public String getHash() {
  return hash;
 }

 public String getMessageBody() {
  return messageBody;
 }

 public List<String> getMessageBodyItems() {
  return messageBodyItems;
 }

 public String getMessageTitle() {
  return messageTitle;
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

 public Long getCommitTimeLong() {
  return commitTimeLong;
 }

 public String getMessage() {
  return message;
 }

 @Override
 public String toString() {
  return "hash: " + hash + " message: " + message;
 }
}
