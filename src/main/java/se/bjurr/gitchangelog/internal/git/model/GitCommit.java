package se.bjurr.gitchangelog.internal.git.model;

import java.util.Date;

public class GitCommit implements Comparable<GitCommit> {
 private final String authorEmailAddress;
 private final String authorName;
 private final Date commitTime;
 private final String hash;
 private final Boolean merge;
 private final String message;

 public GitCommit(String authorName, String authorEmailAddress, Date commitTime, String message, String hash,
   Boolean merge) {
  this.authorEmailAddress = authorEmailAddress;
  this.authorName = authorName;
  this.commitTime = commitTime;
  this.message = message;
  this.hash = hash;
  this.merge = merge;
 }

 @Override
 public int compareTo(GitCommit o) {
  int compareTo = o.commitTime.compareTo(this.commitTime);
  if (compareTo == 0) {
   return o.hash.compareTo(this.hash);
  }
  return compareTo;
 }

 @Override
 public boolean equals(Object obj) {
  if (this == obj) {
   return true;
  }
  if (obj == null) {
   return false;
  }
  if (getClass() != obj.getClass()) {
   return false;
  }
  GitCommit other = (GitCommit) obj;
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
  if (this.hash == null) {
   if (other.hash != null) {
    return false;
   }
  } else if (!this.hash.equals(other.hash)) {
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

 public String getAuthorEmailAddress() {
  return this.authorEmailAddress;
 }

 public String getAuthorName() {
  return this.authorName;
 }

 public Date getCommitTime() {
  return this.commitTime;
 }

 public String getHash() {
  return this.hash;
 }

 public String getMessage() {
  return this.message;
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((this.authorEmailAddress == null) ? 0 : this.authorEmailAddress.hashCode());
  result = prime * result + ((this.authorName == null) ? 0 : this.authorName.hashCode());
  result = prime * result + ((this.commitTime == null) ? 0 : this.commitTime.hashCode());
  result = prime * result + ((this.hash == null) ? 0 : this.hash.hashCode());
  result = prime * result + ((this.merge == null) ? 0 : this.merge.hashCode());
  result = prime * result + ((this.message == null) ? 0 : this.message.hashCode());
  return result;
 }

 public Boolean isMerge() {
  return this.merge;
 }

 @Override
 public String toString() {
  return "GitCommit [authorEmailAddress=" + this.authorEmailAddress + ", authorName=" + this.authorName
    + ", commitTime=" + this.commitTime + ", hash=" + this.hash + ", merge=" + this.merge + ", message=" + this.message
    + "]";
 }
}
