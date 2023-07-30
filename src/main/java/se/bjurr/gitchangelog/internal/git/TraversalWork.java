package se.bjurr.gitchangelog.internal.git;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.eclipse.jgit.revwalk.RevCommit;

@SuppressFBWarnings("CRLF_INJECTION_LOGS")
class TraversalWork implements Comparable<TraversalWork> {
  private final RevCommit to;
  private final String currentTagName;

  public TraversalWork(final RevCommit to, final String currentTagName) {
    this.to = to;
    this.currentTagName = currentTagName;
  }

  public String getCurrentTagName() {
    return this.currentTagName;
  }

  public RevCommit getTo() {
    return this.to;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.to == null) ? 0 : this.to.hashCode());
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
    final TraversalWork other = (TraversalWork) obj;
    if (this.to == null) {
      if (other.to != null) {
        return false;
      }
    } else if (!this.to.equals(other.to)) {
      return false;
    }
    return true;
  }

  @Override
  public int compareTo(final TraversalWork o) {
    final int otherCommitTime = o.getTo().getCommitTime();
    final int compareTo = this.compareTo(this.to.getCommitTime(), otherCommitTime);
    if (compareTo == 0) {
      return (this.to.getName()).compareTo(o.getTo().getName());
    }
    return compareTo;
  }

  int compareTo(final int y, final int x) {
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
  }

  @Override
  public String toString() {
    return "TraversalWork [to=" + this.to + ", currentTagName=" + this.currentTagName + "]";
  }
}
