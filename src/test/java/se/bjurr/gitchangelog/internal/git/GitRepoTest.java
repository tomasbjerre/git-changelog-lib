package se.bjurr.gitchangelog.internal.git;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_MASTER;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import org.eclipse.jgit.lib.ObjectId;
import org.junit.Before;
import org.junit.Test;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

public class GitRepoTest {
  private static final String FIRST_COMMIT_HASH = "a1aa5ff";
  private static final String FIRST_COMMIT_HASH_FULL = "a1aa5ff5b625e63aa5ad7b59367ec7f75658afb8";
  private static final String TAG_1_0_HASH = "01484ce71bbc76e1af75ebb07a52844145ce99dc";
  private File gitRepoFile;

  @Before
  public void before() throws URISyntaxException {
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
                firstCommit, lastCommit, "No tag", Optional.of(".*tag-in-test-feature$"))
            .getGitCommits();
    assertThat(diff).isNotEmpty();
    Collections.reverse(diff);
    assertThat(diff.get(0).getHash()) //
        .as("first") //
        .startsWith("5aaeb90");
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
                firstCommit, lastCommit, "No tag", Optional.of(".*tag-in-test-feature$"))
            .getGitCommits();
    assertThat(diff.size()).isGreaterThan(10);
    Collections.reverse(diff);
    assertThat(diff.get(0).getHash()).startsWith("5aae");
  }

  @Test
  public void testThatCommitsBetweenZeroCommitAndCommitCanBeListed() throws Exception {
    final GitRepo gitRepo = this.getGitRepo();
    final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
    final ObjectId lastCommit = gitRepo.getCommit(TAG_1_0_HASH);
    final GitRepoData gitRepoData =
        gitRepo.getGitRepoData(
            firstCommit, lastCommit, "No tag", Optional.of(".*tag-in-test-feature$"));
    assertThat(gitRepoData.getGitCommits())
        .as("Commits in first release.") //
        .hasSize(5);
    assertThat(gitRepoData.getGitTags())
        .as("Tags in first release.") //
        .hasSize(1);
    final List<GitCommit> diff = gitRepoData.getGitCommits();
    Collections.reverse(diff);
    assertThat(diff.get(0).getHash()) //
        .startsWith("5aae");
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
        gitRepo.getGitRepoData(from, to, "No tag", Optional.<String>empty());
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
                firstRelease, secondRelease, "No tag", Optional.of(".*tag-in-test-feature$"))
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
                firstCommit, secondRelease, "No tag", Optional.of(".*tag-in-test-feature$"))
            .getGitCommits();
    assertThat(diff) //
        .as("Commits in second release from zero commit.") //
        .hasSize(13);
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
            firstCommit, lastCommit, "No tag", Optional.of(".*tag-in-test-feature$"));
    assertThat(gitRepoData.getGitCommits())
        .as("Commits in first release.") //
        .hasSize(15);

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
        gitRepo.getGitRepoData(from, to, "No tag", Optional.of(".*tag-in-test-feature$"));
    final Map<String, GitTag> perTag = this.perTag(gitRepoData.getGitTags());
    assertThat(gitRepoData.getGitTags()) //
        .hasSize(1);
    assertThat(gitRepoData.getGitTags()) //
        .hasSize(1);
    assertThat(perTag.keySet()) //
        .hasSize(1) //
        .contains("No tag");
    final GitTag noTagTag = perTag.get("No tag");

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
        gitRepo.getGitRepoData(from, to, "No tag", Optional.<String>empty());
    final Map<String, GitTag> perTag = this.perTag(gitRepoData.getGitTags());
    assertThat(perTag.keySet()) //
        .hasSize(2) //
        .containsExactly( //
            "No tag", //
            "refs/tags/tag-in-test-feature");
    final GitTag noTagTag = perTag.get("No tag");
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
        gitRepo.getGitRepoData(from, to, "No tag", Optional.<String>empty());
    final Map<String, GitTag> perTag = this.perTag(gitRepoData.getGitTags());
    assertThat(perTag.keySet()) //
        .hasSize(1) //
        .containsExactly( //
            "No tag");
    final GitTag noTagTag = perTag.get("No tag");
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
            gitRepo.getCommit(ZERO_COMMIT),
            gitRepo.getRef(REF_MASTER),
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
    gitRepo.setTreeFilter("src/");
    final ObjectId firstCommit = gitRepo.getCommit(ZERO_COMMIT);
    final ObjectId lastCommit = gitRepo.getRef("1.71");

    final GitRepoData gitRepoData =
        gitRepo.getGitRepoData(
            firstCommit, lastCommit, "No tag", Optional.of(".*tag-in-test-feature$"));

    final List<GitCommit> gitCommits = gitRepoData.getGitCommits();
    assertThat(gitCommits)
        .as("Commits in first release.") //
        .hasSize(104);

    assertThat(gitCommits.get(0).getHash()).startsWith("a394e04");
    Collections.reverse(gitCommits);
    assertThat(gitCommits.get(0).getHash()).startsWith("5aaeb907");
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
