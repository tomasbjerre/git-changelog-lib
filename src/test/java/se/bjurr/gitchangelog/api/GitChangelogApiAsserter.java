package se.bjurr.gitchangelog.api;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.getResource;
import static org.junit.Assert.assertEquals;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import com.google.common.io.Resources;
import com.google.gson.GsonBuilder;
import java.net.URL;

public class GitChangelogApiAsserter {

  public static GitChangelogApiAsserter assertThat(final String template) {
    return new GitChangelogApiAsserter(template);
  }

  private String ignoreCommitsIfMessageMatches;
  private String settings = "git-changelog-test-settings.json";
  private final String template;

  public GitChangelogApiAsserter(final String template) {
    this.template = template;
  }

  public void rendersTo(final String file) throws Exception {
    final String expected = Resources.toString(getResource("assertions/" + file), UTF_8).trim();

    final URL settingsFile = getResource("settings/" + this.settings).toURI().toURL();
    final String templatePath = "templates/" + this.template;

    // Test lib with settings
    final GitChangelogApi gitChangelogApiBuilder =
        gitChangelogApiBuilder() //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withSettings(settingsFile) //
            .withTemplatePath(templatePath);

    if (this.ignoreCommitsIfMessageMatches != null) {
      gitChangelogApiBuilder //
          .withIgnoreCommitsWithMessage(this.ignoreCommitsIfMessageMatches);
    }

    final String changelog = toJson(gitChangelogApiBuilder.getChangelog(true));
    final String settings = toJson(gitChangelogApiBuilder.getSettings());
    final String templateContent = Resources.toString(getResource(templatePath), UTF_8);

    assertEquals(
        "Test:\n"
            + file
            + "\nTemplate:\n"
            + templateContent
            + "\nChangelog: "
            + changelog
            + "\nSettings: "
            + settings,
        expected,
        gitChangelogApiBuilder //
            .render()
            .trim());

    // Test lib
    final GitChangelogApi withTemplatePath =
        gitChangelogApiBuilder() //
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
            .withCustomIssue("Bugs", "#bug", null, "Mixed bugs") //
            .withTemplatePath(templatePath);

    if (this.ignoreCommitsIfMessageMatches != null) {
      withTemplatePath //
          .withIgnoreCommitsWithMessage(this.ignoreCommitsIfMessageMatches);
    }

    assertEquals(
        "With lib: " + file,
        expected,
        withTemplatePath //
            .render() //
            .trim());
  }

  public GitChangelogApiAsserter setIgnoreCommitsIfMessageMatches(
      final String ignoreCommitsIfMessageMatches) {
    this.ignoreCommitsIfMessageMatches = ignoreCommitsIfMessageMatches;
    return this;
  }

  public GitChangelogApiAsserter withSettings(final String settings) {
    this.settings = settings;
    return this;
  }

  private String toJson(final Object object) {
    return new GsonBuilder().setPrettyPrinting().create().toJson(object);
  }
}
