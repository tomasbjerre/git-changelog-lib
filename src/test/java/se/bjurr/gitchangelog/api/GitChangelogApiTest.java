package se.bjurr.gitchangelog.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;
import static se.bjurr.gitchangelog.internal.integrations.rest.RestClient.mock;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogRepositoryException;
import se.bjurr.gitchangelog.internal.integrations.jira.JiraClientFactory;
import se.bjurr.gitchangelog.internal.integrations.redmine.RedmineClientFactory;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClientMock;
import se.bjurr.gitchangelog.test.ApprovalsWrapper;
import se.bjurr.gitchangelog.test.TestGitRepo;

/**
 * Every test here renders against the one synthetic repo built in {@link #before}, instead of this
 * project's own git history - so a reader doesn't need to cross-reference a real `git log` to know
 * what "tag 1.0" or "the test branch" actually contain. See {@link #before} for the shape of that
 * repo and why each commit/tag exists.
 */
public class GitChangelogApiTest {

  private static final String JIRA_ISSUE_FIELDS =
      "fields=parent,summary,issuetype,labels,description,issuelinks";
  private static final String JIRA_BASE_PATH = "/jira/rest/api/2";

  /** A GitHub issue number present in /github-issues.json, used to exercise GitHub integration. */
  private static final String GITHUB_ISSUE = "#80";

  private static final String JIRA_ISSUE_1 = "JIR-1234";
  private static final String JIRA_ISSUE_2 = "JIR-5262";

  private RestClientMock mockedRestClient;
  private TestGitRepo repo;

  @BeforeEach
  public void before(@TempDir final File repoDir) throws Exception {
    JiraClientFactory.reset();
    RedmineClientFactory.reset();

    this.mockedRestClient = new RestClientMock();
    this.mockedRestClient //
        .addMockedResponse(
            "/repos/tomasbjerre/git-changelog-lib/issues?state=all&per_page=100&page=1",
            new String(
                Files.readAllBytes(
                    Paths.get(TemplatesTest.class.getResource("/github-issues.json").toURI())),
                UTF_8)) //
        .addMockedResponse(
            JIRA_BASE_PATH + "/issue/" + JIRA_ISSUE_1 + "?" + JIRA_ISSUE_FIELDS,
            new String(
                Files.readAllBytes(
                    Paths.get(
                        TemplatesTest.class.getResource("/jira-issue-jir-1234.json").toURI())),
                UTF_8)) //
        .addMockedResponse(
            JIRA_BASE_PATH + "/issue/" + JIRA_ISSUE_2 + "?" + JIRA_ISSUE_FIELDS,
            new String(
                Files.readAllBytes(
                    Paths.get(
                        TemplatesTest.class.getResource("/jira-issue-jir-5262.json").toURI())),
                UTF_8)) //
        .addMockedResponse(
            "/redmine/issues/1234.json?null",
            new String(
                Files.readAllBytes(
                    Paths.get(TemplatesTest.class.getResource("/redmine-issue-1234.json").toURI())),
                UTF_8)); //
    mock(this.mockedRestClient);

    // The GitHub/Jira REST calls above are matched by path only (host-agnostic), so the
    // "origin" remote below only needs to make {{ownerName}}/{{repoName}} render sensibly in
    // templates that use them - it plays no part in routing the mocked responses.
    this.repo =
        TestGitRepo.in(repoDir) //
            .remote("origin", "git@github.com:tomasbjerre/git-changelog-lib.git")
            // First release: a handful of plain commits, tagged 0.0.1 on the very first one.
            .commit("root", "Initial commit", "README.md", "hello")
            .tag("0.0.1", "root")
            .commit("feature-a", "Add feature A", "file.txt", "a")
            .commit("fix-a", "Fix crash in feature A", "file.txt", "a-fixed")
            .commit("docs", "Update documentation", "README.md", "hello world")
            // Second release, tagged 1.0.
            .commit("feature-b", "Add feature B", "file.txt", "b")
            .tag("1.0", "feature-b")
            // A commit that gets reverted right after - both should disappear from output.
            .commit("experimental", "Add experimental feature", "experiment.txt", "x");
    this.repo
        .delete("experiment.txt")
        .commit(
            "revert-experimental",
            "Revert \"Add experimental feature\"\n\nThis reverts commit "
                + this.repo.hash("experimental")
                + ".\n")
        // A commit referencing a GitHub issue that exists in /github-issues.json.
        .commit("github-issue-fix", "Fix crash reported in " + GITHUB_ISSUE, "file.txt", "c")
        // Two commits referencing the mocked Jira issues.
        .commit("jira-support", "Implement support for " + JIRA_ISSUE_1, "file.txt", "d")
        .commit("jira-followup", "Follow-up work for " + JIRA_ISSUE_2, "file.txt", "e")
        // A commit with no recognizable issue reference at all, used by the
        // ignoreCommitsWithoutIssue tests to prove such commits (and tags left empty by their
        // removal) are dropped.
        .commit("no-issue", "Tidy up formatting", "file.txt", "f")
        // The "test" tag: the shared target ref for most of the issue/tag-related tests below.
        .commit("release-test", "Improve performance", "file.txt", "g")
        .tag("test", "release-test")
        // Two more commits, one referencing each of GitHub/Jira again, both under src/ - used by
        // the path-filter and "only githuh/jira issues" tests.
        .commit(
            "src-github",
            "Add feature C, fixes " + GITHUB_ISSUE,
            "src/FeatureC.java",
            "class FeatureC {}")
        .commit(
            "src-jira",
            "Add feature D, implements " + JIRA_ISSUE_1,
            "src/FeatureD.java",
            "class FeatureD {}")
        // A commit outside src/, tagged 2.0 - the path filter must exclude this one.
        .commit("docs-update", "Update non-src file", "docs/readme.txt", "docs")
        .tag("2.0", "docs-update")
        // An untagged branch, for testIssue182: rendering from ZERO_COMMIT to a plain branch
        // (not a tag) must not crash, and the "Unreleased" bucket the template hides must not
        // leak any of the tagged commits above into it.
        .branch("unreleased-work")
        .checkout("unreleased-work")
        .commit("wip", "Experiment with a new approach", "wip.txt", "wip")
        .checkout("master");
  }

  @AfterEach
  public void after() throws IOException {
    JiraClientFactory.reset();
    RedmineClientFactory.reset();
    mock(null);
    this.repo.close();
  }

  @Test
  public void testIssue182() throws Exception {
    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withTemplatePath("changelog-with-unreleased.mustache")
            .withFromRevision(ZERO_COMMIT) //
            .withToRevision("unreleased-work") //
            .withFromRepo(this.repo.dir());

    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testThatFirstVersionCanBeGenerated() throws Exception {
    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withFromCommit(ZERO_COMMIT) //
            .withToCommit("0.0.1") //
            .withFromRepo(this.repo.dir());

    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testThatSecondVersionCanBeGenerated() throws Exception {
    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("1.0") //
            .withFromRepo(this.repo.dir());

    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testThatTagsThatAreEmptyAfterCommitsHaveBeenIgnoredAreRemoved() throws Exception {
    final String templatePath = "templatetest/testAuthorsCommitsExtended.mustache";

    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withJiraEnabled(true)
            .withGitHubEnabled(true)
            .withGitLabEnabled(true)
            .withRedmineEnabled(true)
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withTemplatePath(templatePath) //
            .withIgnoreCommitsWithMessage(".*") //
            .withFromRepo(this.repo.dir());

    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testPathFilterCanBeSpecified() throws Exception {
    final String templatePath = "templatetest/testAuthorsCommitsExtended.mustache";

    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withJiraEnabled(true)
            .withGitHubEnabled(true)
            .withGitLabEnabled(true)
            .withRedmineEnabled(true)
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("2.0") //
            .withTemplatePath(templatePath) //
            .withPathFilters("src") //
            .withFromRepo(this.repo.dir());

    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testPathFiltersCanBeSpecified() throws Exception {
    final String templatePath = "templatetest/testAuthorsCommitsExtended.mustache";

    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withJiraEnabled(true)
            .withGitHubEnabled(true)
            .withGitLabEnabled(true)
            .withRedmineEnabled(true)
            .withFromRevision(ZERO_COMMIT)
            .withToRevision("2.0") //
            .withTemplatePath(templatePath) //
            .withPathFilters("src") //
            .withFromRepo(this.repo.dir());

    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testThatIssuesCanBeRemoved() throws Exception {

    final URL settingsFile =
        GitChangelogApiTest.class
            .getResource("/settings/git-changelog-test-settings.json")
            .toURI()
            .toURL();
    final String templatePath = "templatetest/testIssuesCommits.mustache";

    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withJiraEnabled(true)
            .withGitHubEnabled(true)
            .withGitLabEnabled(true)
            .withRedmineEnabled(true)
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withSettings(settingsFile) //
            .withUseIntegrations(true)
            .withRemoveIssueFromMessageArgument(true) //
            .withTemplatePath(templatePath) //
            .withFromRepo(this.repo.dir());

    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testThatCommitsWithoutIssueCanBeIgnoredIssuesCommits() throws Exception {

    final URL settingsFile =
        GitChangelogApiTest.class
            .getResource("/settings/git-changelog-test-settings.json")
            .toURI()
            .toURL();
    final String templatePath = "templatetest/testIssuesCommits.mustache";

    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withJiraEnabled(true)
            .withGitHubEnabled(true)
            .withGitLabEnabled(true)
            .withRedmineEnabled(true)
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withSettings(settingsFile) //
            .withUseIntegrations(true)
            .withIgnoreCommitsWithoutIssue(true) //
            .withTemplatePath(templatePath) //
            .withFromRepo(this.repo.dir());

    ApprovalsWrapper.verify(given);
  }

  /** "no-issue" (see {@link #before}) has no recognizable issue reference, so it's dropped. */
  @Test
  public void testThatCommitsWithoutIssueCanBeIgnoredTagsIssuesCommits() throws Exception {

    final String templatePath =
        "templatetest/testThatCommitsWithoutIssueCanBeIgnoredTagsIssuesCommits.mustache";

    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withJiraEnabled(true)
            .withGitHubEnabled(true)
            .withGitLabEnabled(true)
            .withRedmineEnabled(true)
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withGitHubIssuePattern("nooo") //
            .withGitLabIssuePattern("nooo") //
            .withRedmineIssuePattern("nooo") //
            .withCustomIssue(
                "JIRA", "JIR-[0-9]*", "http://${PATTERN_GROUP}", "${PATTERN_GROUP}") //
            .withIgnoreCommitsWithoutIssue(true) //
            .withTemplatePath(templatePath) //
            .withFromRepo(this.repo.dir());

    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testThatReadableGroupMustExist() throws Exception {
    final URL settingsFile =
        GitChangelogApiTest.class
            .getResource("/settings/git-changelog-test-settings.json")
            .toURI()
            .toURL();
    final String templatePath = "templatetest/testIssuesCommits.mustache";

    try {
      final String actual =
          gitChangelogApiBuilder() //
              .withFromCommit(ZERO_COMMIT) //
              .withSettings(settingsFile) //
              .withToRef("test") //
              .withRemoveIssueFromMessageArgument(true) //
              .withTemplatePath(templatePath) //
              .withReadableTagName("[0-9]+?") //
              .withFromRepo(this.repo.dir()) //
              .render();
      assertThat(actual) //
          .as(
              "Should never happen! But nice to see what was rendered, if it does not crash as expected.") //
          .isEqualTo("");
    } catch (final Exception e) {
      // Every tag in range (0.0.1, 1.0, test) contains a digit, so "[0-9]+?" - which has no
      // capturing group - fails on whichever one is processed first. Which tag that is isn't
      // part of the behavior under test, so only the fixed part of the message is checked.
      assertThat(e.getMessage()) //
          .startsWith("Pattern: \"[0-9]+?\" did not match any group in: \"refs/tags/");
    }
  }

  @Test
  public void testThatReadableGroupCanBeSet() throws Exception {
    final URL settingsFile =
        GitChangelogApiTest.class
            .getResource("/settings/git-changelog-test-settings.json")
            .toURI()
            .toURL();
    final String templatePath = "templatetest/testIssuesCommits.mustache";

    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withJiraEnabled(true)
            .withGitHubEnabled(true)
            .withGitLabEnabled(true)
            .withRedmineEnabled(true)
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withSettings(settingsFile) //
            .withUseIntegrations(true)
            .withRemoveIssueFromMessageArgument(true) //
            .withTemplatePath(templatePath) //
            .withReadableTagName(".*/([0-9]+?\\.[0-9]+?)$") //
            .withFromRepo(this.repo.dir());

    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testThatCustomVariablesCanBeUsed() throws Exception {
    final URL settingsFile =
        GitChangelogApiTest.class
            .getResource("/settings/git-changelog-test-settings.json")
            .toURI()
            .toURL();
    final String templatePath = "templatetest/testAuthorsCommitsExtended.mustache";

    final Map<String, Object> map = new HashMap<>();
    map.put("customVariable", "the value");
    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withSettings(settingsFile) //
            .withExtendedVariables(map) //
            .withRemoveIssueFromMessageArgument(true) //
            .withTemplatePath(templatePath) //
            .withFromRepo(this.repo.dir());

    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testThatRevertedCommitsAreRemoved() throws Exception {
    final String templatePath = "templatetest/testThatRevertedCommitsAreRemoved.mustache";

    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withJiraEnabled(true)
            .withGitHubEnabled(true)
            .withGitLabEnabled(true)
            .withRedmineEnabled(true)
            .withFromCommit(this.repo.hash("feature-b")) //
            .withToCommit(this.repo.hash("github-issue-fix")) //
            .withTemplatePath(templatePath) //
            .withFromRepo(this.repo.dir());

    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testThatFileCanBeSupplied() throws Exception {
    final String templatePath = "templatetest/testThatRevertedCommitsAreRemoved.mustache";

    final Path path = Paths.get("build", "testdirtocreate", "testThatFileCanBeSupplied.md");
    gitChangelogApiBuilder() //
        .withFromCommit(this.repo.hash("feature-b")) //
        .withToCommit(this.repo.hash("github-issue-fix")) //
        .withTemplatePath(templatePath) //
        .withFromRepo(this.repo.dir()) //
        .toFile(path.toFile());

    assertThat(path.toFile()).exists().isFile();
  }

  @Test
  public void testThatOnlyGithubIssuesCanBeParsed() throws Exception {
    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withGitHubEnabled(true)
            .withUseIntegrations(true)
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("2.0") //
            .withPathFilters("src")
            .withIgnoreCommitsWithoutIssue(true) //
            .withFromRepo(this.repo.dir());

    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testThatOnlyJiraIssuesCanBeParsed() throws Exception {
    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withJiraEnabled(true)
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("2.0") //
            .withPathFilters("src")
            .withIgnoreCommitsWithoutIssue(true) //
            .withFromRepo(this.repo.dir());

    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testThatISO88591ChangelogsCanBeUpdated() throws Exception {
    final Charset charsetToTest = StandardCharsets.ISO_8859_1;

    final byte[] renderedContent = this.renderWithEncoding(charsetToTest);

    assertThat(renderedContent)
        .as(new String(renderedContent, charsetToTest))
        .containsExactly(
            new byte[] { //
              -27, -28, -10, //
              -27, -28, -10
            });
  }

  @Test
  public void testThatUTF8ChangelogsCanBeUpdated() throws Exception {
    final Charset charsetToTest = StandardCharsets.UTF_8;

    final byte[] renderedContent = this.renderWithEncoding(charsetToTest);

    assertThat(renderedContent)
        .as(new String(renderedContent, charsetToTest))
        .containsExactly(
            new byte[] { //
              -61, -91, //
              -61, -92, //
              -61, -74, //
              -61, -91, //
              -61, -92, //
              -61, -74 //
            });
  }

  private byte[] renderWithEncoding(final Charset charsetToTest)
      throws IOException, GitChangelogRepositoryException {
    final byte[] aaoIso = "åäö".getBytes(charsetToTest);
    final Path isoFile1 =
        Paths.get("src/test/resources/tmptestfile-" + charsetToTest.name() + "-file-1.txt");
    Files.write(isoFile1, aaoIso);
    final Path isoFile2 =
        Paths.get("src/test/resources/tmptestfile-" + charsetToTest.name() + "-file-2.txt");
    Files.write(isoFile2, aaoIso);

    gitChangelogApiBuilder()
        .withEncoding(charsetToTest)
        .withFromCommit(ZERO_COMMIT)
        .withToRef("2.0")
        .withPrependTemplatePath(isoFile1.toFile().getAbsolutePath())
        .withFromRepo(this.repo.dir())
        .prependToFile(isoFile2.toFile());

    final byte[] renderedContent = Files.readAllBytes(isoFile2);
    return renderedContent;
  }
}
