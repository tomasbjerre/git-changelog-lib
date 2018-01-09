package se.bjurr.gitchangelog.api;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.getResource;
import static se.bjurr.gitchangelog.api.GitChangelogApiAsserter.assertThat;
import static se.bjurr.gitchangelog.internal.integrations.rest.RestClient.mock;

import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import se.bjurr.gitchangelog.internal.integrations.github.GitHubMockInterceptor;
import se.bjurr.gitchangelog.internal.integrations.github.GitHubServiceFactory;
import se.bjurr.gitchangelog.internal.integrations.jira.JiraClientFactory;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClientMock;

public class TemplatesTest {
  @Before
  public void before() throws Exception {
    JiraClientFactory.reset();

    final RestClientMock mockedRestClient = new RestClientMock();
    mockedRestClient //
        .addMockedResponse(
            "/repos/tomasbjerre/git-changelog-lib/issues?state=all",
            Resources.toString(getResource("github-issues.json"), UTF_8)) //
        .addMockedResponse(
            "/jira/rest/api/2/issue/JIR-1234?fields=parent,summary,issuetype,labels,description",
            Resources.toString(getResource("jira-issue-jir-1234.json"), UTF_8)) //
        .addMockedResponse(
            "/jira/rest/api/2/issue/JIR-5262?fields=parent,summary,issuetype,labels,description",
            Resources.toString(getResource("jira-issue-jir-5262.json"), UTF_8));
    mock(mockedRestClient);

    final GitHubMockInterceptor gitHubMockInterceptor = new GitHubMockInterceptor();
    gitHubMockInterceptor.addMockedResponse(
        "https://api.github.com/repos/tomasbjerre/git-changelog-lib/issues?state=all&per_page=100&page=1",
        Resources.toString(getResource("github-issues.json"), UTF_8));

    GitHubServiceFactory.setInterceptor(gitHubMockInterceptor);
  }

  @Test
  public void testAuthorsCommits() throws Exception {
    test("testAuthorsCommits");
  }

  @Test
  public void testCommits() throws Exception {
    test("testCommits");
  }

  @Test
  public void testCommitsVariables() throws Exception {
    test("testCommitsVariables");
  }

  @Test
  public void testIssuesAuthorsCommits() throws Exception {
    test("testIssuesAuthorsCommits");
  }

  @Test
  public void testIssuesCommits() throws Exception {
    test("testIssuesCommits");
  }

  @Test
  public void testIssueTitles() throws Exception {
    test("testIssueTitles");
  }

  @Test
  public void testIssueType() throws Exception {
    test("testIssueType");
  }

  @Test
  public void testIssueLabels() throws Exception {
    test("testIssueLabels");
  }

  @Test
  public void testIssueTypesIssuesCommits() throws Exception {
    test("testIssueTypesIssuesCommits");
  }

  @Test
  public void testMerges() throws Exception {
    final String testcase = "testMerges";
    assertThat(testcase + ".mustache") //
        .setIgnoreCommitsIfMessageMatches("") //
        .rendersTo(testcase + ".md");
  }

  @Test
  public void testTagsCommits() throws Exception {
    test("testTagsCommits");
  }

  @Test
  public void testTagsIssuesAuthorsCommits() throws Exception {
    test("testTagsIssuesAuthorsCommits");
  }

  @Test
  public void testTagsIssuesCommits() throws Exception {
    test("testTagsIssuesCommits");
  }

  private void test(String testcase) throws Exception {
    assertThat(testcase + ".mustache") //
        .rendersTo(testcase + ".md");
  }
}
