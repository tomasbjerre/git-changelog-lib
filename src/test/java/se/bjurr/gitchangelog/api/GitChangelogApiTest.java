package se.bjurr.gitchangelog.api;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;
import static se.bjurr.gitchangelog.internal.integrations.rest.RestClient.mock;

import com.google.common.io.Resources;
import com.google.gson.GsonBuilder;
import java.net.URL;
import org.junit.Before;
import org.junit.Test;
import se.bjurr.gitchangelog.internal.integrations.github.GitHubMockInterceptor;
import se.bjurr.gitchangelog.internal.integrations.github.GitHubServiceFactory;
import se.bjurr.gitchangelog.internal.integrations.jira.JiraClientFactory;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClientMock;

public class GitChangelogApiTest {
  private RestClientMock mockedRestClient;
  private GitHubMockInterceptor gitHubMockInterceptor;

  @Before
  public void before() throws Exception {
    JiraClientFactory.reset();

    mockedRestClient = new RestClientMock();
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

    gitHubMockInterceptor = new GitHubMockInterceptor();
    gitHubMockInterceptor //
        .addMockedResponse(
        "https://api.github.com/repos/tomasbjerre/git-changelog-lib/issues?state=all&per_page=100&page=1",
        Resources.toString(getResource("github-issues.json"), UTF_8));

    GitHubServiceFactory //
        .setInterceptor(gitHubMockInterceptor);
  }

  @Test
  public void testThatTagsThatAreEmptyAfterCommitsHaveBeenIgnoredAreRemoved() throws Exception {
    final String templatePath = "templates/testAuthorsCommitsExtended.mustache";

    assertThat(
            gitChangelogApiBuilder() //
                .withFromCommit(ZERO_COMMIT) //
                .withToRef("test") //
                .withTemplatePath(templatePath) //
                .withIgnoreCommitsWithMessage(".*") //
                .render() //
                .trim())
        .hasSize(74);
  }

  @Test
  public void testThatIssuesCanBeRemoved() throws Exception {

    final String expected =
        Resources.toString(getResource("assertions/testThatIssuesCanBeRemoved.md"), UTF_8).trim();

    final URL settingsFile =
        getResource("settings/git-changelog-test-settings.json").toURI().toURL();
    final String templatePath = "templates/testIssuesCommits.mustache";

    final String templateContent = Resources.toString(getResource(templatePath), UTF_8);

    final GitChangelogApi changelogApiBuilder =
        gitChangelogApiBuilder() //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withSettings(settingsFile) //
            .withRemoveIssueFromMessageArgument(true) //
            .withTemplatePath(templatePath);

    assertEquals(
        "templateContent:\n"
            + templateContent
            + "\nContext:\n"
            + toJson(changelogApiBuilder.getChangelog(true)),
        expected,
        changelogApiBuilder.render().trim());
  }

  @Test
  public void testThatCommitsWithoutIssueCanBeIgnoredIssuesCommits() throws Exception {

    final String expected =
        Resources.toString(
                getResource("assertions/testThatCommitsWithoutIssueCanBeIgnoredIssuesCommits.md"),
                UTF_8)
            .trim();

    final URL settingsFile =
        getResource("settings/git-changelog-test-settings.json").toURI().toURL();
    final String templatePath = "templates/testIssuesCommits.mustache";

    final String templateContent = Resources.toString(getResource(templatePath), UTF_8);

    final GitChangelogApi changelogApiBuilder =
        gitChangelogApiBuilder() //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withSettings(settingsFile) //
            .withIgnoreCommitsWithoutIssue(true) //
            .withTemplatePath(templatePath);

    assertEquals(
        "templateContent:\n"
            + templateContent
            + "\nContext:\n"
            + toJson(changelogApiBuilder.getChangelog(true)),
        expected,
        changelogApiBuilder.render().trim());
  }

  /** The test-lightweight-2 should be ignored here. */
  @Test
  public void testThatCommitsWithoutIssueCanBeIgnoredTagsIssuesCommits() throws Exception {

    final String expected =
        Resources.toString(
                getResource(
                    "assertions/testThatCommitsWithoutIssueCanBeIgnoredTagsIssuesCommits.md"),
                UTF_8)
            .trim();

    getResource("settings/git-changelog-test-settings.json").toURI().toURL();
    final String templatePath =
        "templates/testThatCommitsWithoutIssueCanBeIgnoredTagsIssuesCommits.mustache";

    final String templateContent = Resources.toString(getResource(templatePath), UTF_8);

    final GitChangelogApi changelogApiBuilder =
        gitChangelogApiBuilder() //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withGitHubIssuePattern("nooo") //
            .withGitLabIssuePattern("nooo") //
            .withCustomIssue(
                "JIRA", "JIR-[0-9]*", "http://${PATTERN_GROUP}", "${PATTERN_GROUP}") //
            .withIgnoreCommitsWithoutIssue(true) //
            .withTemplatePath(templatePath);

    assertEquals(
        "templateContent:\n"
            + templateContent
            + "\nContext:\n"
            + toJson(changelogApiBuilder.getChangelog(true)),
        expected,
        changelogApiBuilder.render().trim());
  }

  @Test
  public void testThatReadableGroupMustExist() throws Exception {
    final URL settingsFile =
        getResource("settings/git-changelog-test-settings.json").toURI().toURL();
    final String templatePath = "templates/testIssuesCommits.mustache";

    try {
      final String actual =
          gitChangelogApiBuilder() //
              .withFromCommit(ZERO_COMMIT) //
              .withToRef("test") //
              .withSettings(settingsFile) //
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
        getResource("settings/git-changelog-test-settings.json").toURI().toURL();
    final String templatePath = "templates/testIssuesCommits.mustache";

    gitChangelogApiBuilder() //
        .withFromCommit(ZERO_COMMIT) //
        .withToRef("test") //
        .withSettings(settingsFile) //
        .withRemoveIssueFromMessageArgument(true) //
        .withTemplatePath(templatePath) //
        .withReadableTagName(".*/([0-9]+?\\.[0-9]+?)$") //
        .render();
  }

  @Test
  public void testThatCustomVariablesCanBeUsed() throws Exception {
    final String expected =
        Resources.toString(getResource("assertions/testAuthorsCommitsExtended.md"), UTF_8).trim();

    final URL settingsFile =
        getResource("settings/git-changelog-test-settings.json").toURI().toURL();
    final String templatePath = "templates/testAuthorsCommitsExtended.mustache";

    assertEquals(
        expected,
        gitChangelogApiBuilder() //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withSettings(settingsFile) //
            .withExtendedVariables(newHashMap(of("customVariable", (Object) "the value"))) //
            .withRemoveIssueFromMessageArgument(true) //
            .withTemplatePath(templatePath) //
            .render() //
            .trim());
  }

  @Test
  public void shouldUseIntegrationIfConfigured() {
    assertThat(GitChangelogApi.shouldUseIntegrationIfConfigured("just title is false")) //
        .isFalse();

    assertThat(GitChangelogApi.shouldUseIntegrationIfConfigured("{{title}}")) //
        .isTrue();

    assertThat(GitChangelogApi.shouldUseIntegrationIfConfigured("{{link}}")) //
        .isTrue();

    assertThat(GitChangelogApi.shouldUseIntegrationIfConfigured("{{type}}")) //
        .isTrue();

    assertThat(GitChangelogApi.shouldUseIntegrationIfConfigured("asd{{#labels}}")) //
        .isTrue();
    assertThat(GitChangelogApi.shouldUseIntegrationIfConfigured("a\nsd{{#labels}}asd\nsdasd")) //
        .isTrue();
    assertThat(GitChangelogApi.shouldUseIntegrationIfConfigured("a\nsd{{labels}}asd\nsdasd")) //
        .isTrue();
  }

  private String toJson(final Object object) {
    return new GsonBuilder().setPrettyPrinting().create().toJson(object);
  }
}
