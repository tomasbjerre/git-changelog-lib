package se.bjurr.gitchangelog.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;
import static se.bjurr.gitchangelog.internal.integrations.rest.RestClient.mock;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import se.bjurr.gitchangelog.internal.integrations.jira.JiraClientFactory;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClientMock;
import se.bjurr.gitchangelog.test.TestGitRepo;

/**
 * Base for the *TemplateTest classes: each renders one or more Mustache/Handlebars templates
 * against this same small synthetic repo and asserts the exact output. Every subclass inlines its
 * own templates via {@code withTemplateContent(...)} rather than pointing at a resource file, so a
 * reader sees both the input and the expected output in the test itself.
 */
abstract class AbstractTemplatesTest {
  protected GitChangelogApi baseBuilder;
  protected TestGitRepo repo;

  @BeforeEach
  public void before(@TempDir final File repoDir) throws Exception {
    JiraClientFactory.reset();

    final RestClientMock mockedRestClient = new RestClientMock();
    mockedRestClient //
        .addMockedResponse(
        "/repos/tomasbjerre/git-changelog-lib/issues?state=all&per_page=100&page=1",
        new String(
            Files.readAllBytes(
                Paths.get(AbstractTemplatesTest.class.getResource("/github-issues.json").toURI())),
            UTF_8)); //

    this.mockJiraResponses(mockedRestClient);
    this.mockJiraResponses(mockedRestClient, "customfield_10000,customfield_10002");

    mock(mockedRestClient);

    // Two releases (1.0, then test) covering every issue-tracker shape these tests exercise: a
    // GitHub issue with a "bug" label (#20) and one with "enhancement" (#80), a Jira issue with
    // labels and a custom field (JIR-1234), one with linked issues (JIR-5262), each of the three
    // custom issue trackers configured below (Incident/CQ/Bugs), a commit with no issue at all, a
    // multi-paragraph commit body, and a merge (plus the feature commit it merges) that the
    // default ignoreCommitsWithMessage pattern filters out everywhere except the one test that
    // overrides it.
    this.repo =
        TestGitRepo.in(repoDir) //
            .remote("origin", "git@github.com:tomasbjerre/git-changelog-lib.git")
            .author("Alice", "alice@example.com")
            .commit("root", "Initial commit", "README.md", "hello")
            .commit(
                "body",
                "Add feature A\n\n" + "paragraph one\n\n" + " * item one\n" + " * item two",
                "file.txt",
                "a")
            .branch("feature-x")
            .checkout("feature-x")
            .commit(
                "feature-x-commit",
                "[Gradle Release Plugin] plumbing commit, filtered out by default",
                "plumbing.txt",
                "x")
            .checkout("master")
            .merge("merge-commit", "feature-x", "Merge branch 'feature-x' into master")
            .author("Bob", "bob@example.com")
            .commit("github-bug", "Fix serious problem reported in #20", "file.txt", "b")
            .commit("github-enhancement", "Add cool feature requested in #80", "file.txt", "c")
            .tag("1.0")
            .author("Alice", "alice@example.com")
            .commit("jira-1234", "Improve reliability addressed in JIR-1234", "file.txt", "d")
            .author("Bob", "bob@example.com")
            .commit("jira-5262", "Refactor module addressed in JIR-5262", "file.txt", "e")
            .author("Alice", "alice@example.com")
            .commit("incident", "Resolve incident logged in INC123", "file.txt", "f")
            .author("Bob", "bob@example.com")
            .commit("cq", "Address code quality flagged in CQ55", "file.txt", "g")
            .author("Alice", "alice@example.com")
            .commit("bugs-custom", "General bug sweep flagged in #bug", "file.txt", "h")
            .author("Bob", "bob@example.com")
            .commit("no-issue", "Tidy up whitespace", "file.txt", "i")
            .tag("test");

    this.baseBuilder =
        gitChangelogApiBuilder() //
            .withUseIntegrations(true)
            .withJiraEnabled(true)
            .withGitHubEnabled(true)
            .withGitLabEnabled(true)
            .withRedmineEnabled(true)
            .withFromRepo(this.repo.dir()) //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withIgnoreCommitsWithMessage(
                "^\\[maven-release-plugin\\].*|^\\[Gradle Release Plugin\\].*|^Merge.*") //
            .withIgnoreTagsIfNameMatches(".*tag-in-test-feature$") //
            .withReadableTagName("/([^/]+?)$") //
            .withDateFormat("YYYY-MM-dd HH:mm:ss") //
            .withUntaggedName("No tag") //
            .withNoIssueName("No issue supplied") //
            .withTimeZone("UTC") //
            .withRemoveIssueFromMessageArgument(true) //
            .withJiraServer("https://jiraserver/jira") //
            .withJiraIssuePattern("\\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\\b") //
            .withJiraUsername("user") //
            .withJiraPassword("code") //
            .withGitHubApi("https://api.github.com/repos/tomasbjerre/git-changelog-lib") //
            .withGitHubIssuePattern("#([0-9]+)") //
            .withCustomIssue(
                "Incident", "INC[0-9]*", "http://inc/${PATTERN_GROUP}", "${PATTERN_GROUP}") //
            .withCustomIssue(
                "CQ", "CQ([0-9]+)", "http://cq/${PATTERN_GROUP_1}", "${PATTERN_GROUP_1}") //
            .withCustomIssue("Bugs", "#bug", null, "Mixed bugs");
  }

  private RestClientMock mockJiraResponses(final RestClientMock mockedRestClient)
      throws IOException, URISyntaxException {
    return this.mockJiraResponses(mockedRestClient, null);
  }

  private RestClientMock mockJiraResponses(
      final RestClientMock mockedRestClient, final String additionalFields)
      throws IOException, URISyntaxException {
    return mockedRestClient
        .addMockedResponse(
            "/jira/rest/api/2/issue/JIR-1234?fields=parent,summary,issuetype,labels,description,issuelinks"
                + (additionalFields != null ? "," + additionalFields : ""),
            new String(
                Files.readAllBytes(
                    Paths.get(
                        AbstractTemplatesTest.class
                            .getResource("/jira-issue-jir-1234.json")
                            .toURI())),
                UTF_8)) //
        .addMockedResponse(
            "/jira/rest/api/2/issue/JIR-5262?fields=parent,summary,issuetype,labels,description,issuelinks"
                + (additionalFields != null ? "," + additionalFields : ""),
            new String(
                Files.readAllBytes(
                    Paths.get(
                        AbstractTemplatesTest.class
                            .getResource("/jira-issue-jir-5262.json")
                            .toURI())),
                UTF_8));
  }

  @AfterEach
  public void after() throws IOException {
    JiraClientFactory.reset();
    mock(null);
    this.repo.close();
  }
}
