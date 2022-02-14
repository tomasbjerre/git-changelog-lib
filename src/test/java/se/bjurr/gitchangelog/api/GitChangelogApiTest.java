package se.bjurr.gitchangelog.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;
import static se.bjurr.gitchangelog.internal.integrations.rest.RestClient.mock;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.bjurr.gitchangelog.internal.integrations.github.GitHubMockInterceptor;
import se.bjurr.gitchangelog.internal.integrations.github.GitHubServiceFactory;
import se.bjurr.gitchangelog.internal.integrations.jira.JiraClientFactory;
import se.bjurr.gitchangelog.internal.integrations.redmine.RedmineClientFactory;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClientMock;
import se.bjurr.gitchangelog.test.ApprovalsWrapper;

public class GitChangelogApiTest {
  private RestClientMock mockedRestClient;
  private GitHubMockInterceptor gitHubMockInterceptor;

  @Before
  public void before() throws Exception {
    JiraClientFactory.reset();
    RedmineClientFactory.reset();

    this.mockedRestClient = new RestClientMock();
    this.mockedRestClient //
        .addMockedResponse(
            "/repos/tomasbjerre/git-changelog-lib/issues?state=all",
            new String(
                Files.readAllBytes(
                    Paths.get(TemplatesTest.class.getResource("/github-issues.json").toURI())),
                UTF_8)) //
        .addMockedResponse(
            "/jira/rest/api/2/issue/JIR-1234?fields=parent,summary,issuetype,labels,description,issuelinks",
            new String(
                Files.readAllBytes(
                    Paths.get(
                        TemplatesTest.class.getResource("/jira-issue-jir-1234.json").toURI())),
                UTF_8)) //
        .addMockedResponse(
            "/jira/rest/api/2/issue/JIR-5262?fields=parent,summary,issuetype,labels,description,issuelinks",
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

    this.gitHubMockInterceptor = new GitHubMockInterceptor();
    this.gitHubMockInterceptor //
        .addMockedResponse(
        "https://api.github.com/repos/tomasbjerre/git-changelog-lib/issues?state=all&per_page=100&page=1",
        new String(
            Files.readAllBytes(
                Paths.get(TemplatesTest.class.getResource("/github-issues.json").toURI())),
            UTF_8));

    GitHubServiceFactory //
        .setInterceptor(this.gitHubMockInterceptor);
  }

  @After
  public void after() {
    JiraClientFactory.reset();
    RedmineClientFactory.reset();
    GitHubServiceFactory.setInterceptor(null);
    mock(null);
  }

  @Test
  public void testThatFirstVersionCanBeGenerated() throws Exception {
    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withFromCommit(ZERO_COMMIT) //
            .withToCommit("0.0.1");

    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testThatSecondVersionCanBeGenerated() throws Exception {
    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("1.0");

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
            .withIgnoreCommitsWithMessage(".*");

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
            .withToRef("1.71") //
            .withTemplatePath(templatePath) //
            .withPathFilter("src");

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
            .withTemplatePath(templatePath);

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
            .withTemplatePath(templatePath);

    ApprovalsWrapper.verify(given);
  }

  /** The test-lightweight-2 should be ignored here. */
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
            .withTemplatePath(templatePath);

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
              .withToRef("/test") //
              .withRemoveIssueFromMessageArgument(true) //
              .withTemplatePath(templatePath) //
              .withReadableTagName("[0-9]+?") //
              .render();
      assertThat(actual) //
          .as(
              "Should never happen! But nice to see what was rendered, if it does not crash as expected.") //
          .isEqualTo("");
    } catch (final Exception e) {
      if (!e.getMessage()
          .equals(
              "Pattern: \"[0-9]+?\" did not match any group in: \"refs/tags/test-lightweight-2\"")) {
        throw new AssertionError("Got: \"" + e.getMessage() + "\"", e);
      }
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
            .withReadableTagName(".*/([0-9]+?\\.[0-9]+?)$");

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
            .withTemplatePath(templatePath);

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
            .withFromCommit("aa1fd33") //
            .withToCommit("4c6e078") //
            .withTemplatePath(templatePath);

    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testThatFileCanBeSupplied() throws Exception {
    final String templatePath = "templatetest/testThatRevertedCommitsAreRemoved.mustache";

    final Path path = Paths.get("build", "testdirtocreate", "testThatFileCanBeSupplied.md");
    gitChangelogApiBuilder() //
        .withFromCommit("aa1fd33") //
        .withToCommit("4c6e078") //
        .withTemplatePath(templatePath) //
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
            .withToRef("1.71") //
            .withPathFilter("src")
            .withIgnoreCommitsWithoutIssue(true);

    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testThatOnlyJiraIssuesCanBeParsed() throws Exception {
    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withJiraEnabled(true)
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("1.71") //
            .withPathFilter("src")
            .withIgnoreCommitsWithoutIssue(true);

    ApprovalsWrapper.verify(given);
  }
}
