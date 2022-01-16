package se.bjurr.gitchangelog.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;
import static se.bjurr.gitchangelog.internal.integrations.rest.RestClient.mock;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.bjurr.gitchangelog.internal.integrations.github.GitHubMockInterceptor;
import se.bjurr.gitchangelog.internal.integrations.github.GitHubServiceFactory;
import se.bjurr.gitchangelog.internal.integrations.jira.JiraClientFactory;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClientMock;
import se.bjurr.gitchangelog.test.ApprovalsWrapper;

public class TemplatesTest {
  private GitChangelogApi baseBuilder;

  @Before
  public void before() throws Exception {
    JiraClientFactory.reset();

    final RestClientMock mockedRestClient = new RestClientMock();
    mockedRestClient //
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
                UTF_8));
    mock(mockedRestClient);

    final GitHubMockInterceptor gitHubMockInterceptor = new GitHubMockInterceptor();
    gitHubMockInterceptor.addMockedResponse(
        "https://api.github.com/repos/tomasbjerre/git-changelog-lib/issues?state=all&per_page=100&page=1",
        new String(
            Files.readAllBytes(
                Paths.get(TemplatesTest.class.getResource("/github-issues.json").toURI())),
            UTF_8));

    GitHubServiceFactory.setInterceptor(gitHubMockInterceptor);

    this.baseBuilder =
        gitChangelogApiBuilder() //
            .withUseIntegrations(true)
            .withJiraEnabled(true)
            .withGitHubEnabled(true)
            .withGitLabEnabled(true)
            .withRedmineEnabled(true)
            .withFromRepo(".") //
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

  @After
  public void after() {
    JiraClientFactory.reset();
    GitHubServiceFactory.setInterceptor(null);
    mock(null);
  }

  @Test
  public void testAuthorsCommits() throws Exception {
    final GitChangelogApi given =
        this.baseBuilder.withTemplatePath("templatetest/" + "testAuthorsCommits" + ".mustache");
    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testCommits() throws Exception {
    final GitChangelogApi given =
        this.baseBuilder.withTemplatePath("templatetest/" + "testCommits" + ".mustache");
    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testCommitsVariables() throws Exception {
    final GitChangelogApi given =
        this.baseBuilder.withTemplatePath("templatetest/" + "testCommitsVariables" + ".mustache");
    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testIssuesAuthorsCommits() throws Exception {
    final GitChangelogApi given =
        this.baseBuilder.withTemplatePath(
            "templatetest/" + "testIssuesAuthorsCommits" + ".mustache");
    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testIssuesCommits() throws Exception {
    final GitChangelogApi given =
        this.baseBuilder.withTemplatePath("templatetest/" + "testIssuesCommits" + ".mustache");
    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testIssueTitles() throws Exception {
    final GitChangelogApi given =
        this.baseBuilder.withTemplatePath("templatetest/" + "testIssueTitles" + ".mustache");
    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testIssueType() throws Exception {
    final GitChangelogApi given =
        this.baseBuilder.withTemplatePath("templatetest/" + "testIssueType" + ".mustache");
    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testIssueLabels() throws Exception {
    final GitChangelogApi given =
        this.baseBuilder.withTemplatePath("templatetest/" + "testIssueLabels" + ".mustache");
    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testIssueLinkedIssues() throws Exception {
    final GitChangelogApi given =
        this.baseBuilder.withTemplatePath("templatetest/" + "testIssueLinkedIssues" + ".mustache");
    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testIssueTypesIssuesCommits() throws Exception {
    final GitChangelogApi given =
        this.baseBuilder.withTemplatePath(
            "templatetest/" + "testIssueTypesIssuesCommits" + ".mustache");
    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testTagsCommits() throws Exception {
    final GitChangelogApi given =
        this.baseBuilder.withTemplatePath("templatetest/" + "testTagsCommits" + ".mustache");
    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testTagsIssuesAuthorsCommits() throws Exception {
    final GitChangelogApi given =
        this.baseBuilder.withTemplatePath(
            "templatetest/" + "testTagsIssuesAuthorsCommits" + ".mustache");
    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testTagsIssuesCommits() throws Exception {
    final GitChangelogApi given =
        this.baseBuilder.withTemplatePath("templatetest/" + "testTagsIssuesCommits" + ".mustache");
    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testThatIgnoreCommitsIfMessageMatchesCanBeEmptyToDisableTheFeature()
      throws Exception {
    final GitChangelogApi given =
        this.baseBuilder
            .withTemplatePath("mix/testMerges.mustache")
            .withIgnoreCommitsWithMessage("");
    ApprovalsWrapper.verify(given);
  }

  @Test
  public void testThatPartialsCanBeIncluded() throws Exception {
    final GitChangelogApi given =
        this.baseBuilder
            .withTemplatePath("templatetest/testThatPartialsCanBeIncluded.mustache")
            .withTemplateBaseDir("./src/test/resources/templatetest")
            .withTemplateSuffix(".partial");
    ApprovalsWrapper.verify(given);
  }
}
