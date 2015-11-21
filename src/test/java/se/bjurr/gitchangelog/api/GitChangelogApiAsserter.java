package se.bjurr.gitchangelog.api;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.Resources.getResource;
import static org.junit.Assert.assertEquals;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApi.setFakeGitRepo;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;
import static se.bjurr.gitchangelog.main.Main.PARAM_OUTPUT_STDOUT;
import static se.bjurr.gitchangelog.main.Main.PARAM_SETTINGS_FILE;
import static se.bjurr.gitchangelog.main.Main.PARAM_TEMPLATE;

import java.net.URL;
import java.util.List;

import se.bjurr.gitchangelog.main.Main;

import com.google.common.io.Resources;

public class GitChangelogApiAsserter {

 private String template;
 private String settings = "git-changelog-test-settings.json";

 private GitChangelogApiAsserter(FakeGitRepo fakeGitRepo) {
  setFakeGitRepo(fakeGitRepo);
 }

 public static GitChangelogApiAsserter assertThat(FakeGitRepo fakeGitRepo) {
  return new GitChangelogApiAsserter(fakeGitRepo);
 }

 public GitChangelogApiAsserter withSettings(String settings) {
  this.settings = settings;
  return this;
 }

 public GitChangelogApiAsserter withTemplate(String template) {
  this.template = template;
  return this;
 }

 public void rendersTo(String file) throws Exception {
  String expected = Resources.toString(getResource("assertions/" + file), UTF_8).trim();

  URL settingsFile = getResource("settings/" + settings).toURI().toURL();
  String templatePath = "templates/" + template;

  // Test lib with settings
  assertEquals("With lib: " + file, expected, gitChangelogApiBuilder()//
    .withSettings(settingsFile)//
    .withTemplatePath(templatePath)//
    .render().trim());

  // Test lib
  assertEquals(
    "With lib: " + file,
    expected,
    gitChangelogApiBuilder()
      .withFromRepo(".")
      .withFromCommit(ZERO_COMMIT)
      .withToRef("refs/heads/master")
      .withIgnoreCommitsWithMesssage("^\\[maven-release-plugin\\].*|^\\[Gradle Release Plugin\\].*|^Merge.*")
      .withReadableTagName("/([^/]+?)$")
      .withDateFormat("YYYY-MM-dd HH:mm:ss")
      .withUntaggedName("No tag")
      .withNoIssueName("No issue supplied")
      .withTimeZone("UTC")
      .withRemoveIssueFromMessageArgument(true)
      .withJiraServer("https://jiraserver/jira")
      .withJiraIssuePattern("\\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\\b")
      .withCustomIssue("Github", "#([0-9]+)",
        "https://github.com/tomasbjerre/git-releasenotes-plugin/issues/${PATTERN_GROUP_1}")
      .withCustomIssue("Bugs", "#bug", null) //
      .withTemplatePath(templatePath) //
      .render() //
      .trim());

  // Test main
  List<String> argList = newArrayList(//
    PARAM_SETTINGS_FILE, settingsFile.getFile(), //
    PARAM_TEMPLATE, templatePath,//
    PARAM_OUTPUT_STDOUT//
  );
  Main.recordSystemOutPrintln();
  String[] args = new String[argList.size()];
  Main.main(argList.toArray(args));
  assertEquals("With Main: " + file, expected, Main.getSystemOutPrintln().trim());
 }
}
