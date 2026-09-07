package se.bjurr.gitchangelog.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;

import java.util.List;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogRepositoryException;

public class GitChangelogLibAssertions {

  private static final List<String> EXPECTED_URL_PARTS =
      List.of("git-changelog-lib", "tomasbjerre", "git@github.com");

  private static final String HOW_TO =
      "The testcases render this very repository and the approved files contain its origin URL,"
          + " so they need the main repository as origin, cloned over SSH.\n\n"
          + "  1. git clone git@github.com:tomasbjerre/git-changelog-lib.git\n"
          + "  2. git remote add yourfork git@github.com:yourfork/git-changelog-lib.git\n\n"
          + "And when pushing to the fork, do:\n\n"
          + "  1. git push -u yourfork feature/your-feature-branch\n";

  public static void assertHavingMainRepoAsOrigin() {
    final List<String> urlParts;
    try {
      urlParts = gitChangelogApiBuilder().getChangelog().getUrlParts();
    } catch (final GitChangelogRepositoryException e) {
      throw new RuntimeException(e.getMessage(), e);
    }

    if (urlParts.contains("github.com") && !urlParts.contains("git@github.com")) {
      fail(
          "Origin is an HTTPS clone of the repository, but the approved files were recorded with"
              + " an SSH origin. Switch it over with:\n\n"
              + "  git remote set-url origin git@github.com:tomasbjerre/git-changelog-lib.git\n\n"
              + HOW_TO);
    }

    assertThat(urlParts).as(HOW_TO).containsOnlyElementsOf(EXPECTED_URL_PARTS);
  }
}
