package se.bjurr.gitchangelog.api;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.getResource;
import static se.bjurr.gitchangelog.api.FakeRepo.fakeRepo;
import static se.bjurr.gitchangelog.api.GitChangelogApiAsserter.assertThat;
import static se.bjurr.gitchangelog.internal.integrations.rest.RestClient.mock;

import org.junit.Test;

import se.bjurr.gitchangelog.internal.integrations.github.GitHubClientFactory;
import se.bjurr.gitchangelog.internal.integrations.jira.JiraClientFactory;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClientMock;

import com.google.common.io.Resources;

public class TemplatesTest {
 @Test
 public void testTagsIssuesAuthorsCommits() throws Exception {
  test("testTagsIssuesAuthorsCommits");
 }

 @Test
 public void testTagsIssuesCommits() throws Exception {
  test("testTagsIssuesCommits");
 }

 @Test
 public void testTagsCommits() throws Exception {
  test("testTagsCommits");
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
 public void testIssuesCommits() throws Exception {
  test("testIssuesCommits");
 }

 @Test
 public void testIssuesAuthorsCommits() throws Exception {
  test("testIssuesAuthorsCommits");
 }

 @Test
 public void testAuthorsCommits() throws Exception {
  test("testAuthorsCommits");
 }

 private void test(String testcase) throws Exception {
  GitHubClientFactory.reset();
  JiraClientFactory.reset();

  RestClientMock mockedRestClient = new RestClientMock();
  mockedRestClient //
    .addMockedResponse("/repos/tomasbjerre/git-changelog-lib/issues?state=all",
      Resources.toString(getResource("github-issues.json"), UTF_8)) //
    .addMockedResponse("/jira/rest/api/2/issue/JIR-1234?fields=parent,summary",
      Resources.toString(getResource("jira-issue-jir-1234.json"), UTF_8)) //
    .addMockedResponse("/jira/rest/api/2/issue/JIR-5262?fields=parent,summary",
      Resources.toString(getResource("jira-issue-jir-5262.json"), UTF_8));
  mock(mockedRestClient);

  assertThat(fakeRepo())//
    .withTemplate(testcase + ".mustache") //
    .rendersTo(testcase + ".md");
 }
}
