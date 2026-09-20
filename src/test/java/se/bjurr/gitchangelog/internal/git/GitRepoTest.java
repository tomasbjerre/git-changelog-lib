package se.bjurr.gitchangelog.internal.git;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_HEAD;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_MASTER;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import org.eclipse.jgit.lib.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import se.bjurr.gitchangelog.api.InclusivenessStrategy;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;
import se.bjurr.gitchangelog.test.TestGitRepo;

/**
 * Every test builds its own small, throwaway repo with {@link TestGitRepo} rather than reading this
 * project's own git history, so the commits and tags a test relies on are spelled out right there
 * in the test instead of being hashes that only make sense next to a `git log`.
 */
public class GitRepoTest {

  @Test
  public void testThatCommitsBetweenCommitAndCommitCanBeListed(@TempDir final File tempRepoDir)
      throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir)
                .commit("first", "Initial commit", "file.txt", "1")
                .commit("middle", "Middle commit", "file.txt", "2")
                .commit("last", "Last commit", "file.txt", "3");
        GitRepo gitRepo = repo.open()) {
      final ObjectId firstCommit = gitRepo.getCommit(repo.hash("first"));
      final ObjectId lastCommit = gitRepo.getCommit(repo.hash("last"));

      final List<GitCommit> diff =
          gitRepo
              .getGitRepoData(
                  new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
                  new RevisionBoundary<ObjectId>(lastCommit, InclusivenessStrategy.DEFAULT),
                  "No tag",
                  Optional.empty())
              .getGitCommits();

      assertThat(diff) //
          .as("newest first; \"first\" is the repo's root commit, so it's included too") //
          .extracting(GitCommit::getHash) //
          .containsExactly(repo.hash("last"), repo.hash("middle"), repo.hash("first"));
    }
  }

  @Test
  public void testThatCommitsBetweenCommitAndReferenceCanBeListed(@TempDir final File tempRepoDir)
      throws Exception {
    try (TestGitRepo repo = TestGitRepo.in(tempRepoDir)) {
      for (int i = 1; i <= 12; i++) {
        repo.commit("commit-" + i, "Commit " + i, "file.txt", Integer.toString(i));
      }

      try (GitRepo gitRepo = repo.open()) {
        final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
        final ObjectId lastCommit = gitRepo.getRef(REF_MASTER);

        final List<GitCommit> diff =
            gitRepo
                .getGitRepoData(
                    new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
                    new RevisionBoundary<ObjectId>(lastCommit, InclusivenessStrategy.DEFAULT),
                    "No tag",
                    Optional.empty())
                .getGitCommits();

        assertThat(diff) //
            .as("everything from the root commit to the master branch tip") //
            .extracting(GitCommit::getHash) //
            .hasSize(12) //
            .endsWith(repo.hash("commit-1"));
      }
    }
  }

  @Test
  public void testThatCommitsBetweenZeroCommitAndCommitCanBeListed(@TempDir final File tempRepoDir)
      throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir)
                .commit("c1", "Initial commit", "file.txt", "1")
                .tag("0.0.1", "c1")
                .commit("c2", "Second commit", "file.txt", "2")
                .commit("c3", "Third commit", "file.txt", "3")
                .commit("c4", "Fourth commit", "file.txt", "4")
                .commit("c5", "Fifth commit", "file.txt", "5")
                .commit("c6", "Sixth commit", "file.txt", "6")
                .tag("1.0", "c6");
        GitRepo gitRepo = repo.open()) {
      final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
      final ObjectId lastCommit = gitRepo.getCommit(repo.hash("c6"));

      final GitRepoData gitRepoData =
          gitRepo.getGitRepoData(
              new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
              new RevisionBoundary<ObjectId>(lastCommit, InclusivenessStrategy.DEFAULT),
              "No tag",
              Optional.empty());

      assertThat(gitRepoData.getGitCommits()) //
          .as("commits from the root up to and including the tagged commit") //
          .extracting(GitCommit::getHash) //
          .endsWith(repo.hash("c1"));
      assertThat(gitRepoData.getGitCommits()).hasSize(6);
      assertThat(gitRepoData.getGitTags()) //
          .as("both 0.0.1 and 1.0 are reachable in that range") //
          .hasSize(2);
    }
  }

  @Test
  public void testThatCommitCountIsNullByDefault(@TempDir final File tempRepoDir) throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir)
                .commit("first", "Initial commit", "file.txt", "1")
                .commit("second", "Second commit", "file.txt", "2");
        GitRepo gitRepo = repo.open()) {
      final List<GitCommit> diff =
          gitRepo
              .getGitRepoData(
                  new RevisionBoundary<ObjectId>(
                      gitRepo.getCommit(ZERO_COMMIT), InclusivenessStrategy.DEFAULT),
                  new RevisionBoundary<ObjectId>(
                      gitRepo.getRef(REF_HEAD), InclusivenessStrategy.DEFAULT),
                  "No tag",
                  Optional.empty())
              .getGitCommits();

      assertThat(diff).allMatch(commit -> commit.getCommitCount() == null);
    }
  }

  @Test
  public void testThatCommitCountCanBeComputedForTheFirstCommitInTheRepo(
      @TempDir final File tempRepoDir) throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir)
                .commit("first", "Initial commit", "file.txt", "1")
                .commit("second", "Second commit", "file.txt", "2");
        GitRepo gitRepo = repo.open()) {
      gitRepo.setCommitCount(true);

      final List<GitCommit> diff =
          gitRepo
              .getGitRepoData(
                  new RevisionBoundary<ObjectId>(
                      gitRepo.getCommit(ZERO_COMMIT), InclusivenessStrategy.DEFAULT),
                  new RevisionBoundary<ObjectId>(
                      gitRepo.getRef(REF_HEAD), InclusivenessStrategy.DEFAULT),
                  "No tag",
                  Optional.empty())
              .getGitCommits();

      final GitCommit first =
          diff.stream().filter(c -> c.getHash().equals(repo.hash("first"))).findFirst().get();
      final GitCommit second =
          diff.stream().filter(c -> c.getHash().equals(repo.hash("second"))).findFirst().get();
      assertThat(first.getCommitCount()) //
          .as("the very first commit ever made in the repo has exactly itself as an ancestor") //
          .isEqualTo(1);
      assertThat(second.getCommitCount()).isEqualTo(2);
    }
  }

  @Test
  public void testThatCommitsCanBeRetrieved(@TempDir final File tempRepoDir) throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir)
                .commit("first", "Initial commit", "file.txt", "1")
                .commit("second", "Second commit", "file.txt", "2");
        GitRepo gitRepo = repo.open()) {
      assertThat(gitRepo.getCommit(repo.hash("first")).name()).isEqualTo(repo.hash("first"));
      assertThat(gitRepo.getCommit(repo.hash("second")).name()).isEqualTo(repo.hash("second"));
    }
  }

  @Test
  public void testThatCommitsFromMergeNotInFromAreIncluded(@TempDir final File tempRepoDir)
      throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir)
                // "root" precedes the "from" boundary below on purpose: if "base" were the repo's
                // very first commit, GitRepo's root-commit special case would include it despite
                // the exclusive-from boundary, and this test wants to prove the opposite - that a
                // regular, non-root "from" commit is excluded.
                .commit("root", "Root commit", "file.txt", "0")
                .commit("base", "Base commit", "file.txt", "1")
                .branch("feature")
                .checkout("feature")
                .commit("feature-commit", "Some stuff in the feature branch", "feature.txt", "1")
                .checkout("master")
                .merge("merge", "feature", "Merge branch 'feature' into master");
        GitRepo gitRepo = repo.open()) {
      final ObjectId from = gitRepo.getCommit(repo.hash("base"));
      final ObjectId to = gitRepo.getCommit(repo.hash("merge"));

      final GitRepoData gitRepoData =
          gitRepo.getGitRepoData(
              new RevisionBoundary<ObjectId>(from, InclusivenessStrategy.DEFAULT),
              new RevisionBoundary<ObjectId>(to, InclusivenessStrategy.DEFAULT),
              "No tag",
              Optional.<String>empty());

      assertThat(this.messages(gitRepoData.getGitCommits())) //
          .as(
              "the feature commit is pulled in by the merge even though it isn't on the direct"
                  + " line of ancestry from the 'from' boundary") //
          .containsExactly(
              "Merge branch 'feature' into master", "Some stuff in the feature branch");
    }
  }

  @Test
  public void testThatCommitsSecondReleaseCommitCanBeListed(@TempDir final File tempRepoDir)
      throws Exception {
    try (TestGitRepo repo = TestGitRepo.in(tempRepoDir)) {
      for (int i = 1; i <= 6; i++) {
        repo.commit("v1.0-" + i, "Commit " + i + " towards 1.0", "file.txt", "1.0-" + i);
      }
      repo.tag("1.0", "v1.0-6");
      for (int i = 1; i <= 8; i++) {
        repo.commit("v1.1-" + i, "Commit " + i + " towards 1.1", "file.txt", "1.1-" + i);
      }
      repo.tag("1.1", "v1.1-8");

      try (GitRepo gitRepo = repo.open()) {
        final ObjectId firstRelease = gitRepo.getRef("refs/tags/1.0");
        final ObjectId secondRelease = gitRepo.getRef("refs/tags/1.1");

        final List<GitCommit> betweenReleases =
            gitRepo
                .getGitRepoData(
                    new RevisionBoundary<ObjectId>(firstRelease, InclusivenessStrategy.DEFAULT),
                    new RevisionBoundary<ObjectId>(secondRelease, InclusivenessStrategy.DEFAULT),
                    "No tag",
                    Optional.empty())
                .getGitCommits();
        assertThat(betweenReleases) //
            .as("commits in the second release, counted from the 1.0 tag") //
            .extracting(GitCommit::getHash) //
            .containsExactly(
                repo.hash("v1.1-8"),
                repo.hash("v1.1-7"),
                repo.hash("v1.1-6"),
                repo.hash("v1.1-5"),
                repo.hash("v1.1-4"),
                repo.hash("v1.1-3"),
                repo.hash("v1.1-2"),
                repo.hash("v1.1-1"));

        final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
        final List<GitCommit> sinceTheBeginning =
            gitRepo
                .getGitRepoData(
                    new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
                    new RevisionBoundary<ObjectId>(secondRelease, InclusivenessStrategy.DEFAULT),
                    "No tag",
                    Optional.empty())
                .getGitCommits();
        assertThat(sinceTheBeginning) //
            .as("commits in the second release, counted from the very first commit") //
            .hasSize(14);
      }
    }
  }

  @Test
  public void testThatMergeCommitsBetweenZeroCommitAndTestCanBeListed(
      @TempDir final File tempRepoDir) throws Exception {
    try (TestGitRepo repo = TestGitRepo.in(tempRepoDir)) {
      for (int i = 1; i <= 14; i++) {
        repo.commit("base-" + i, "Base commit " + i, "file.txt", "base-" + i);
      }
      repo //
          .branch("feature")
          .checkout("feature")
          .commit("feature-commit", "Some stuff in the feature branch", "feature.txt", "1")
          .checkout("master")
          .branch("test")
          .checkout("test")
          .merge("merge", "feature", "Merge branch 'feature' into test");

      try (GitRepo gitRepo = repo.open()) {
        final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
        final ObjectId lastCommit = gitRepo.getRef("test");

        final GitRepoData gitRepoData =
            gitRepo.getGitRepoData(
                new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
                new RevisionBoundary<ObjectId>(lastCommit, InclusivenessStrategy.DEFAULT),
                "No tag",
                Optional.empty());

        assertThat(gitRepoData.getGitCommits()) //
            .as("14 base commits, plus the feature commit, plus the merge commit itself") //
            .hasSize(16);
        assertThat(gitRepoData.getGitCommits()) //
            .filteredOn(GitCommit::isMerge) //
            .hasSize(1);
      }
    }
  }

  @Test
  public void testThatRepoCanBeFound(@TempDir final File tempRepoDir) throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir).commit("first", "Initial commit", "file.txt", "1");
        GitRepo gitRepo = repo.open()) {
      assertThat(gitRepo).isNotNull();
    }
  }

  @Test
  public void testThatShortHashCanBeUsedToFindCommits(@TempDir final File tempRepoDir)
      throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir).commit("first", "Initial commit", "file.txt", "1");
        GitRepo gitRepo = repo.open()) {
      final String fullHash = repo.hash("first");
      final String shortHash = fullHash.substring(0, 7);

      assertThat(gitRepo.getCommit(fullHash).getName()).isEqualTo(fullHash);
      assertThat(gitRepo.getCommit(shortHash).getName()).isEqualTo(fullHash);
    }
  }

  @Test
  public void testThatTagCanBeIgnored(@TempDir final File tempRepoDir) throws Exception {
    try (TestGitRepo repo = this.mainAndFeatureBranchWithTags(tempRepoDir);
        GitRepo gitRepo = repo.open()) {
      final ObjectId from = gitRepo.getCommit(repo.hash("anchor"));
      final ObjectId to = gitRepo.getRef("test");

      final GitRepoData gitRepoData =
          gitRepo.getGitRepoData(
              new RevisionBoundary<ObjectId>(from, InclusivenessStrategy.DEFAULT),
              new RevisionBoundary<ObjectId>(to, InclusivenessStrategy.DEFAULT),
              "No tag",
              Optional.of(".*tag-in-feature$"));
      final Map<String, GitTag> perTag = this.perTag(gitRepoData.getGitTags());

      assertThat(perTag.keySet()) //
          .as(
              "tag-in-feature is excluded by the ignore pattern, so its commit falls into the"
                  + " next reachable tag instead") //
          .containsExactly("refs/tags/test");
      assertThat(this.messages(perTag.get("refs/tags/test").getGitCommits())) //
          .containsExactly(
              "Commit on main after the merge",
              "Merge branch 'feature' into main",
              "Commit on the feature branch",
              "Commit on main before the feature branch");
    }
  }

  @Test
  public void testThatTagInFeatureBranchAndMainBranchIsNotMixed(@TempDir final File tempRepoDir)
      throws Exception {
    try (TestGitRepo repo = this.mainAndFeatureBranchWithTags(tempRepoDir);
        GitRepo gitRepo = repo.open()) {
      final ObjectId from = gitRepo.getCommit(repo.hash("anchor"));
      final ObjectId to = gitRepo.getRef("test");

      final GitRepoData gitRepoData =
          gitRepo.getGitRepoData(
              new RevisionBoundary<ObjectId>(from, InclusivenessStrategy.DEFAULT),
              new RevisionBoundary<ObjectId>(to, InclusivenessStrategy.DEFAULT),
              "No tag",
              Optional.<String>empty());
      final Map<String, GitTag> perTag = this.perTag(gitRepoData.getGitTags());

      assertThat(perTag.keySet()) //
          .containsExactly("refs/tags/tag-in-feature", "refs/tags/test");
      assertThat(this.messages(perTag.get("refs/tags/test").getGitCommits())) //
          .as("commits merged in from the feature branch stay out of the main branch's tag") //
          .containsExactly(
              "Commit on main after the merge",
              "Merge branch 'feature' into main",
              "Commit on main before the feature branch");
      assertThat(this.messages(perTag.get("refs/tags/tag-in-feature").getGitCommits())) //
          .as("the feature branch's own tag only contains its own commit") //
          .containsExactly("Commit on the feature branch");
    }
  }

  /**
   * Builds: root -- anchor -- main-1 -- (feature: feature-1, tagged tag-in-feature) -- merge --
   * main-2, tagged test. Shared by {@link #testThatTagCanBeIgnored} and {@link
   * #testThatTagInFeatureBranchAndMainBranchIsNotMixed}, which only differ in whether
   * tag-in-feature is excluded by pattern. "root" precedes "anchor" (the "from" boundary used by
   * both tests) so that boundary is a regular, non-root commit - otherwise GitRepo's root-commit
   * special case would include it despite the exclusive-from boundary.
   */
  private TestGitRepo mainAndFeatureBranchWithTags(final File dir) throws Exception {
    return TestGitRepo.in(dir)
        .commit("root", "Root commit", "file.txt", "root")
        .commit("anchor", "Anchor commit, outside the range under test", "file.txt", "0")
        .commit("main-1", "Commit on main before the feature branch", "file.txt", "1")
        .branch("feature")
        .checkout("feature")
        .commit("feature-1", "Commit on the feature branch", "feature.txt", "1")
        .tag("tag-in-feature", "feature-1")
        .checkout("master")
        .merge("merge", "feature", "Merge branch 'feature' into main")
        .commit("main-2", "Commit on main after the merge", "file.txt", "2")
        .tag("test", "main-2");
  }

  @Test
  public void
      testThatTagInFeatureBranchDoesNotIncludeNewUnmergedCommitsInItsMainBranchWhenFeatureLaterMerged(
          @TempDir final File tempRepoDir) throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir)
                // "root" precedes "anchor" (the "from" boundary below) so that boundary is a
                // regular, non-root commit - see mainAndFeatureBranchWithTags for why that matters.
                .commit("root", "Root commit", "file.txt", "root")
                .commit("anchor", "Anchor commit, outside the range under test", "file.txt", "0")
                .commit("main-1", "Base commit", "file.txt", "1")
                .branch("feature")
                .checkout("feature")
                .commit("feature-1", "Feature work merged in time", "feature.txt", "1")
                .checkout("master")
                .merge("first-merge", "feature", "Merge branch 'feature' into main")
                // More feature work, tagged and merged only *after* the range under test ends at
                // first-merge - it must not show up in that earlier, already-closed range.
                .checkout("feature")
                .commit("feature-2", "Feature work added after the range", "feature.txt", "2")
                .tag("tag-in-feature", "feature-2")
                .checkout("master")
                .merge("second-merge", "feature", "Merge branch 'feature' into main again");
        GitRepo gitRepo = repo.open()) {
      final ObjectId from = gitRepo.getCommit(repo.hash("anchor"));
      final ObjectId to = gitRepo.getCommit(repo.hash("first-merge"));

      final GitRepoData gitRepoData =
          gitRepo.getGitRepoData(
              new RevisionBoundary<ObjectId>(from, InclusivenessStrategy.DEFAULT),
              new RevisionBoundary<ObjectId>(to, InclusivenessStrategy.DEFAULT),
              "Untagged",
              Optional.<String>empty());
      final Map<String, GitTag> perTag = this.perTag(gitRepoData.getGitTags());

      assertThat(perTag.keySet()) //
          .as(
              "tag-in-feature sits on a commit outside this range, so it must not leak in - even"
                  + " though, later, that feature branch does get merged again") //
          .containsExactly("Untagged");
      assertThat(this.messages(perTag.get("Untagged").getGitCommits())) //
          .containsExactly(
              "Merge branch 'feature' into main", "Feature work merged in time", "Base commit");
    }
  }

  @Test
  public void testThatTagsCanBeListed(@TempDir final File tempRepoDir) throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir)
                .commit("first", "Initial commit", "file.txt", "1")
                .tag("1.0");
        GitRepo gitRepo = repo.open()) {
      final GitRepoData gitRepoData =
          gitRepo.getGitRepoData(
              new RevisionBoundary<ObjectId>(
                  gitRepo.getCommit(ZERO_COMMIT), InclusivenessStrategy.DEFAULT),
              new RevisionBoundary<ObjectId>(
                  gitRepo.getRef(REF_MASTER), InclusivenessStrategy.DEFAULT),
              "No tag",
              Optional.empty());
      assertThat(gitRepoData.getGitTags()).isNotEmpty();
    }
  }

  @Test
  public void testThatZeroCommitCanBeRetrieved(@TempDir final File tempRepoDir) throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir)
                .commit("first", "Initial commit", "file.txt", "1")
                .commit("second", "Second commit", "file.txt", "2");
        GitRepo gitRepo = repo.open()) {
      final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);

      assertThat(firstCommit.name()).isEqualTo(repo.hash("first"));
    }
  }

  @Test
  public void testThatRepoFilterReducesTheNumberOfCommitsUsingPathFilters(
      @TempDir final File tempRepoDir) throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir)
                .commit("root", "Initial commit", "README.md", "hello")
                .commit("src-1", "Add a source file", "src/Main.java", "class Main {}")
                .commit("docs-1", "Add documentation", "docs/readme.txt", "docs")
                .commit(
                    "src-2",
                    "Change the source file",
                    "src/Main.java",
                    "class Main { void run() {} }")
                .tag("1.0");
        GitRepo gitRepo = repo.open()) {
      gitRepo.setPathFilters(Arrays.asList("src/"));
      final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
      final ObjectId lastCommit = gitRepo.getRef("1.0");

      final List<GitCommit> gitCommits =
          gitRepo
              .getGitRepoData(
                  new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
                  new RevisionBoundary<ObjectId>(lastCommit, InclusivenessStrategy.DEFAULT),
                  "No tag",
                  Optional.empty())
              .getGitCommits();

      assertThat(gitCommits) //
          .as(
              "only the commits that touch src/ are kept - plus the root commit, which GitRepo"
                  + " always includes when \"from\" is ZERO_COMMIT, regardless of path filters") //
          .extracting(GitCommit::getHash) //
          .containsExactly(repo.hash("src-2"), repo.hash("src-1"), repo.hash("root"));
    }
  }

  @Test
  public void testThatRepoFilterIncreasesTheNumberOfCommitsUsingMultiplePathFilters(
      @TempDir final File tempRepoDir) throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir)
                .commit("root", "Initial commit", "README.md", "hello")
                .commit("src-1", "Add a source file", "src/Main.java", "class Main {}")
                .commit("docs-1", "Add documentation", "docs/readme.txt", "docs")
                .commit("examples-1", "Add an example", "examples/Sample.java", "class Sample {}")
                .tag("1.0");
        GitRepo gitRepo = repo.open()) {
      final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
      final ObjectId lastCommit = gitRepo.getRef("1.0");

      gitRepo.setPathFilters(Arrays.asList("src/"));
      final List<GitCommit> withOnePathFilter =
          gitRepo
              .getGitRepoData(
                  new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
                  new RevisionBoundary<ObjectId>(lastCommit, InclusivenessStrategy.DEFAULT),
                  "No tag",
                  Optional.empty())
              .getGitCommits();
      assertThat(withOnePathFilter) //
          .as("the root commit is always included, regardless of path filters") //
          .extracting(GitCommit::getHash) //
          .containsExactly(repo.hash("src-1"), repo.hash("root"));

      gitRepo.setPathFilters(Arrays.asList("src/", "examples/"));
      final List<GitCommit> withTwoPathFilters =
          gitRepo
              .getGitRepoData(
                  new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
                  new RevisionBoundary<ObjectId>(lastCommit, InclusivenessStrategy.DEFAULT),
                  "No tag",
                  Optional.empty())
              .getGitCommits();
      assertThat(withTwoPathFilters) //
          .as("adding examples/ as a second path filter brings its commit back in") //
          .extracting(GitCommit::getHash) //
          .containsExactly(repo.hash("examples-1"), repo.hash("src-1"), repo.hash("root"));
    }
  }

  @Test
  public void priorityTags() throws Exception {
    assertThat(GitRepo.isFirstTagSemanticallyHighest(null, null)).isFalse();

    assertThat(GitRepo.isFirstTagSemanticallyHighest(null, "hello")).isTrue();
    assertThat(GitRepo.isFirstTagSemanticallyHighest("hello", null)).isFalse();
    assertThat(GitRepo.isFirstTagSemanticallyHighest("hello", "hello")).isFalse();

    assertThat(GitRepo.isFirstTagSemanticallyHighest("1.2.3", "hello")).isFalse();
    assertThat(GitRepo.isFirstTagSemanticallyHighest("hello", "1.2.3")).isTrue();
    assertThat(GitRepo.isFirstTagSemanticallyHighest("hello", "hello")).isFalse();

    assertThat(GitRepo.isFirstTagSemanticallyHighest("1.2.3", "1.2.3")).isFalse();
    assertThat(GitRepo.isFirstTagSemanticallyHighest("1.2.3", "1.2.4")).isFalse();
    assertThat(GitRepo.isFirstTagSemanticallyHighest("1.2.4", "1.2.3")).isTrue();
  }

  @Test
  public void testThatUntaggedGroupGetsTheDateOfItsLatestCommit(@TempDir final File tempRepoDir)
      throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir)
                .commit("tagged", "Tagged commit", "file1.txt", "content")
                .tag("v1.0", "tagged")
                .commit("untagged", "Untagged commit", "file2.txt", "more content");
        GitRepo gitRepo = repo.open()) {
      final ObjectId from = gitRepo.getCommit(ZERO_COMMIT);
      final ObjectId to = gitRepo.getRef(REF_HEAD);

      final List<GitTag> gitTags =
          gitRepo
              .getGitRepoData(
                  new RevisionBoundary<ObjectId>(from, InclusivenessStrategy.DEFAULT),
                  new RevisionBoundary<ObjectId>(to, InclusivenessStrategy.DEFAULT),
                  "Next release",
                  Optional.empty())
              .getGitTags();

      assertThat(gitTags)
          .extracting(GitTag::getName)
          .containsExactlyInAnyOrder("refs/tags/v1.0", "Next release");
      final GitTag untaggedGroup =
          gitTags.stream().filter(t -> t.getName().equals("Next release")).findFirst().get();
      assertThat(untaggedGroup.getTagTime()).isEqualTo(repo.commitDate("untagged"));
    }
  }

  @Test
  public void testThatGitNotesArePopulatedOnCommits(@TempDir final File tempRepoDir)
      throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir)
                .commit("first", "Some commit", "file.txt", "content")
                .note("first", "This is a note\non the commit");
        GitRepo gitRepo = repo.open()) {
      final ObjectId from = gitRepo.getCommit(ZERO_COMMIT);
      final ObjectId to = gitRepo.getRef(REF_HEAD);

      final List<GitCommit> gitCommits =
          gitRepo
              .getGitRepoData(
                  new RevisionBoundary<ObjectId>(from, InclusivenessStrategy.DEFAULT),
                  new RevisionBoundary<ObjectId>(to, InclusivenessStrategy.DEFAULT),
                  "No tag",
                  Optional.empty())
              .getGitCommits();

      assertThat(gitCommits).hasSize(1);
      assertThat(gitCommits.get(0).getMessageNotes()).isEqualTo("This is a note\non the commit");
    }
  }

  @Test
  public void testThatMessageNotesIsEmptyWhenNoGitNoteExists(@TempDir final File tempRepoDir)
      throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir).commit("first", "Some commit", "file.txt", "content");
        GitRepo gitRepo = repo.open()) {
      final ObjectId from = gitRepo.getCommit(ZERO_COMMIT);
      final ObjectId to = gitRepo.getRef(REF_HEAD);

      final List<GitCommit> gitCommits =
          gitRepo
              .getGitRepoData(
                  new RevisionBoundary<ObjectId>(from, InclusivenessStrategy.DEFAULT),
                  new RevisionBoundary<ObjectId>(to, InclusivenessStrategy.DEFAULT),
                  "No tag",
                  Optional.empty())
              .getGitCommits();

      assertThat(gitCommits).hasSize(1);
      assertThat(gitCommits.get(0).getMessageNotes()).isEmpty();
    }
  }

  @Test
  public void testThatChangedFilesArePopulatedOnCommits(@TempDir final File tempRepoDir)
      throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir)
                .write("first.txt", "first")
                .write("second.txt", "second")
                .commit("initial", "Initial commit")
                .write("first.txt", "first-updated")
                .write("third.txt", "third")
                .delete("second.txt")
                .commit("second", "Second commit");
        GitRepo gitRepo = repo.open()) {
      gitRepo.setCommitFiles(true);
      final ObjectId from = gitRepo.getCommit(ZERO_COMMIT);
      final ObjectId to = gitRepo.getRef(REF_HEAD);

      final List<GitCommit> gitCommits =
          gitRepo
              .getGitRepoData(
                  new RevisionBoundary<ObjectId>(from, InclusivenessStrategy.DEFAULT),
                  new RevisionBoundary<ObjectId>(to, InclusivenessStrategy.DEFAULT),
                  "No tag",
                  Optional.empty())
              .getGitCommits();

      final GitCommit initialCommit =
          gitCommits.stream()
              .filter(c -> c.getHash().equals(repo.hash("initial")))
              .findFirst()
              .get();
      final GitCommit secondCommit =
          gitCommits.stream()
              .filter(c -> c.getHash().equals(repo.hash("second")))
              .findFirst()
              .get();

      assertThat(initialCommit.getFiles()) //
          .containsExactly("first.txt", "second.txt");
      assertThat(secondCommit.getFiles()) //
          .containsExactly("first.txt", "second.txt", "third.txt");
    }
  }

  @Test
  public void testThatChangedFilesAreEmptyWhenCommitFilesIsNotEnabled(
      @TempDir final File tempRepoDir) throws Exception {
    try (TestGitRepo repo =
            TestGitRepo.in(tempRepoDir).commit("first", "Initial commit", "first.txt", "first");
        GitRepo gitRepo = repo.open()) {
      final ObjectId from = gitRepo.getCommit(ZERO_COMMIT);
      final ObjectId to = gitRepo.getRef(REF_HEAD);

      final List<GitCommit> gitCommits =
          gitRepo
              .getGitRepoData(
                  new RevisionBoundary<ObjectId>(from, InclusivenessStrategy.DEFAULT),
                  new RevisionBoundary<ObjectId>(to, InclusivenessStrategy.DEFAULT),
                  "No tag",
                  Optional.empty())
              .getGitCommits();

      assertThat(gitCommits).hasSize(1);
      assertThat(gitCommits.get(0).getFiles()).isEmpty();
    }
  }

  private List<String> messages(final List<GitCommit> gitCommits) {
    final List<String> messages = new ArrayList<>();
    for (final GitCommit gc : gitCommits) {
      messages.add(gc.getMessage().trim());
    }
    return messages;
  }

  private Map<String, GitTag> perTag(final List<GitTag> gitTags) {
    final Map<String, GitTag> map = new TreeMap<>();
    for (final GitTag gitTag : gitTags) {
      map.put(gitTag.getName(), gitTag);
    }
    return map;
  }
}
