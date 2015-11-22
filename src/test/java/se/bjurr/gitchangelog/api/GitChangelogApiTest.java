package se.bjurr.gitchangelog.api;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.getResource;
import static org.junit.Assert.assertEquals;
import static se.bjurr.gitchangelog.api.FakeRepo.fakeRepo;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApi.setFakeGitRepo;
import static se.bjurr.gitchangelog.internal.integrations.rest.RestClient.mock;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import se.bjurr.gitchangelog.internal.integrations.rest.RestClientMock;

import com.google.common.io.Resources;

public class GitChangelogApiTest {
 private RestClientMock mockedRestClient;

 @Before
 public void before() throws Exception {
  setFakeGitRepo(fakeRepo());
  mockedRestClient = new RestClientMock();
  mockedRestClient.addMockedResponse("/repos/tomasbjerre/git-changelog-lib/issues?state=all",
    Resources.toString(getResource("github-issues.json"), UTF_8));
  mock(mockedRestClient);
 }

 @Test
 public void testThatIssuesCanBeRemoved() throws Exception {

  String expected = Resources.toString(getResource("assertions/testThatIssuesCanBeRemoved.md"), UTF_8).trim();

  URL settingsFile = getResource("settings/git-changelog-test-settings.json").toURI().toURL();
  String templatePath = "templates/testIssuesCommits.mustache";

  assertEquals(expected, gitChangelogApiBuilder()//
    .withSettings(settingsFile)//
    .withRemoveIssueFromMessageArgument(true) //
    .withTemplatePath(templatePath)//
    .render().trim());
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
}
