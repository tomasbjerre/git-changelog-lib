package se.bjurr.gitchangelog.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;
import static se.bjurr.gitchangelog.internal.integrations.rest.RestClient.mock;

import java.io.File;
import java.io.IOException;
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
import se.bjurr.gitchangelog.api.model.Changelog;
import se.bjurr.gitchangelog.api.model.Commit;
import se.bjurr.gitchangelog.api.model.Issue;
import se.bjurr.gitchangelog.api.model.Tag;
import se.bjurr.gitchangelog.internal.integrations.jira.JiraClientFactory;
import se.bjurr.gitchangelog.internal.integrations.redmine.RedmineClientFactory;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClientMock;
import se.bjurr.gitchangelog.test.TestGitRepo;

/**
 * Every test here renders against the one synthetic repo built in {@link #before}, instead of this
 * project's own git history - so a reader doesn't need to cross-reference a real `git log` to know
 * what "tag 1.0" or "the test branch" actually contain. See {@link #before} for the shape of that
 * repo and why each commit/tag exists.
 *
 * <p>Every test asserts directly on {@link GitChangelogApi#getChangelog()} (or a rendered string,
 * for the handful where the render itself is the point) rather than approving a full rendered
 * template: each one has one narrow, nameable behavior to prove (a tag disappearing once empty, a
 * revert pair vanishing, a path filter narrowing the commit set, ...), and a direct assertion says
 * so, where a multi-hundred-line snapshot would bury it. Also only pull in the
 * jira/github/gitlab/redmine/settings-file configuration a given test actually needs - not the full
 * set every other test happens to use.
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
                    Paths.get(
                        GitChangelogApiTest.class.getResource("/github-issues.json").toURI())),
                UTF_8)) //
        .addMockedResponse(
            JIRA_BASE_PATH + "/issue/" + JIRA_ISSUE_1 + "?" + JIRA_ISSUE_FIELDS,
            new String(
                Files.readAllBytes(
                    Paths.get(
                        GitChangelogApiTest.class
                            .getResource("/jira-issue-jir-1234.json")
                            .toURI())),
                UTF_8)) //
        .addMockedResponse(
            JIRA_BASE_PATH + "/issue/" + JIRA_ISSUE_2 + "?" + JIRA_ISSUE_FIELDS,
            new String(
                Files.readAllBytes(
                    Paths.get(
                        GitChangelogApiTest.class
                            .getResource("/jira-issue-jir-5262.json")
                            .toURI())),
                UTF_8)) //
        .addMockedResponse(
            "/redmine/issues/1234.json?null",
            new String(
                Files.readAllBytes(
                    Paths.get(
                        GitChangelogApiTest.class.getResource("/redmine-issue-1234.json").toURI())),
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
        // An untagged branch: rendering from ZERO_COMMIT to a plain branch (not a tag) must not
        // crash, and its "Unreleased" bucket must not leak any of the tagged commits above.
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
  public void testThatRenderingFromZeroCommitToABranchDoesNotCrash() throws Exception {
    final String rendered =
        gitChangelogApiBuilder() //
            .withTemplateContent(
                """
                {{#tags}}
                ## {{name}} ({{tagDate .}})
                {{/tags}}""") //
            .withFromRevision(ZERO_COMMIT) //
            .withToRevision("unreleased-work") //
            .withFromRepo(this.repo.dir()) //
            .render();

    assertThat(rendered).contains("## test (", "## 1.0 (", "## 0.0.1 (", "## Unreleased (");
  }

  @Test
  public void testThatFirstVersionCanBeGenerated() throws Exception {
    final Changelog changelog =
        gitChangelogApiBuilder() //
            .withFromCommit(ZERO_COMMIT) //
            .withToCommit("0.0.1") //
            .withFromRepo(this.repo.dir()) //
            .getChangelog();

    assertThat(changelog.getTags()).extracting(Tag::getName).containsExactly("0.0.1");
    assertThat(changelog.getTags().get(0).getCommits())
        .extracting(Commit::getMessage)
        .containsExactly("Initial commit");
  }

  @Test
  public void testThatSecondVersionCanBeGenerated() throws Exception {
    final Changelog changelog =
        gitChangelogApiBuilder() //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("1.0") //
            .withFromRepo(this.repo.dir()) //
            .getChangelog();

    assertThat(changelog.getTags()) //
        .as("newest tag first") //
        .extracting(Tag::getName)
        .containsExactly("1.0", "0.0.1");
    assertThat(changelog.getTags().get(0).getCommits())
        .extracting(Commit::getMessage)
        .containsExactly(
            "Add feature B", "Update documentation", "Fix crash in feature A", "Add feature A");
  }

  @Test
  public void testThatTagsThatAreEmptyAfterCommitsHaveBeenIgnoredAreRemoved() throws Exception {
    final Changelog changelog =
        gitChangelogApiBuilder() //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withIgnoreCommitsWithMessage(".*") //
            .withFromRepo(this.repo.dir()) //
            .getChangelog();

    assertThat(changelog.getCommits()) //
        .as("every commit is filtered out by the \".*\" pattern") //
        .isEmpty();
    assertThat(changelog.getTags()) //
        .as("tags left with no commits are removed rather than rendered empty") //
        .isEmpty();
  }

  @Test
  public void testPathFilterCanBeSpecified() throws Exception {
    final Changelog changelog =
        gitChangelogApiBuilder() //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("2.0") //
            .withPathFilters("src") //
            .withFromRepo(this.repo.dir()) //
            .getChangelog();

    assertThat(changelog.getCommits()) //
        .as("only commits touching src/, plus the always-included root commit, survive") //
        .extracting(Commit::getMessage)
        .containsExactly(
            "Add feature D, implements " + JIRA_ISSUE_1,
            "Add feature C, fixes " + GITHUB_ISSUE,
            "Initial commit");
  }

  @Test
  public void testPathFiltersCanBeSpecified() throws Exception {
    // Same scenario as testPathFilterCanBeSpecified, but through the withFromRevision/
    // withToRevision aliases instead of withFromCommit/withToRef.
    final Changelog changelog =
        gitChangelogApiBuilder() //
            .withFromRevision(ZERO_COMMIT) //
            .withToRevision("2.0") //
            .withPathFilters("src") //
            .withFromRepo(this.repo.dir()) //
            .getChangelog();

    assertThat(changelog.getCommits())
        .extracting(Commit::getMessage)
        .containsExactly(
            "Add feature D, implements " + JIRA_ISSUE_1,
            "Add feature C, fixes " + GITHUB_ISSUE,
            "Initial commit");
  }

  @Test
  public void testThatIssuesCanBeRemoved() throws Exception {
    final Changelog changelog =
        gitChangelogApiBuilder() //
            .withJiraEnabled(true)
            .withGitHubEnabled(true)
            .withJiraServer("https://jiraserver/jira") //
            .withUseIntegrations(true)
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withRemoveIssueFromMessageArgument(true) //
            .withFromRepo(this.repo.dir()) //
            .getChangelog();

    assertThat(changelog.getIssues()) //
        .as(
            "GitHub/Jira issues are enriched via REST (title comes from the mocked response),"
                + " and the issue reference is stripped from the commit message") //
        .extracting(Issue::getName, Issue::getTitle, i -> i.getCommits().get(0).getMessage())
        .contains(
            tuple(
                "GitHub",
                "Create resource that can be invoked from scripts to trigger events",
                "Fix crash reported in"),
            tuple("Jira", "Title of jira 1234", "Implement support for"),
            tuple("Jira", "The Title of jira 5262", "Follow-up work for"));
  }

  @Test
  public void testThatCommitsWithoutIssueCanBeIgnoredIssuesCommits() throws Exception {
    final Changelog changelog =
        gitChangelogApiBuilder() //
            .withJiraEnabled(true)
            .withGitHubEnabled(true)
            .withJiraServer("https://jiraserver/jira") //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withIgnoreCommitsWithoutIssue(true) //
            .withFromRepo(this.repo.dir()) //
            .getChangelog();

    assertThat(changelog.getCommits()) //
        .as("only commits with a resolvable GitHub/Jira issue reference survive") //
        .extracting(Commit::getMessage)
        .containsExactly(
            "Follow-up work for " + JIRA_ISSUE_2,
            "Implement support for " + JIRA_ISSUE_1,
            "Fix crash reported in " + GITHUB_ISSUE);
    assertThat(changelog.getTags()) //
        .as("0.0.1 and 1.0 are left with no matching commits and dropped") //
        .extracting(Tag::getName)
        .containsExactly("test");
  }

  /** "no-issue" (see {@link #before}) has no recognizable issue reference, so it's dropped. */
  @Test
  public void testThatCommitsWithoutIssueCanBeIgnoredTagsIssuesCommits() throws Exception {
    final Changelog changelog =
        gitChangelogApiBuilder() //
            .withJiraEnabled(true)
            .withGitHubEnabled(true)
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withGitHubIssuePattern("nooo") //
            .withCustomIssue(
                "JIRA", "JIR-[0-9]*", "http://${PATTERN_GROUP}", "${PATTERN_GROUP}") //
            .withIgnoreCommitsWithoutIssue(true) //
            .withFromRepo(this.repo.dir()) //
            .getChangelog();

    assertThat(changelog.getCommits()) //
        .as(
            "the GitHub-referencing commit is dropped once that pattern is disabled; only the"
                + " commits matched by the custom JIRA pattern remain") //
        .extracting(Commit::getMessage)
        .containsExactly(
            "Follow-up work for " + JIRA_ISSUE_2, "Implement support for " + JIRA_ISSUE_1);
    assertThat(changelog.getIssues())
        .extracting(Issue::getName, Issue::getIssue)
        .containsExactlyInAnyOrder(tuple("JIRA", JIRA_ISSUE_1), tuple("JIRA", JIRA_ISSUE_2));
  }

  @Test
  public void testThatReadableGroupMustExist() throws Exception {
    assertThatThrownBy(
            () ->
                gitChangelogApiBuilder() //
                    .withFromCommit(ZERO_COMMIT) //
                    .withToRef("test") //
                    .withReadableTagName("[0-9]+?") //
                    .withFromRepo(this.repo.dir()) //
                    .getChangelog())
        // Every tag in range (0.0.1, 1.0, test) contains a digit, so "[0-9]+?" - which has no
        // capturing group - fails on whichever one is processed first. Which tag that is isn't
        // part of the behavior under test, so only the fixed part of the message is checked.
        .hasMessageStartingWith("Pattern: \"[0-9]+?\" did not match any group in: \"refs/tags/");
  }

  @Test
  public void testThatReadableGroupCanBeSet() throws Exception {
    final Changelog changelog =
        gitChangelogApiBuilder() //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withReadableTagName(".*/([0-9]+?\\.[0-9]+?)$") //
            .withFromRepo(this.repo.dir()) //
            .getChangelog();

    assertThat(changelog.getTags()) //
        .as(
            "only a ref ending in exactly \"N.N\" matches this pattern and is shortened; refs"
                + " that don't match (0.0.1 has three components, test has none) keep their full"
                + " ref name") //
        .extracting(Tag::getName)
        .containsExactly("refs/tags/test", "1.0", "refs/tags/0.0.1");
  }

  @Test
  public void testThatCustomVariablesCanBeUsed() throws Exception {
    final Map<String, Object> extendedVariables = new HashMap<>();
    extendedVariables.put("customVariable", "the value");

    final String rendered =
        gitChangelogApiBuilder() //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withExtendedVariables(extendedVariables) //
            .withTemplateContent("Extended variable: {{customVariable}}") //
            .withFromRepo(this.repo.dir()) //
            .render();

    assertThat(rendered).contains("Extended variable: the value");
  }

  @Test
  public void testThatRevertedCommitsAreRemoved() throws Exception {
    final Changelog changelog =
        gitChangelogApiBuilder() //
            .withFromCommit(this.repo.hash("feature-b")) //
            .withToCommit(this.repo.hash("github-issue-fix")) //
            .withFromRepo(this.repo.dir()) //
            .getChangelog();

    assertThat(changelog.getCommits()) //
        .as(
            "both \"experimental\" and its revert commit are removed; only the following commit"
                + " remains") //
        .extracting(Commit::getMessage)
        .containsExactly("Fix crash reported in " + GITHUB_ISSUE);
  }

  @Test
  public void testThatFileCanBeSupplied() throws Exception {
    // Content doesn't matter here - only that toFile() writes it out.
    final Path path = Paths.get("build", "testdirtocreate", "testThatFileCanBeSupplied.md");
    gitChangelogApiBuilder() //
        .withFromCommit(this.repo.hash("feature-b")) //
        .withToCommit(this.repo.hash("github-issue-fix")) //
        .withTemplateContent("content") //
        .withFromRepo(this.repo.dir()) //
        .toFile(path.toFile());

    assertThat(path.toFile()).exists().isFile();
  }

  @Test
  public void testThatOnlyGithubIssuesCanBeParsed() throws Exception {
    final Changelog changelog =
        gitChangelogApiBuilder() //
            .withGitHubEnabled(true)
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("2.0") //
            .withPathFilters("src") //
            .withIgnoreCommitsWithoutIssue(true) //
            .withFromRepo(this.repo.dir()) //
            .getChangelog();

    assertThat(changelog.getCommits()) //
        .as(
            "with only GitHub enabled, the src/ commit referencing a Jira issue counts as"
                + " \"without issue\" and is dropped along with the issue-less root commit") //
        .extracting(Commit::getMessage)
        .containsExactly("Add feature C, fixes " + GITHUB_ISSUE);
  }

  @Test
  public void testThatOnlyJiraIssuesCanBeParsed() throws Exception {
    final Changelog changelog =
        gitChangelogApiBuilder() //
            .withJiraEnabled(true)
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("2.0") //
            .withPathFilters("src") //
            .withIgnoreCommitsWithoutIssue(true) //
            .withFromRepo(this.repo.dir()) //
            .getChangelog();

    assertThat(changelog.getCommits())
        .as("with only Jira enabled, only the src/ commit referencing a Jira issue survives")
        .extracting(Commit::getMessage)
        .containsExactly("Add feature D, implements " + JIRA_ISSUE_1);
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
