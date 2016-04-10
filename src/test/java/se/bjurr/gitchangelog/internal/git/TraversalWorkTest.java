package se.bjurr.gitchangelog.internal.git;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import com.google.common.collect.Sets;

public class TraversalWorkTest {

 public class TraversalWorkMock extends TraversalWork {

  private final int commitTime;

  public TraversalWorkMock(RevCommit to, String currentTagName, int commitTime) {
   super(to, currentTagName);
   this.commitTime = commitTime;
  }

  private int getCommitTime() {
   return commitTime;
  }

  @Override
  public int compareTo(TraversalWork o) {
   return compareTo(this.getCommitTime(), ((TraversalWorkMock) o).getCommitTime());
  }
 }

 @Test
 public void testThatOldestCommitsAreTraversedFirst() {
  Set<TraversalWorkMock> sorted = Sets.newTreeSet();
  sorted.add(newMock(0));
  sorted.add(newMock(2));
  sorted.add(newMock(4));
  sorted.add(newMock(3));
  sorted.add(newMock(1));

  Iterator<TraversalWorkMock> itr = sorted.iterator();

  assertThat(itr.next().getCommitTime())//
    .isEqualTo(0);
  assertThat(itr.next().getCommitTime())//
    .isEqualTo(1);
  assertThat(itr.next().getCommitTime())//
    .isEqualTo(2);
  assertThat(itr.next().getCommitTime())//
    .isEqualTo(3);
  assertThat(itr.next().getCommitTime())//
    .isEqualTo(4);
 }

 private TraversalWorkMock newMock(int commitTime) {
  return new TraversalWorkMock(null, null, commitTime);
 }
}
