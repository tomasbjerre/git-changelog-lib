package se.bjurr.gitchangelog.api;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.getResource;
import static org.junit.Assert.assertEquals;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.net.URL;

import com.google.common.io.Resources;
import com.google.gson.GsonBuilder;

public class GitChangelogApiAsserter {

 private final String template;
 private String settings = "git-changelog-test-settings.json";

 public GitChangelogApiAsserter(String template) {
  this.template = template;
 }

 public static GitChangelogApiAsserter assertThat(String template) {
  return new GitChangelogApiAsserter(template);
 }

 public GitChangelogApiAsserter withSettings(String settings) {
  this.settings = settings;
  return this;
 }

 public void rendersTo(String file) throws Exception {
  String expected = Resources.toString(getResource("assertions/" + file), UTF_8).trim();

  URL settingsFile = getResource("settings/" + settings).toURI().toURL();
  String templatePath = "templates/" + template;

  // Test lib with settings
  GitChangelogApi gitChangelogApiBuilder = gitChangelogApiBuilder()//
    .withFromCommit(ZERO_COMMIT)//
    .withToRef("test")//
    .withSettings(settingsFile)//
    .withTemplatePath(templatePath);

  String changelog = toJson(gitChangelogApiBuilder.getChangelog());
  String settings = toJson(gitChangelogApiBuilder.getSettings());
  String templateContent = Resources.toString(getResource(templatePath), UTF_8);

  assertEquals("Test:\n" + file + "\nTemplate:\n" + templateContent + "\nChangelog: " + changelog + "\nSettings: "
    + settings, expected, gitChangelogApiBuilder //
    .render().trim());

  // Test lib
  assertEquals("With lib: " + file, expected, gitChangelogApiBuilder() //
    .withFromRepo(".") //
    .withFromCommit(ZERO_COMMIT)//
    .withToRef("test")//
    .withIgnoreCommitsWithMesssage("^\\[maven-release-plugin\\].*|^\\[Gradle Release Plugin\\].*|^Merge.*") //
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
    .withCustomIssue("Incident", "INC[0-9]*", "http://inc/${PATTERN_GROUP}", "${PATTERN_GROUP}") //
    .withCustomIssue("CQ", "CQ([0-9]+)", "http://cq/${PATTERN_GROUP_1}", "${PATTERN_GROUP_1}") //
    .withCustomIssue("Bugs", "#bug", null, "Mixed bugs") //
    .withTemplatePath(templatePath) //
    .render() //
    .trim());
 }

 private String toJson(Object object) {
  return new GsonBuilder().setPrettyPrinting().create().toJson(object);
 }
}
