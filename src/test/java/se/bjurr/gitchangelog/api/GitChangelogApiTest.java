package se.bjurr.gitchangelog.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;
import static se.bjurr.gitchangelog.internal.integrations.rest.RestClient.mock;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogRepositoryException;
import se.bjurr.gitchangelog.internal.integrations.github.GitHubMockInterceptor;
import se.bjurr.gitchangelog.internal.integrations.github.GitHubServiceFactory;
import se.bjurr.gitchangelog.internal.integrations.jira.JiraClientFactory;
import se.bjurr.gitchangelog.internal.integrations.redmine.RedmineClientFactory;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClientMock;
import se.bjurr.gitchangelog.test.ApprovalsWrapper;
import se.bjurr.gitchangelog.test.GitChangelogLibAssertions;

public class GitChangelogApiTest {

  private static final String JIRA_ISSUE_FIELDS =
      "fields=parent,summary,issuetype,labels,description,issuelinks";
  private static final String JIRA_BASE_PATH = "/jira/rest/api/2";

  private RestClientMock mockedRestClient;
  private GitHubMockInterceptor gitHubMockInterceptor;

  @Before
  public void before() throws Exception {
    GitChangelogLibAssertions.assertHavingMainRepoAsOrigin();
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
            JIRA_BASE_PATH + "/issue/JIR-1234?" + JIRA_ISSUE_FIELDS,
            new String(
                Files.readAllBytes(
                    Paths.get(
                        TemplatesTest.class.getResource("/jira-issue-jir-1234.json").toURI())),
                UTF_8)) //
        .addMockedResponse(
            JIRA_BASE_PATH + "/issue/JIR-5262?" + JIRA_ISSUE_FIELDS,
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
  public void testIssue182() throws Exception {
    final GitChangelogApi given =
        gitChangelogApiBuilder() //
            .withTemplatePath("changelog-with-unreleased.mustache")
            .withFromRevision(ZERO_COMMIT) //
            .withToRevision("test/issue-182");

    ApprovalsWrapper.verify(given);
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
            .withPathFilters("src");

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
            .withToRevision("1.71") //
            .withTemplatePath(templatePath) //
            .withPathFilters("src");

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
              .withToRef("test") //
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
            .withPathFilters("src")
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
            .withPathFilters("src")
            .withIgnoreCommitsWithoutIssue(true);

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
        .withToRef("1.71")
        .withPrependTemplatePath(isoFile1.toFile().getAbsolutePath())
        .prependToFile(isoFile2.toFile());

    final byte[] renderedContent = Files.readAllBytes(isoFile2);
    return renderedContent;
  }
}
