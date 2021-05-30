package se.bjurr.gitchangelog.internal.git;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class TraversalWorkTest {

  public class TraversalWorkMock extends TraversalWork {

    private final int commitTime;

    public TraversalWorkMock(
        final RevCommit to, final String currentTagName, final int commitTime) {
      super(to, currentTagName);
      this.commitTime = commitTime;
    }

    private int getCommitTime() {
      return this.commitTime;
    }

    @Override
    public int compareTo(final TraversalWork o) {
      return this.compareTo(this.getCommitTime(), ((TraversalWorkMock) o).getCommitTime());
    }
  }

  @Test
  public void testThatOldestCommitsAreTraversedFirst() {
    final Set<TraversalWorkMock> sorted = new TreeSet<>();
    sorted.add(this.newMock(0));
    sorted.add(this.newMock(2));
    sorted.add(this.newMock(4));
    sorted.add(this.newMock(3));
    sorted.add(this.newMock(1));

    final Iterator<TraversalWorkMock> itr = sorted.iterator();

    assertThat(itr.next().getCommitTime()) //
        .isEqualTo(0);
    assertThat(itr.next().getCommitTime()) //
        .isEqualTo(1);
    assertThat(itr.next().getCommitTime()) //
        .isEqualTo(2);
    assertThat(itr.next().getCommitTime()) //
        .isEqualTo(3);
    assertThat(itr.next().getCommitTime()) //
        .isEqualTo(4);
  }

  private TraversalWorkMock newMock(final int commitTime) {
    return new TraversalWorkMock(null, null, commitTime);
  }
}
