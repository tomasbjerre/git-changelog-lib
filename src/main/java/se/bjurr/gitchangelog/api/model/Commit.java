package se.bjurr.gitchangelog.api.model;

import static se.bjurr.gitchangelog.internal.util.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Commit implements Serializable {
  private static final long serialVersionUID = 6622555148468372816L;

  private static List<String> notFirst(final List<String> stringList) {
    return stringList.subList(1, stringList.size());
  }

  private static String toHash(final String input) {
    return input.substring(0, 15);
  }

  private static List<String> toNoEmptyStringsList(final String message) {
    final List<String> toReturn = new ArrayList<>();
    for (final String part : message.split("\n")) {
      if (!part.isEmpty()) {
        toReturn.add(part);
      }
    }
    return toReturn;
  }

  static String toMessageBody(final String message) {
    final List<String> stringList = toNoEmptyStringsList(message);
    if (stringList.size() > 1) {
      final List<String> notFirst = notFirst(stringList);
      return notFirst.stream().collect(Collectors.joining("\n"));
    }
    return "";
  }

  static List<String> toMessageItems(final String message) {
    final List<String> toReturn = new ArrayList<>();
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result
            + ((this.authorEmailAddress == null) ? 0 : this.authorEmailAddress.hashCode());
    result = prime * result + ((this.authorName == null) ? 0 : this.authorName.hashCode());
    result = prime * result + ((this.commitTime == null) ? 0 : this.commitTime.hashCode());
    result = prime * result + ((this.commitTimeLong == null) ? 0 : this.commitTimeLong.hashCode());
    result = prime * result + ((this.hash == null) ? 0 : this.hash.hashCode());
    result = prime * result + ((this.hashFull == null) ? 0 : this.hashFull.hashCode());
    result = prime * result + ((this.merge == null) ? 0 : this.merge.hashCode());
    result = prime * result + ((this.message == null) ? 0 : this.message.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final Commit other = (Commit) obj;
    if (this.authorEmailAddress == null) {
      if (other.authorEmailAddress != null) {
        return false;
      }
    } else if (!this.authorEmailAddress.equals(other.authorEmailAddress)) {
      return false;
    }
    if (this.authorName == null) {
      if (other.authorName != null) {
        return false;
      }
    } else if (!this.authorName.equals(other.authorName)) {
      return false;
    }
    if (this.commitTime == null) {
      if (other.commitTime != null) {
        return false;
      }
    } else if (!this.commitTime.equals(other.commitTime)) {
      return false;
    }
    if (this.commitTimeLong == null) {
      if (other.commitTimeLong != null) {
        return false;
      }
    } else if (!this.commitTimeLong.equals(other.commitTimeLong)) {
      return false;
    }
    if (this.hash == null) {
      if (other.hash != null) {
        return false;
      }
    } else if (!this.hash.equals(other.hash)) {
      return false;
    }
    if (this.hashFull == null) {
      if (other.hashFull != null) {
        return false;
      }
    } else if (!this.hashFull.equals(other.hashFull)) {
      return false;
    }
    if (this.merge == null) {
      if (other.merge != null) {
        return false;
      }
    } else if (!this.merge.equals(other.merge)) {
      return false;
    }
    if (this.message == null) {
      if (other.message != null) {
        return false;
      }
    } else if (!this.message.equals(other.message)) {
      return false;
    }
    return true;
  }
}
