package se.bjurr.gitchangelog.test;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand.FastForwardMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import se.bjurr.gitchangelog.internal.git.GitRepo;

/**
 * Builds a small, throwaway git repository in a temp directory for a test to exercise {@link
 * GitRepo} against, instead of depending on this project's own commit history. Every method returns
 * {@code this}, so a test can spell out exactly the repo shape it needs as a short, readable chain
 * of calls, and commits/tags are referred to by the label given when they were created rather than
 * by a hash copied out of a real `git log`.
 *
 * <p>Every commit (including merge commits) is stamped with a strictly increasing, virtual
 * timestamp instead of the real wall clock, so tests that assert on commit ordering aren't at the
 * mercy of how fast the JVM happens to create commits, or of two commits landing in the same
 * second.
 */
public final class TestGitRepo implements Closeable {

  private static final Instant EPOCH = Instant.parse("2020-01-01T00:00:00Z");

  private final File dir;
  private final Git git;
  private final Map<String, ObjectId> commits = new HashMap<>();
  private int tick = 0;

  private TestGitRepo(final File dir) throws Exception {
    this.dir = dir;
    this.git = Git.init().setDirectory(dir).setInitialBranch("master").call();
  }

  public static TestGitRepo in(final File dir) throws Exception {
    return new TestGitRepo(dir);
  }

  /**
   * Writes {@code content} to {@code path} (parent directories created as needed) and stages it.
   */
  public TestGitRepo write(final String path, final String content) throws Exception {
    final File file = new File(this.dir, path);
    file.getParentFile().mkdirs();
    Files.write(file.toPath(), content.getBytes(UTF_8));
    this.git.add().addFilepattern(".").call();
    return this;
  }

  /** Stages the removal of a previously written file. */
  public TestGitRepo delete(final String path) throws Exception {
    this.git.rm().addFilepattern(path).call();
    return this;
  }

  /** Commits currently staged changes and remembers the result under {@code label}. */
  public TestGitRepo commit(final String label, final String message) throws Exception {
    final PersonIdent identity = this.nextIdentity();
    final RevCommit revCommit =
        this.git.commit().setMessage(message).setAuthor(identity).setCommitter(identity).call();
    this.commits.put(label, revCommit.copy());
    return this;
  }

  /** Convenience for the common case of a commit that only touches one file. */
  public TestGitRepo commit(
      final String label, final String message, final String path, final String content)
      throws Exception {
    return this.write(path, content).commit(label, message);
  }

  public TestGitRepo branch(final String name) throws Exception {
    this.git.branchCreate().setName(name).call();
    return this;
  }

  public TestGitRepo checkout(final String name) throws Exception {
    this.git.checkout().setName(name).call();
    return this;
  }

  /**
   * Merges {@code branch} into the currently checked out branch, always creating a real merge
   * commit even when a fast-forward would be possible, and remembers the result under {@code
   * label}.
   */
  public TestGitRepo merge(final String label, final String branch, final String message)
      throws Exception {
    final Ref ref = this.git.getRepository().findRef(branch);
    // setCommit(false) merges into the index/working tree and writes MERGE_HEAD, without
    // creating the merge commit itself - that's done below via a plain commit, so the merge
    // commit gets the same virtual, strictly increasing timestamp as every other commit.
    this.git.merge().include(ref).setFastForward(FastForwardMode.NO_FF).setCommit(false).call();
    return this.commit(label, message);
  }

  /** Tags the current HEAD. */
  public TestGitRepo tag(final String name) throws Exception {
    return this.tag(name, null);
  }

  /** Tags a previously labelled commit, or the current HEAD when {@code label} is null. */
  public TestGitRepo tag(final String name, final String label) throws Exception {
    final var tagCommand = this.git.tag().setName(name);
    if (label != null) {
      tagCommand.setObjectId(this.revCommit(label));
    }
    tagCommand.call();
    return this;
  }

  /** Adds a git-note to a previously labelled commit. */
  public TestGitRepo note(final String label, final String note) throws Exception {
    this.git.notesAdd().setObjectId(this.revCommit(label)).setMessage(note).call();
    return this;
  }

  /** The id of a previously labelled commit. */
  public ObjectId id(final String label) {
    final ObjectId id = this.commits.get(label);
    if (id == null) {
      throw new IllegalArgumentException("No commit labelled \"" + label + "\"");
    }
    return id;
  }

  /** The full hash of a previously labelled commit. */
  public String hash(final String label) {
    return this.id(label).getName();
  }

  /** The commit time of a previously labelled commit, at the same (1-second) precision git uses. */
  public Date commitDate(final String label) throws IOException {
    return new Date(this.revCommit(label).getCommitTime() * 1000L);
  }

  private RevCommit revCommit(final String label) throws IOException {
    try (RevWalk revWalk = new RevWalk(this.git.getRepository())) {
      return revWalk.parseCommit(this.id(label));
    }
  }

  private PersonIdent nextIdentity() {
    final Instant when = EPOCH.plus(Duration.ofMinutes(this.tick++));
    return new PersonIdent("Author", "author@example.com", when, ZoneOffset.UTC);
  }

  /** Opens the {@link GitRepo} under test against this repo. */
  public GitRepo open() throws Exception {
    return new GitRepo(this.dir);
  }

  @Override
  public void close() throws IOException {
    this.git.close();
  }
}
