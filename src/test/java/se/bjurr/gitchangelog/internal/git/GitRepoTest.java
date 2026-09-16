package se.bjurr.gitchangelog.internal.git;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_HEAD;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_MASTER;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import se.bjurr.gitchangelog.api.InclusivenessStrategy;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

public class GitRepoTest {
  private static final String FIRST_COMMIT_HASH = "a1aa5ff";
  private static final String FIRST_COMMIT_HASH_FULL = "a1aa5ff5b625e63aa5ad7b59367ec7f75658afb8";
  private static final String TAG_1_0_HASH = "01484ce71bbc76e1af75ebb07a52844145ce99dc";
  private File gitRepoFile;

  @BeforeEach
  public void before() throws Exception {
    this.gitRepoFile = new File(GitRepoTest.class.getResource("/github-issues.json").toURI());
  }

  @Test
  public void testThatCommitsBetweenCommitAndCommitCanBeListed() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    final ObjectId firstCommit = gitRepo.getCommit(FIRST_COMMIT_HASH_FULL);
    final ObjectId lastCommit = gitRepo.getCommit("e3766e2d4bc6d206475c5d2ed96b3f967a6e157e");
    final List<GitCommit> diff =
        gitRepo
            .getGitRepoData(
                new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
                new RevisionBoundary<ObjectId>(lastCommit, InclusivenessStrategy.DEFAULT),
                "No tag",
                Optional.of(".*tag-in-test-feature$"))
            .getGitCommits();
    assertThat(diff).isNotEmpty();
    Collections.reverse(diff);
    assertThat(diff.get(0).getHash()) //
        .as("first") //
        .startsWith("a1aa");
    Collections.reverse(diff);
    assertThat(diff.get(0).getHash()) //
        .as("last") //
        .startsWith("e3766e2d4bc6d20");
  }

  @Test
  public void testThatCommitsBetweenCommitAndReferenceCanBeListed() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
    final ObjectId lastCommit = gitRepo.getRef(REF_MASTER);
    final List<GitCommit> diff =
        gitRepo
            .getGitRepoData(
                new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
                new RevisionBoundary<ObjectId>(lastCommit, InclusivenessStrategy.DEFAULT),
                "No tag",
                Optional.of(".*tag-in-test-feature$"))
            .getGitCommits();
    assertThat(diff.size()).isGreaterThan(10);
    Collections.reverse(diff);
    assertThat(diff.get(0).getHash()).startsWith("a1aa");
  }

  @Test
  public void testThatCommitsBetweenZeroCommitAndCommitCanBeListed() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
    final ObjectId lastCommit = gitRepo.getCommit(TAG_1_0_HASH);
    final GitRepoData gitRepoData =
        gitRepo.getGitRepoData(
            new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
            new RevisionBoundary<ObjectId>(lastCommit, InclusivenessStrategy.DEFAULT),
            "No tag",
            Optional.of(".*tag-in-test-feature$"));
    assertThat(gitRepoData.getGitCommits())
        .as("Commits in first release.") //
        .hasSize(6);
    assertThat(gitRepoData.getGitTags())
        .as("Tags in first release.") //
        .hasSize(2);
    final List<GitCommit> diff = gitRepoData.getGitCommits();
    Collections.reverse(diff);
    assertThat(diff.get(0).getHash()) //
        .startsWith("a1aa");
  }

  @Test
  public void testThatCommitCountIsNullByDefault() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
    final ObjectId lastCommit = gitRepo.getCommit(TAG_1_0_HASH);
    final List<GitCommit> diff =
        gitRepo
            .getGitRepoData(
                new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
                new RevisionBoundary<ObjectId>(lastCommit, InclusivenessStrategy.DEFAULT),
                "No tag",
                Optional.of(".*tag-in-test-feature$"))
            .getGitCommits();
    assertThat(diff).allMatch(commit -> commit.getCommitCount() == null);
  }

  @Test
  public void testThatCommitCountCanBeComputedForTheFirstCommitInTheRepo() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    gitRepo.setCommitCount(true);
    final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
    final ObjectId lastCommit = gitRepo.getCommit(TAG_1_0_HASH);
    final List<GitCommit> diff =
        gitRepo
            .getGitRepoData(
                new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
                new RevisionBoundary<ObjectId>(lastCommit, InclusivenessStrategy.DEFAULT),
                "No tag",
                Optional.of(".*tag-in-test-feature$"))
            .getGitCommits();
    Collections.reverse(diff);
    // The very first commit ever made in the repo has exactly itself as an ancestor.
    assertThat(diff.get(0).getHash()).startsWith("a1aa");
    assertThat(diff.get(0).getCommitCount()).isEqualTo(1);
  }

  @Test
  public void testThatCommitsCanBeRetrieved() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    assertThat(gitRepo.getCommit(FIRST_COMMIT_HASH_FULL)).isNotNull();
    assertThat(gitRepo.getCommit(FIRST_COMMIT_HASH_FULL).name()).isEqualTo(FIRST_COMMIT_HASH_FULL);
    assertThat(gitRepo.getCommit(TAG_1_0_HASH)).isNotNull();
    assertThat(gitRepo.getCommit(TAG_1_0_HASH).name()).isEqualTo(TAG_1_0_HASH);
  }

  @Test
  public void testThatCommitsFromMergeNotInFromAreIncluded() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    final ObjectId from = gitRepo.getCommit("c5d37f5");
    final ObjectId to = gitRepo.getCommit("bb2f35b");

    final GitRepoData gitRepoData =
        gitRepo.getGitRepoData(
            new RevisionBoundary<ObjectId>(from, InclusivenessStrategy.DEFAULT),
            new RevisionBoundary<ObjectId>(to, InclusivenessStrategy.DEFAULT),
            "No tag",
            Optional.<String>empty());
    final List<String> noTagNames = this.messages(gitRepoData.getGitCommits());
    assertThat(noTagNames) //
        .containsExactly( //
            "Merge branch 'test-feature' into test", //
            "Some stuff in test-feature");
  }

  @Test
  public void testThatCommitsSecondReleaseCommitCanBeListed() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    final ObjectId firstRelease = gitRepo.getRef("refs/tags/1.0");
    final ObjectId secondRelease = gitRepo.getRef("refs/tags/1.1");
    List<GitCommit> diff =
        gitRepo
            .getGitRepoData(
                new RevisionBoundary<ObjectId>(firstRelease, InclusivenessStrategy.DEFAULT),
                new RevisionBoundary<ObjectId>(secondRelease, InclusivenessStrategy.DEFAULT),
                "No tag",
                Optional.of(".*tag-in-test-feature$"))
            .getGitCommits();
    assertThat(diff) //
        .as("Commits in second release from 1.0.") //
        .hasSize(8);
    assertThat(diff.get(7).getHash()) //
        .startsWith("3950c64");
    assertThat(diff.get(0).getHash()) //
        .startsWith(secondRelease.getName().substring(0, 10));

    final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
    diff =
        gitRepo
            .getGitRepoData(
                new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
                new RevisionBoundary<ObjectId>(secondRelease, InclusivenessStrategy.DEFAULT),
                "No tag",
                Optional.of(".*tag-in-test-feature$"))
            .getGitCommits();
    assertThat(diff) //
        .as("Commits in second release from zero commit.") //
        .hasSize(14);
    assertThat(diff.get(6).getHash()) //
        .startsWith("ba9d");
    assertThat(diff.get(0).getHash()) //
        .startsWith(secondRelease.getName().substring(0, 10));
  }

  @Test
  public void testThatMergeCommitsBetweenZeroCommitAndTestCanBeListed() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
    final ObjectId lastCommit = gitRepo.getRef("test");
    final GitRepoData gitRepoData =
        gitRepo.getGitRepoData(
            new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
            new RevisionBoundary<ObjectId>(lastCommit, InclusivenessStrategy.DEFAULT),
            "No tag",
            Optional.of(".*tag-in-test-feature$"));
    assertThat(gitRepoData.getGitCommits())
        .as("Commits in first release.") //
        .hasSize(16);

    final List<GitCommit> merges = new ArrayList<>();
    for (final GitCommit gitCommit : gitRepoData.getGitCommits()) {
      if (gitCommit.isMerge()) {
        merges.add(gitCommit);
      }
    }
    assertThat(merges).hasSize(1);
  }

  @Test
  public void testThatRepoCanBeFound() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    assertThat(gitRepo).isNotNull();
  }

  @Test
  public void testThatShortHashCanBeUsedToFindCommits() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    assertThat(gitRepo.getCommit("5a50ad3672c9f5a273c04711ed9b3daebf1f9b07").getName()) //
        .isEqualTo("5a50ad3672c9f5a273c04711ed9b3daebf1f9b07");
    assertThat(gitRepo.getCommit("5a50ad3").getName()) //
        .isEqualTo("5a50ad3672c9f5a273c04711ed9b3daebf1f9b07");
  }

  @Test
  public void testThatTagCanBeIgnored() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    final ObjectId from = gitRepo.getCommit("87c0d72888961712d4d63dd6298c24c1133a6b51");
    final ObjectId to = gitRepo.getRef("test");

    final GitRepoData gitRepoData =
        gitRepo.getGitRepoData(
            new RevisionBoundary<ObjectId>(from, InclusivenessStrategy.DEFAULT),
            new RevisionBoundary<ObjectId>(to, InclusivenessStrategy.DEFAULT),
            "No tag",
            Optional.of(".*tag-in-test-feature$"));
    final Map<String, GitTag> perTag = this.perTag(gitRepoData.getGitTags());
    assertThat(gitRepoData.getGitTags()) //
        .hasSize(1);
    assertThat(gitRepoData.getGitTags()) //
        .hasSize(1);
    assertThat(perTag.keySet()) //
        .hasSize(1) //
        .contains("refs/tags/test");
    final GitTag noTagTag = perTag.get("refs/tags/test");

    final List<String> noTagTagMessages = this.messages(noTagTag.getGitCommits());
    assertThat(noTagTagMessages) //
        .containsExactly( //
            "Some stuff in test again", //
            "Merge branch 'test-feature' into test", //
            "Some stuff in test-feature", //
            "some stuff in test branch");
  }

  @Test
  public void testThatTagInFeatureBranchAndMainBranchIsNotMixed() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    final ObjectId from = gitRepo.getCommit("87c0d72888961712d4d63dd6298c24c1133a6b51");
    final ObjectId to = gitRepo.getRef("test");

    final GitRepoData gitRepoData =
        gitRepo.getGitRepoData(
            new RevisionBoundary<ObjectId>(from, InclusivenessStrategy.DEFAULT),
            new RevisionBoundary<ObjectId>(to, InclusivenessStrategy.DEFAULT),
            "No tag",
            Optional.<String>empty());
    final Map<String, GitTag> perTag = this.perTag(gitRepoData.getGitTags());
    assertThat(perTag.keySet()) //
        .hasSize(2) //
        .containsExactly( //
            "refs/tags/tag-in-test-feature", //
            "refs/tags/test");
    final GitTag noTagTag = perTag.get("refs/tags/test");
    final List<String> noTagNames = this.messages(noTagTag.getGitCommits());
    assertThat(noTagNames) //
        .containsExactly( //
            "Some stuff in test again", //
            "Merge branch 'test-feature' into test", //
            "some stuff in test branch");

    final GitTag testFeatureTag = perTag.get("refs/tags/tag-in-test-feature");
    final List<String> testFeatureTagMessages = this.messages(testFeatureTag.getGitCommits());
    assertThat(testFeatureTagMessages) //
        .containsExactly( //
            "Some stuff in test-feature");
  }

  @Test
  public void
      testThatTagInFeatureBranchDoesNotIncludeNewUnmergedCommitsInItsMainBranchWhenFeatureLaterMerged()
          throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    final ObjectId from = gitRepo.getCommit("090e7f4");
    final ObjectId to = gitRepo.getCommit("8371342");

    final GitRepoData gitRepoData =
        gitRepo.getGitRepoData(
            new RevisionBoundary<ObjectId>(from, InclusivenessStrategy.DEFAULT),
            new RevisionBoundary<ObjectId>(to, InclusivenessStrategy.DEFAULT),
            "refs/tags/test",
            Optional.<String>empty());
    final Map<String, GitTag> perTag = this.perTag(gitRepoData.getGitTags());
    assertThat(perTag.keySet()) //
        .hasSize(1) //
        .containsExactly( //
            "refs/tags/test");
    final GitTag noTagTag = perTag.get("refs/tags/test");
    final List<String> noTagNames = this.messages(noTagTag.getGitCommits());
    assertThat(noTagNames) //
        .containsExactly( //
            "Some stuff in test again", //
            "Merge branch 'test-feature' into test", //
            "some stuff in test branch");
  }

  @Test
  public void testThatTagsCanBeListed() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    final GitRepoData gitRepoData =
        gitRepo.getGitRepoData(
            new RevisionBoundary<ObjectId>(
                gitRepo.getCommit(ZERO_COMMIT), InclusivenessStrategy.DEFAULT),
            new RevisionBoundary<ObjectId>(
                gitRepo.getRef(REF_MASTER), InclusivenessStrategy.DEFAULT),
            "No tag",
            Optional.of(".*tag-in-test-feature$"));
    assertThat(gitRepoData.getGitTags()).isNotEmpty();
  }

  @Test
  public void testThatZeroCommitCanBeRetrieved() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
    assertThat(firstCommit).as(gitRepo.toString()).isNotNull();
    assertThat(firstCommit.name()).as(gitRepo.toString()).startsWith(FIRST_COMMIT_HASH);
  }

  @Test
  public void testThatRepoFilterReducesTheNumberOfCommits() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    gitRepo.setPathFilters(Arrays.asList("src/"));
    final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
    final ObjectId lastCommit = gitRepo.getRef("1.71");

    final GitRepoData gitRepoData =
        gitRepo.getGitRepoData(
            new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
            new RevisionBoundary<ObjectId>(lastCommit, InclusivenessStrategy.DEFAULT),
            "No tag",
            Optional.of(".*tag-in-test-feature$"));

    final List<GitCommit> gitCommits = gitRepoData.getGitCommits();
    assertThat(gitCommits)
        .as("Commits in first release.") //
        .hasSize(105);

    assertThat(gitCommits.get(0).getHash()).startsWith("a394e04");
    Collections.reverse(gitCommits);
    assertThat(gitCommits.get(0).getHash()).startsWith("a1aa");
  }

  @Test
  public void testThatRepoFilterReducesTheNumberOfCommitsUsingPathFilters() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    gitRepo.setPathFilters(Arrays.asList("src/"));
    final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
    final ObjectId lastCommit = gitRepo.getRef("1.71");

    final GitRepoData gitRepoData =
        gitRepo.getGitRepoData(
            new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
            new RevisionBoundary<ObjectId>(lastCommit, InclusivenessStrategy.DEFAULT),
            "No tag",
            Optional.of(".*tag-in-test-feature$"));

    final List<GitCommit> gitCommits = gitRepoData.getGitCommits();
    assertThat(gitCommits)
        .as("Commits in first release.") //
        .hasSize(105);

    assertThat(gitCommits.get(0).getHash()).startsWith("a394e04");
    Collections.reverse(gitCommits);
    assertThat(gitCommits.get(0).getHash()).startsWith("a1aa");
  }

  @Test
  public void testThatRepoFilterIncreasesTheNumberOfCommitsUsingMultiplePathFilters()
      throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    gitRepo.setPathFilters(Arrays.asList("src/", "examples/"));
    final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
    final ObjectId lastCommit = gitRepo.getRef("1.71");

    final GitRepoData gitRepoData =
        gitRepo.getGitRepoData(
            new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
            new RevisionBoundary<ObjectId>(lastCommit, InclusivenessStrategy.DEFAULT),
            "No tag",
            Optional.of(".*tag-in-test-feature$"));

    final List<GitCommit> gitCommits = gitRepoData.getGitCommits();
    assertThat(gitCommits)
        .as("Commits in first release.") //
        .hasSize(106);

    assertThat(gitCommits.get(0).getHash()).startsWith("a394e04");
    Collections.reverse(gitCommits);
    assertThat(gitCommits.get(0).getHash()).startsWith("a1aa");
  }

  @Test
  public void
      testThatRepoFilterIncreasesTheNumberOfCommitsUsingBothPathFilterAndMultiplePathFilters()
          throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    gitRepo.setPathFilters(Arrays.asList("src/", "examples/"));
    final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
    final ObjectId lastCommit = gitRepo.getRef("1.71");

    final GitRepoData gitRepoData =
        gitRepo.getGitRepoData(
            new RevisionBoundary<ObjectId>(firstCommit, InclusivenessStrategy.DEFAULT),
            new RevisionBoundary<ObjectId>(lastCommit, InclusivenessStrategy.DEFAULT),
            "No tag",
            Optional.of(".*tag-in-test-feature$"));

    final List<GitCommit> gitCommits = gitRepoData.getGitCommits();
    assertThat(gitCommits)
        .as("Commits in first release.") //
        .hasSize(106);

    assertThat(gitCommits.get(0).getHash()).startsWith("a394e04");
    Collections.reverse(gitCommits);
    assertThat(gitCommits.get(0).getHash()).startsWith("a1aa");
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
    RevCommit untaggedCommit;
    try (Git git = Git.init().setDirectory(tempRepoDir).call()) {
      final File file1 = new File(tempRepoDir, "file1.txt");
      java.nio.file.Files.write(file1.toPath(), "content".getBytes());
      git.add().addFilepattern("file1.txt").call();
      final RevCommit taggedCommit = git.commit().setMessage("Tagged commit").call();
      git.tag().setName("v1.0").setObjectId(taggedCommit).call();

      final File file2 = new File(tempRepoDir, "file2.txt");
      java.nio.file.Files.write(file2.toPath(), "more content".getBytes());
      git.add().addFilepattern("file2.txt").call();
      untaggedCommit = git.commit().setMessage("Untagged commit").call();
    }

    try (GitRepo gitRepo = new GitRepo(tempRepoDir)) {
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
      assertThat(untaggedGroup.getTagTime()).isNotNull();
      assertThat(untaggedGroup.getTagTime().getTime() / 1000)
          .isEqualTo(untaggedCommit.getCommitTime());
    }
  }

  @Test
  public void testThatGitNotesArePopulatedOnCommits(@TempDir final File tempRepoDir)
      throws Exception {
    RevCommit revCommit;
    try (Git git = Git.init().setDirectory(tempRepoDir).call()) {
      final File file = new File(tempRepoDir, "file.txt");
      java.nio.file.Files.write(file.toPath(), "content".getBytes());
      git.add().addFilepattern("file.txt").call();
      revCommit = git.commit().setMessage("Some commit").call();
      git.notesAdd().setObjectId(revCommit).setMessage("This is a note\non the commit").call();
    }

    try (GitRepo gitRepo = new GitRepo(tempRepoDir)) {
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
    try (Git git = Git.init().setDirectory(tempRepoDir).call()) {
      final File file = new File(tempRepoDir, "file.txt");
      java.nio.file.Files.write(file.toPath(), "content".getBytes());
      git.add().addFilepattern("file.txt").call();
      git.commit().setMessage("Some commit").call();
    }

    try (GitRepo gitRepo = new GitRepo(tempRepoDir)) {
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
    try (Git git = Git.init().setDirectory(tempRepoDir).call()) {
      java.nio.file.Files.write(new File(tempRepoDir, "first.txt").toPath(), "first".getBytes());
      java.nio.file.Files.write(new File(tempRepoDir, "second.txt").toPath(), "second".getBytes());
      git.add().addFilepattern(".").call();
      git.commit().setMessage("Initial commit").call();

      java.nio.file.Files.write(
          new File(tempRepoDir, "first.txt").toPath(), "first-updated".getBytes());
      java.nio.file.Files.write(new File(tempRepoDir, "third.txt").toPath(), "third".getBytes());
      git.add().addFilepattern(".").call();
      git.rm().addFilepattern("second.txt").call();
      git.commit().setMessage("Second commit").call();
    }

    try (GitRepo gitRepo = new GitRepo(tempRepoDir)) {
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

      assertThat(gitCommits).hasSize(2);
      final GitCommit initialCommit =
          gitCommits.stream()
              .filter(c -> c.getMessage().trim().equals("Initial commit"))
              .findFirst()
              .get();
      final GitCommit secondCommit =
          gitCommits.stream()
              .filter(c -> c.getMessage().trim().equals("Second commit"))
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
    try (Git git = Git.init().setDirectory(tempRepoDir).call()) {
      java.nio.file.Files.write(new File(tempRepoDir, "first.txt").toPath(), "first".getBytes());
      git.add().addFilepattern(".").call();
      git.commit().setMessage("Initial commit").call();
    }

    try (GitRepo gitRepo = new GitRepo(tempRepoDir)) {
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

  private GitRepo getGitRepo() throws Exception {
    return new GitRepo(this.gitRepoFile);
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
