package se.bjurr.gitchangelog.api.model;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import java.io.Serializable;
import java.util.List;

public class Commit implements Serializable {
  private static final long serialVersionUID = 6622555148468372816L;

  private static List<String> notFirst(final List<String> stringList) {
    return stringList.subList(1, stringList.size());
  }

  private static String toHash(final String input) {
    return input.substring(0, 15);
  }

  private static List<String> toNoEmptyStringsList(final String message) {
    final List<String> toReturn = newArrayList();
    for (final String part : Splitter.on("\n").split(message)) {
      if (!part.isEmpty()) {
        toReturn.add(part);
      }
    }
    return toReturn;
  }

  @VisibleForTesting
  static String toMessageBody(final String message) {
    final List<String> stringList = toNoEmptyStringsList(message);
    if (stringList.size() > 1) {
      final List<String> notFirst = notFirst(stringList);
      return on("\n") //
          .join(notFirst);
    }
    return "";
  }

  @VisibleForTesting
  static List<String> toMessageItems(final String message) {
    final List<String> toReturn = newArrayList();
    final List<String> stringList = toNoEmptyStringsList(message);
    if (stringList.size() > 1) {
      final List<String> notFirst = notFirst(stringList);
      for (final String part : notFirst) {
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
  static String toMessageTitle(final String message) {
    final List<String> stringList = toNoEmptyStringsList(message);
    if (stringList.size() > 0) {
      return stringList.get(0).trim();
    }
    return "";
  }

  private final String authorEmailAddress;
  private final String authorName;
  private final String commitTime;
  private final Long commitTimeLong;
  private final String hash;
  private final String hashFull;
  private final Boolean merge;
  private final String message;

  public Commit(
      final String authorName,
      final String authorEmailAddress,
      final String commitTime,
      final Long commitTimeLong,
      final String message,
      final String hash,
      final Boolean merge) {
    this.authorName = checkNotNull(authorName, "authorName");
    this.authorEmailAddress = checkNotNull(authorEmailAddress, "authorEmailAddress");
    this.message = checkNotNull(message, "message").trim();
    this.commitTime = checkNotNull(commitTime, "commitTime");
    this.commitTimeLong = checkNotNull(commitTimeLong, "commitTimeLong");
    this.hash = toHash(checkNotNull(hash, "hash"));
    this.hashFull = checkNotNull(hash, "hashFull");
    this.merge = checkNotNull(merge, "merge");
  }

  public String getAuthorEmailAddress() {
    return this.authorEmailAddress;
  }

  public String getAuthorName() {
    return this.authorName;
  }

  public String getCommitTime() {
    return this.commitTime;
  }

  public Long getCommitTimeLong() {
    return this.commitTimeLong;
  }

  public String getHash() {
    return this.hash;
  }

  public String getHashFull() {
    return this.hashFull;
  }

  public String getMessage() {
    return this.message;
  }

  public String getMessageBody() {
    return toMessageBody(this.message);
  }

  public List<String> getMessageBodyItems() {
    return toMessageItems(this.message);
  }

  public String getMessageTitle() {
    return toMessageTitle(this.message);
  }

  public Boolean isMerge() {
    return this.merge;
  }

  @Override
  public String toString() {
    return "hash: " + this.hash + " message: " + this.message;
  }
}
