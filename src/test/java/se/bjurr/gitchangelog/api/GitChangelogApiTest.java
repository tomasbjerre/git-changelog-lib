package se.bjurr.gitchangelog.api;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.io.Resources.getResource;
import static org.junit.Assert.assertEquals;
import static se.bjurr.gitchangelog.api.FakeRepo.fakeRepo;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApi.setFakeGitRepo;
import static se.bjurr.gitchangelog.internal.integrations.rest.RestClient.mock;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import se.bjurr.gitchangelog.internal.integrations.github.GitHubClientFactory;
import se.bjurr.gitchangelog.internal.integrations.jira.JiraClientFactory;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClientMock;

import com.google.common.io.Resources;
import com.google.gson.GsonBuilder;

public class GitChangelogApiTest {
 private RestClientMock mockedRestClient;

 @Before
 public void before() throws Exception {
  GitHubClientFactory.reset();
  JiraClientFactory.reset();

  setFakeGitRepo(fakeRepo());
  mockedRestClient = new RestClientMock();
  mockedRestClient //
    .addMockedResponse("/repos/tomasbjerre/git-changelog-lib/issues?state=all",
      Resources.toString(getResource("github-issues.json"), UTF_8)) //
    .addMockedResponse("/jira/rest/api/2/issue/JIR-1234?fields=parent,summary",
      Resources.toString(getResource("jira-issue-jir-1234.json"), UTF_8)) //
    .addMockedResponse("/jira/rest/api/2/issue/JIR-5262?fields=parent,summary",
      Resources.toString(getResource("jira-issue-jir-5262.json"), UTF_8));
  mock(mockedRestClient);
 }

 @Test
 public void testThatIssuesCanBeRemoved() throws Exception {

  String expected = Resources.toString(getResource("assertions/testThatIssuesCanBeRemoved.md"), UTF_8).trim();

  URL settingsFile = getResource("settings/git-changelog-test-settings.json").toURI().toURL();
  String templatePath = "templates/testIssuesCommits.mustache";

  String templateContent = Resources.toString(getResource(templatePath), UTF_8);

  GitChangelogApi changelogApiBuilder = gitChangelogApiBuilder()//
    .withSettings(settingsFile)//
    .withRemoveIssueFromMessageArgument(true) //
    .withTemplatePath(templatePath);

  assertEquals("templateContent:\n" + templateContent + "\nContext:\n" + toJson(changelogApiBuilder.getChangelog()),
    expected, changelogApiBuilder.render().trim());
 }

 @Test(expected = RuntimeException.class)
 public void testThatReadableGroupMustExist() throws Exception {
  URL settingsFile = getResource("settings/git-changelog-test-settings.json").toURI().toURL();
  String templatePath = "templates/testIssuesCommits.mustache";

  gitChangelogApiBuilder()//
    .withSettings(settingsFile)//
    .withRemoveIssueFromMessageArgument(true) //
    .withTemplatePath(templatePath)//
    .withReadableTagName(".*/[0-9]+?\\.[0-9]+?$")//
    .render();
 }

 @Test()
 public void testThatReadableGroupCanBeSet() throws Exception {
  URL settingsFile = getResource("settings/git-changelog-test-settings.json").toURI().toURL();
  String templatePath = "templates/testIssuesCommits.mustache";

  gitChangelogApiBuilder()//
    .withSettings(settingsFile)//
    .withRemoveIssueFromMessageArgument(true) //
    .withTemplatePath(templatePath)//
    .withReadableTagName(".*/([0-9]+?\\.[0-9]+?)$")//
    .render();
 }

 @Test()
 public void testThatCustomVariablesCanBeUsed() throws Exception {
  String expected = Resources.toString(getResource("assertions/testAuthorsCommitsExtended.md"), UTF_8).trim();

  URL settingsFile = getResource("settings/git-changelog-test-settings.json").toURI().toURL();
  String templatePath = "templates/testAuthorsCommitsExtended.mustache";

  assertEquals(expected, gitChangelogApiBuilder()//
    .withSettings(settingsFile)//
    .withExtendedVariables(newHashMap(of("customVariable", (Object) "the value"))) //
    .withRemoveIssueFromMessageArgument(true) //
    .withTemplatePath(templatePath)//
    .render() //
    .trim());
 }

 private String toJson(Object object) {
  return new GsonBuilder().setPrettyPrinting().create().toJson(object);
 }
}
