package se.bjurr.gitchangelog.api.helpers;

import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_MASTER;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.io.File;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import se.bjurr.gitchangelog.api.GitChangelogApi;
import se.bjurr.gitchangelog.test.TestGitRepo;

/**
 * Base for the conventional-commit Handlebars helper tests. The repo built in {@link #before}
 * reuses - verbatim - the same 15 conventional-commit messages (feat/fix/refactor/breaking-change/
 * revert/footers/paragraphs/scopes) this suite has exercised for years; they're just example text,
 * not tied to any real commit, so reusing them keeps every helper's parsing behavior proven rather
 * than reconstructed from scratch.
 */
abstract class AbstractHandlebarsHelperTest {
  protected GitChangelogApi baseBuilder;
  protected TestGitRepo repo;

  @BeforeEach
  public void before(@TempDir final File repoDir) throws Exception {
    this.repo =
        TestGitRepo.in(repoDir) //
            .commit("c1", "fix: dont use integrations when determining versions", "f", "1")
            .commit("c2", "New version: 1.147.1 [GRADLE SCRIPT]", "f", "2")
            .tag("1.147.1", "c2")
            .commit("c3", "Updating changelog with 1.147.1 [GRADLE SCRIPT]", "f", "3")
            .commit("c4", "fix: dont require semantic patterns to get highest tag", "f", "4")
            .commit("c5", "New version: 1.147.2 [GRADLE SCRIPT]", "f", "5")
            .tag("1.147.2", "c5")
            .commit("c6", "doing change 2", "f", "6")
            .commit("c7", "feat(utils): more utils", "f", "7")
            .commit("c8", "fix(utils:mix): the description (fixes ABC-123)", "f", "8")
            .commit(
                "c9",
                "Revert \"[Gradle Release Plugin] - pre tag commit:  '1.94'.\"\n\n"
                    + "This reverts commit 1edc0d71eccce51abfb5f62fdddfbe73913785f5.",
                "f",
                "9")
            .commit("c10", "refactor!: doing major stuff", "f", "10")
            .commit(
                "c11",
                "refactor!: using both ! and br\n\n"
                    + "BREAKING CHANGE: refactor to use JavaScript features not available in"
                    + " Node 6.",
                "f",
                "11")
            .commit(
                "c12",
                "refactor: using only br\n\n"
                    + "BREAKING CHANGE: refactor to use JavaScript features not available in"
                    + " Node 6.",
                "f",
                "12")
            .commit(
                "c13",
                "feat: allow provided config object to extend other configs\n\n"
                    + "BREAKING CHANGE: `extends` key in config file is now used for extending"
                    + " other config files",
                "f",
                "13")
            .commit(
                "c14",
                "fix: correct minor typos in code\n\n"
                    + "see the issue for details\n\n"
                    + "on typos fixed.\n\n"
                    + "Reviewed-by: Z\n"
                    + "Refs #133",
                "f",
                "14")
            .commit(
                "c15",
                "feat: dsc\n\n"
                    + "paragraph first line of two here!\n"
                    + "second line of first paragraph\n\n"
                    + "second paragraph the only line\n\n"
                    + "third paragraph first line of two\n"
                    + "and second line of third paragraph.\n\n"
                    + "first-token-here: value of first token\n"
                    + "second line of first token\n"
                    + "second-token-here: value of second token",
                "f",
                "15");

    this.baseBuilder =
        gitChangelogApiBuilder() //
            .withUseIntegrations(false)
            .withJiraEnabled(true)
            .withGitHubEnabled(true)
            .withGitLabEnabled(true)
            .withRedmineEnabled(true)
            .withFromRepo(this.repo.dir()) //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef(REF_MASTER);
  }

  @AfterEach
  public void after() throws Exception {
    this.repo.close();
  }
}
