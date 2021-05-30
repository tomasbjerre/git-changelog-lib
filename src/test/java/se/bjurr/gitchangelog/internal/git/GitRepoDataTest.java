package se.bjurr.gitchangelog.internal.git;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

public class GitRepoDataTest {

  public GitRepoData newGitRepoData(final String originUrl) {
    final List<GitTag> gitTags = new ArrayList<>();
    return new GitRepoData(originUrl, gitTags);
  }

  @Test
  public void testGitHubRepo() {
    final GitRepoData sut = this.newGitRepoData("git@github.com:tomasbjerre/git-changelog-lib.git");

    assertThat(sut.findOwnerName().orElse(null)) //
        .isEqualTo("tomasbjerre");
    assertThat(sut.findRepoName().orElse(null)) //
        .isEqualTo("git-changelog-lib");

    assertThat(sut.findGitHubApi().orElse(null)) //
        .isEqualTo("https://api.github.com/repos/tomasbjerre/git-changelog-lib");
    assertThat(sut.findGitLabServer().orElse(null)) //
        .isEqualTo(null);
  }

  @Test
  public void testGitHubRepoTwoDots() {
    final GitRepoData sut =
        this.newGitRepoData("git@github.com:tomasbjerre/git-changelog-lib-1.0.git");

    assertThat(sut.findOwnerName().orElse(null)) //
        .isEqualTo("tomasbjerre");
    assertThat(sut.findRepoName().orElse(null)) //
        .isEqualTo("git-changelog-lib-1.0");

    assertThat(sut.findGitHubApi().orElse(null)) //
        .isEqualTo("https://api.github.com/repos/tomasbjerre/git-changelog-lib-1.0");
    assertThat(sut.findGitLabServer().orElse(null)) //
        .isEqualTo(null);
  }

  @Test
  public void testGitHubRepoNoDotGit() {
    final GitRepoData sut = this.newGitRepoData("https://github.com/VoltzEngine-Project/Build");

    assertThat(sut.findOwnerName().orElse(null)) //
        .isEqualTo("VoltzEngine-Project");
    assertThat(sut.findRepoName().orElse(null)) //
        .isEqualTo("Build");

    assertThat(sut.findGitHubApi().orElse(null)) //
        .isEqualTo("https://api.github.com/repos/VoltzEngine-Project/Build");
    assertThat(sut.findGitLabServer().orElse(null)) //
        .isEqualTo(null);
  }

  @Test
  public void testGitBitbucketRepo() {
    final GitRepoData sut =
        this.newGitRepoData(
            "https://tomasbjerre@bitbucket.org/ljohansson/grunt-connect-apimock.git");

    assertThat(sut.findOwnerName().orElse(null)) //
        .isEqualTo("ljohansson");
    assertThat(sut.findRepoName().orElse(null)) //
        .isEqualTo("grunt-connect-apimock");

    assertThat(sut.findGitHubApi().orElse(null)) //
        .isEqualTo(null);
    assertThat(sut.findGitLabServer().orElse(null)) //
        .isEqualTo(null);
  }

  @Test
  public void testGitLabRepo() {
    final GitRepoData sut = this.newGitRepoData("http://root@gitlab.com/root/violations-test.git");

    assertThat(sut.findOwnerName().orElse(null)) //
        .isEqualTo("root");
    assertThat(sut.findRepoName().orElse(null)) //
        .isEqualTo("violations-test");

    assertThat(sut.findGitHubApi().orElse(null)) //
        .isEqualTo(null);
    assertThat(sut.findGitLabServer().orElse(null)) //
        .isEqualTo("https://gitlab.com/");
  }
}
