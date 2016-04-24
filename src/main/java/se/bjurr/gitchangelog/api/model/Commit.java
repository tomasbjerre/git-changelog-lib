package se.bjurr.gitchangelog.api.model;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;

public class Commit implements Serializable {
 private static final long serialVersionUID = 6622555148468372816L;
 private final String authorName;
 private final String authorEmailAddress;
 private final String commitTime;
 private final Long commitTimeLong;
 private final String message;
 private final String hash;

 public Commit(String authorName, String authorEmailAddress, String commitTime, Long commitTimeLong, String message,
   String hash) {
  this.authorName = checkNotNull(authorName, "authorName");
  this.authorEmailAddress = checkNotNull(authorEmailAddress, "authorEmailAddress");
  this.message = checkNotNull(message, "message").trim();
  this.commitTime = checkNotNull(commitTime, "commitTime");
  this.commitTimeLong = checkNotNull(commitTimeLong, "commitTimeLong");
  this.hash = toHash(checkNotNull(hash, "hash"));
 }

 public String getHash() {
  return hash;
 }

 public String getMessageBody() {
  return toMessageBody(message);
 }

 public List<String> getMessageBodyItems() {
  return toMessageItems(message);
 }

 public String getMessageTitle() {
  return toMessageTitle(message);
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

 @VisibleForTesting
 static List<String> toMessageItems(String message) {
  List<String> toReturn = newArrayList();
  List<String> stringList = toNoEmptyStringsList(message);
  if (stringList.size() > 1) {
   List<String> notFirst = notFirst(stringList);
   for (String part : notFirst) {
    String candidate = part.trim();
    if (candidate.startsWith("*")) {
     candidate = candidate.substring(1).trim();
    }
    if (!candidate.isEmpty()) {
     toReturn.add(candidate);
    }
   }
  }
  return toReturn;
 }

 @VisibleForTesting
 static String toMessageBody(String message) {
  List<String> stringList = toNoEmptyStringsList(message);
  if (stringList.size() > 1) {
   List<String> notFirst = notFirst(stringList);
   return on("\n")//
     .join(notFirst);
  }
  return "";
 }

 @VisibleForTesting
 static String toMessageTitle(String message) {
  List<String> stringList = toNoEmptyStringsList(message);
  if (stringList.size() > 0) {
   return stringList.get(0).trim();
  }
  return "";
 }

 static private List<String> notFirst(List<String> stringList) {
  return stringList.subList(1, stringList.size());
 }

 static private List<String> toNoEmptyStringsList(String message) {
  List<String> toReturn = newArrayList();
  for (String part : message.split("\n")) {
   if (!part.isEmpty()) {
    toReturn.add(part);
   }
  }
  return toReturn;
 }

 @Override
 public String toString() {
  return "hash: " + hash + " message: " + message;
 }

 private static String toHash(String input) {
  return input.substring(0, 15);
 }
}
