package se.bjurr.gitchangelog.api;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.getResource;
import static org.junit.Assert.assertEquals;
import static se.bjurr.gitchangelog.api.FakeRepo.fakeRepo;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApi.setFakeGitRepo;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Resources;

public class GitChangelogApiTest {
 @Before
 public void before() {
  setFakeGitRepo(fakeRepo());
 }

 @Test
 public void testTagsIssuesAuthorsCommits() throws Exception {

  String expected = Resources.toString(getResource("assertions/testThatIssuesCanBeRemoved.md"), UTF_8).trim();

  URL settingsFile = getResource("settings/git-changelog-test-settings.json").toURI().toURL();
  String templatePath = "templates/testIssuesCommits.mustache";

  assertEquals(expected, gitChangelogApiBuilder()//
    .withSettings(settingsFile)//
    .withRemoveIssueFromMessageArgument(true) //
    .withTemplatePath(templatePath)//
    .render().trim());
 }
}
