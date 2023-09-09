package se.bjurr.gitchangelog.test;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;

import se.bjurr.gitchangelog.api.exceptions.GitChangelogRepositoryException;

public class GitChangelogLibAssertions {

  public static void assertHavingMainRepoAsOrigin() {

    try {
      assertThat(gitChangelogApiBuilder().getChangelog().getUrlParts())
          .as(
              "The testcases needs to run having the main repository as origin.\n"
                  + "So you need to clone the main repository and add your fork as another origin.\n\n"
                  + "  1. git clone git@github.com:tomasbjerre/git-changelog-lib.git\n"
                  + "  2. git remote add yourfork git@github.com:yourfork/git-changelog-lib.git\n\n"
                  + "And when pushing to the fork, do:\n\n"
                  + "  1. git push -u yourfork feature/your-feature-branch\n")
          .containsOnly("git-changelog-lib", "tomasbjerre", "git@github.com");
    } catch (GitChangelogRepositoryException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
