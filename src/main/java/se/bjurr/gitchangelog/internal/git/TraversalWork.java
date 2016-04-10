package se.bjurr.gitchangelog.internal.git;

import org.eclipse.jgit.revwalk.RevCommit;

import com.google.common.annotations.VisibleForTesting;

class TraversalWork implements Comparable<TraversalWork> {
 private final RevCommit to;
 private final String currentTagName;

 public TraversalWork(RevCommit to, String currentTagName) {
  this.to = to;
  this.currentTagName = currentTagName;
 }

 public String getCurrentTagName() {
  return currentTagName;
 }

 public RevCommit getTo() {
  return to;
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((currentTagName == null) ? 0 : currentTagName.hashCode());
  result = prime * result + ((to == null) ? 0 : to.hashCode());
  return result;
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
  TraversalWork other = (TraversalWork) obj;
  if (currentTagName == null) {
   if (other.currentTagName != null) {
    return false;
   }
  } else if (!currentTagName.equals(other.currentTagName)) {
   return false;
  }
  if (to == null) {
   if (other.to != null) {
    return false;
   }
  } else if (!to.equals(other.to)) {
   return false;
  }
  return true;
 }

 @Override
 public int compareTo(TraversalWork o) {
  int otherCommitTime = o.getTo().getCommitTime();
  return compareTo(to.getCommitTime(), otherCommitTime);
 }

 @VisibleForTesting
 int compareTo(int selfCommitTime, int otherCommitTime) {
  return new Integer(selfCommitTime)//
    .compareTo(otherCommitTime);
 }
}
