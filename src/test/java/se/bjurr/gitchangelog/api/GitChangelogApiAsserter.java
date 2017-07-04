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

  public static GitChangelogApiAsserter assertThat(String template) {
    return new GitChangelogApiAsserter(template);
  }

  private String ignoreCommitsIfMessageMatches;
  private String settings = "git-changelog-test-settings.json";
  private final String template;

  public GitChangelogApiAsserter(String template) {
    this.template = template;
  }

  public void rendersTo(String file) throws Exception {
    String expected = Resources.toString(getResource("assertions/" + file), UTF_8).trim();

    URL settingsFile = getResource("settings/" + this.settings).toURI().toURL();
    String templatePath = "templates/" + this.template;

    // Test lib with settings
    GitChangelogApi gitChangelogApiBuilder =
        gitChangelogApiBuilder() //
            .withFromCommit(ZERO_COMMIT) //
            .withToRef("test") //
            .withSettings(settingsFile) //
            .withTemplatePath(templatePath);

    if (this.ignoreCommitsIfMessageMatches != null) {
      gitChangelogApiBuilder //
          .withIgnoreCommitsWithMessage(this.ignoreCommitsIfMessageMatches);
    }

    String changelog = toJson(gitChangelogApiBuilder.getChangelog());
    String settings = toJson(gitChangelogApiBuilder.getSettings());
    String templateContent = Resources.toString(getResource(templatePath), UTF_8);

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
    GitChangelogApi withTemplatePath =
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
      String ignoreCommitsIfMessageMatches) {
    this.ignoreCommitsIfMessageMatches = ignoreCommitsIfMessageMatches;
    return this;
  }

  public GitChangelogApiAsserter withSettings(String settings) {
    this.settings = settings;
    return this;
  }

  private String toJson(Object object) {
    return new GsonBuilder().setPrettyPrinting().create().toJson(object);
  }
}
