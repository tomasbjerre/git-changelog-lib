package se.bjurr.gitchangelog.api;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.getResource;
import static org.junit.Assert.assertEquals;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApi.setFakeGitRepo;

import com.google.common.io.Resources;

public class GitChangelogApiAsserter {

 private String template;
 private String settings;

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
  assertEquals(//
    Resources.toString(getResource("assertions/" + file), UTF_8).trim(),//
    gitChangelogApiBuilder()//
      .withSettings(getResource("settings/" + settings).toURI().toURL())//
      .withTemplatePath("templates/" + template)//
      .render().trim());
 }
}
