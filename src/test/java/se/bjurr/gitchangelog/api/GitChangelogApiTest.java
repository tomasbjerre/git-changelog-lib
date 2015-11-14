package se.bjurr.gitchangelog.api;

import static se.bjurr.gitchangelog.api.FakeRepo.fakeRepo;
import static se.bjurr.gitchangelog.api.GitChangelogApiAsserter.assertThat;

import org.junit.Test;

public class GitChangelogApiTest {
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
  assertThat(fakeRepo())//
    .withTemplate(testcase + ".mustache") //
    .withSettings("git-changelog-test-settings.json") //
    .rendersTo(testcase + ".md");
 }
}
