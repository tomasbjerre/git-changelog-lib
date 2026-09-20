package se.bjurr.gitchangelog.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;
import static se.bjurr.gitchangelog.internal.integrations.rest.RestClient.mock;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import se.bjurr.gitchangelog.internal.integrations.jira.JiraClientFactory;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClientMock;
import se.bjurr.gitchangelog.test.TestGitRepo;

/**
 * Every test here renders one template against the same small synthetic repo built in {@link
 * #before}, instead of this project's own git history, and asserts the rendered output as a text
 * block (via {@code isEqualToIgnoringWhitespace}, so incidental whitespace - trailing spaces, a
 * stray blank line - Mustache happens to emit around tags doesn't matter) - so both the input (the
 * repo) and the expected output are short, readable, and live right next to each other, instead of
 * a multi-thousand-line approved.txt dominated by a full settings+context JSON dump that has
 * nothing to do with what a given template actually renders.
 */
public class TemplatesTest {
  private GitChangelogApi baseBuilder;
  private TestGitRepo repo;

  @BeforeEach
  public void before(@TempDir final File repoDir) throws Exception {
    JiraClientFactory.reset();

    final RestClientMock mockedRestClient = new RestClientMock();
    mockedRestClient //
        .addMockedResponse(
        "/repos/tomasbjerre/git-changelog-lib/issues?state=all&per_page=100&page=1",
        new String(
            Files.readAllBytes(
                Paths.get(TemplatesTest.class.getResource("/github-issues.json").toURI())),
            UTF_8)); //

    this.mockJiraResponses(mockedRestClient);
    this.mockJiraResponses(mockedRestClient, "customfield_10000,customfield_10002");

    mock(mockedRestClient);

    // Two releases (1.0, then test) covering every issue-tracker shape the templates below
    // exercise: a GitHub issue with a "bug" label (#20) and one with "enhancement" (#80), a Jira
    // issue with labels and a custom field (JIR-1234), one with linked issues (JIR-5262), each of
    // the three custom issue trackers configured below (Incident/CQ/Bugs), a commit with no issue
    // at all, a multi-paragraph commit body, and a merge (plus the feature commit it merges) that
    // the default ignoreCommitsWithMessage pattern filters out of every test except
    // testThatIgnoreCommitsIfMessageMatchesCanBeEmptyToDisableTheFeature.
    this.repo =
        TestGitRepo.in(repoDir) //
            .remote("origin", "git@github.com:tomasbjerre/git-changelog-lib.git")
            .author("Alice", "alice@example.com")
            .commit("root", "Initial commit", "README.md", "hello")
            .commit(
                "body",
                "Add feature A\n\n" + "paragraph one\n\n" + " * item one\n" + " * item two",
                "file.txt",
                "a")
            .branch("feature-x")
            .checkout("feature-x")
            .commit(
                "feature-x-commit",
                "[Gradle Release Plugin] plumbing commit, filtered out by default",
                "plumbing.txt",
                "x")
            .checkout("master")
            .merge("merge-commit", "feature-x", "Merge branch 'feature-x' into master")
            .author("Bob", "bob@example.com")
            .commit("github-bug", "Fix serious problem reported in #20", "file.txt", "b")
            .commit("github-enhancement", "Add cool feature requested in #80", "file.txt", "c")
            .tag("1.0")
            .author("Alice", "alice@example.com")
            .commit("jira-1234", "Improve reliability addressed in JIR-1234", "file.txt", "d")
            .author("Bob", "bob@example.com")
            .commit("jira-5262", "Refactor module addressed in JIR-5262", "file.txt", "e")
            .author("Alice", "alice@example.com")
            .commit("incident", "Resolve incident logged in INC123", "file.txt", "f")
            .author("Bob", "bob@example.com")
            .commit("cq", "Address code quality flagged in CQ55", "file.txt", "g")
            .author("Alice", "alice@example.com")
            .commit("bugs-custom", "General bug sweep flagged in #bug", "file.txt", "h")
            .author("Bob", "bob@example.com")
            .commit("no-issue", "Tidy up whitespace", "file.txt", "i")
            .tag("test");

    this.baseBuilder =
        gitChangelogApiBuilder() //
            .withUseIntegrations(true)
            .withJiraEnabled(true)
            .withGitHubEnabled(true)
            .withGitLabEnabled(true)
            .withRedmineEnabled(true)
            .withFromRepo(this.repo.dir()) //
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
            .withCustomIssue("Bugs", "#bug", null, "Mixed bugs");
  }

  private RestClientMock mockJiraResponses(final RestClientMock mockedRestClient)
      throws IOException, URISyntaxException {
    return this.mockJiraResponses(mockedRestClient, null);
  }

  private RestClientMock mockJiraResponses(
      final RestClientMock mockedRestClient, final String additionalFields)
      throws IOException, URISyntaxException {
    return mockedRestClient
        .addMockedResponse(
            "/jira/rest/api/2/issue/JIR-1234?fields=parent,summary,issuetype,labels,description,issuelinks"
                + (additionalFields != null ? "," + additionalFields : ""),
            new String(
                Files.readAllBytes(
                    Paths.get(
                        TemplatesTest.class.getResource("/jira-issue-jir-1234.json").toURI())),
                UTF_8)) //
        .addMockedResponse(
            "/jira/rest/api/2/issue/JIR-5262?fields=parent,summary,issuetype,labels,description,issuelinks"
                + (additionalFields != null ? "," + additionalFields : ""),
            new String(
                Files.readAllBytes(
                    Paths.get(
                        TemplatesTest.class.getResource("/jira-issue-jir-5262.json").toURI())),
                UTF_8));
  }

  @AfterEach
  public void after() throws IOException {
    JiraClientFactory.reset();
    mock(null);
    this.repo.close();
  }

  @Test
  public void testUrlParts() throws Exception {
    final String rendered =
        this.baseBuilder.withTemplatePath("templatetest/testUrlParts.mustache").render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
ownerName: tomasbjerre
repoName: git-changelog-lib
urlParts: [git-changelog-lib, tomasbjerre, git@github.com]
urlParts.0: git-changelog-lib
urlParts.1: tomasbjerre
urlParts.2: git@github.com
""");
  }

  @Test
  public void testEachUrlPart() throws Exception {
    final String rendered =
        this.baseBuilder.withTemplatePath("templatetest/testEachUrlPart.mustache").render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
ownerName: tomasbjerre
repoName: git-changelog-lib
urlParts: [git-changelog-lib, tomasbjerre, git@github.com]
https://github.com/tomasbjerre/git-changelog-lib/
""");
  }

  @Test
  public void testAuthorsCommits() throws Exception {
    final String rendered =
        this.baseBuilder.withTemplatePath("templatetest/testAuthorsCommits.mustache").render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
# Git Changelog changelog

Changelog of Git Changelog.

* Bob

[9427e3ef765e48e](https://server/9427e3ef765e48e) Bob *2020-01-01 00:11:00*

Tidy up whitespace


[127169f614ede3a](https://server/127169f614ede3a) Bob *2020-01-01 00:09:00*

Address code quality flagged in


[f01290c7a6cc563](https://server/f01290c7a6cc563) Bob *2020-01-01 00:07:00*

Refactor module addressed in


[68af4c90cc26bde](https://server/68af4c90cc26bde) Bob *2020-01-01 00:05:00*

Add cool feature requested in


[24043e3a72ec224](https://server/24043e3a72ec224) Bob *2020-01-01 00:04:00*

Fix serious problem reported in


* Alice

[fa34fdd74dc8aec](https://server/fa34fdd74dc8aec) Alice *2020-01-01 00:10:00*

General bug sweep flagged in


[1e90a9c9ef6e7dc](https://server/1e90a9c9ef6e7dc) Alice *2020-01-01 00:08:00*

Resolve incident logged in


[18d2df17b08e6e1](https://server/18d2df17b08e6e1) Alice *2020-01-01 00:06:00*

Improve reliability addressed in


[cf032fad475c3db](https://server/cf032fad475c3db) Alice *2020-01-01 00:01:00*

Add feature A

paragraph one

 * item one
 * item two


[3b2a25d3e86b87c](https://server/3b2a25d3e86b87c) Alice *2020-01-01 00:00:00*

Initial commit



""");
  }

  @Test
  public void testCommits() throws Exception {
    final String rendered =
        this.baseBuilder.withTemplatePath("templatetest/testCommits.mustache").render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
# Git Changelog changelog

Changelog of Git Changelog.

## Bob - 2020-01-01 00:11:00
[9427e3ef765e48e25c66e074386bec122cb21d40](https://server/9427e3ef765e48e)

Tidy up whitespace

## Alice - 2020-01-01 00:10:00
[fa34fdd74dc8aecaad8eaa5f9fd6bece00d32c2b](https://server/fa34fdd74dc8aec)

General bug sweep flagged in

## Bob - 2020-01-01 00:09:00
[127169f614ede3af56f0e71908fc69f6766256da](https://server/127169f614ede3a)

Address code quality flagged in

## Alice - 2020-01-01 00:08:00
[1e90a9c9ef6e7dc97e72400652d1ee7ade585a9a](https://server/1e90a9c9ef6e7dc)

Resolve incident logged in

## Bob - 2020-01-01 00:07:00
[f01290c7a6cc56373854eb0fb690658851466574](https://server/f01290c7a6cc563)

Refactor module addressed in

## Alice - 2020-01-01 00:06:00
[18d2df17b08e6e15ac61fc0054286313d4f49c63](https://server/18d2df17b08e6e1)

Improve reliability addressed in

## Bob - 2020-01-01 00:05:00
[68af4c90cc26bde3c64932dbbda86eb334dc6efb](https://server/68af4c90cc26bde)

Add cool feature requested in

## Bob - 2020-01-01 00:04:00
[24043e3a72ec224994a2669ee98472588a4f68cd](https://server/24043e3a72ec224)

Fix serious problem reported in

## Alice - 2020-01-01 00:01:00
[cf032fad475c3dbb5b33995269a0b842c7f0276b](https://server/cf032fad475c3db)

Add feature A

paragraph one

 * item one
 * item two

## Alice - 2020-01-01 00:00:00
[3b2a25d3e86b87cd0de5df623be686e3823c5506](https://server/3b2a25d3e86b87c)

Initial commit


""");
  }

  @Test
  public void testCommitsVariables() throws Exception {
    final String rendered =
        this.baseBuilder.withTemplatePath("templatetest/testCommitsVariables.mustache").render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
# Git Changelog changelog

Changelog of Git Changelog.

## Commit 9427e3ef765e48e
 Message: Tidy up whitespace

 Message Title: Tidy up whitespace

 Message Body:


## Commit fa34fdd74dc8aec
 Message: General bug sweep flagged in

 Message Title: General bug sweep flagged in

 Message Body:


## Commit 127169f614ede3a
 Message: Address code quality flagged in

 Message Title: Address code quality flagged in

 Message Body:


## Commit 1e90a9c9ef6e7dc
 Message: Resolve incident logged in

 Message Title: Resolve incident logged in

 Message Body:


## Commit f01290c7a6cc563
 Message: Refactor module addressed in

 Message Title: Refactor module addressed in

 Message Body:


## Commit 18d2df17b08e6e1
 Message: Improve reliability addressed in

 Message Title: Improve reliability addressed in

 Message Body:


## Commit 68af4c90cc26bde
 Message: Add cool feature requested in

 Message Title: Add cool feature requested in

 Message Body:


## Commit 24043e3a72ec224
 Message: Fix serious problem reported in

 Message Title: Fix serious problem reported in

 Message Body:


## Commit cf032fad475c3db
 Message: Add feature A

paragraph one

 * item one
 * item two

 Message Title: Add feature A

 Message Body: paragraph one
 * item one
 * item two

 Item: paragraph one

 Item: item one

 Item: item two


## Commit 3b2a25d3e86b87c
 Message: Initial commit

 Message Title: Initial commit

 Message Body:



""");
  }

  @Test
  public void testIssuesAuthorsCommits() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplatePath("templatetest/testIssuesAuthorsCommits.mustache")
            .render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
# Git Changelog changelog

Changelog of Git Changelog.

## Bugs Mixed bugs
### Alice
2020-01-01 00:10:00
General bug sweep flagged in


## CQ [CQ55](http://cq/55) 55
### Bob
2020-01-01 00:09:00
Address code quality flagged in


## GitHub [#20](https://github.com/tomasbjerre/made-up-mocked-repo/issues/20) Parameterized Jenkins&#x27; job error
### Bob
2020-01-01 00:04:00
Fix serious problem reported in


## GitHub [#80](https://github.com/tomasbjerre/made-up-mocked-repo/issues/80) Create resource that can be invoked from scripts to trigger events
### Bob
2020-01-01 00:05:00
Add cool feature requested in


## Incident [INC123](http://inc/INC123) INC123
### Alice
2020-01-01 00:08:00
Resolve incident logged in


## Jira [JIR-1234](https://jiraserver/jira/browse/JIR-1234) Title of jira 1234
### Alice
2020-01-01 00:06:00
Improve reliability addressed in


## Jira [JIR-5262](https://jiraserver/jira/browse/JIR-5262) The Title of jira 5262
### Bob
2020-01-01 00:07:00
Refactor module addressed in


## No issue supplied
### Bob
2020-01-01 00:11:00
Tidy up whitespace


### Alice
2020-01-01 00:01:00
Add feature A

paragraph one

 * item one
 * item two

2020-01-01 00:00:00
Initial commit



""");
  }

  @Test
  public void testIssuesCommits() throws Exception {
    final String rendered =
        this.baseBuilder.withTemplatePath("templatetest/testIssuesCommits.mustache").render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
# Git Changelog changelog

Changelog of Git Changelog.

## Bugs Mixed bugs

### Alice - 2020-01-01 00:10:00
[fa34fdd74dc8aec](https://server/fa34fdd74dc8aec)

General bug sweep flagged in

## CQ [CQ55](http://cq/55) 55

### Bob - 2020-01-01 00:09:00
[127169f614ede3a](https://server/127169f614ede3a)

Address code quality flagged in

## GitHub [#20](https://github.com/tomasbjerre/made-up-mocked-repo/issues/20) Parameterized Jenkins&#x27; job error

### Bob - 2020-01-01 00:04:00
[24043e3a72ec224](https://server/24043e3a72ec224)

Fix serious problem reported in

## GitHub [#80](https://github.com/tomasbjerre/made-up-mocked-repo/issues/80) Create resource that can be invoked from scripts to trigger events

### Bob - 2020-01-01 00:05:00
[68af4c90cc26bde](https://server/68af4c90cc26bde)

Add cool feature requested in

## Incident [INC123](http://inc/INC123) INC123

### Alice - 2020-01-01 00:08:00
[1e90a9c9ef6e7dc](https://server/1e90a9c9ef6e7dc)

Resolve incident logged in

## Jira [JIR-1234](https://jiraserver/jira/browse/JIR-1234) Title of jira 1234

### Alice - 2020-01-01 00:06:00
[18d2df17b08e6e1](https://server/18d2df17b08e6e1)

Improve reliability addressed in

## Jira [JIR-5262](https://jiraserver/jira/browse/JIR-5262) The Title of jira 5262

### Bob - 2020-01-01 00:07:00
[f01290c7a6cc563](https://server/f01290c7a6cc563)

Refactor module addressed in

## No issue supplied

### Bob - 2020-01-01 00:11:00
[9427e3ef765e48e](https://server/9427e3ef765e48e)

Tidy up whitespace

### Alice - 2020-01-01 00:01:00
[cf032fad475c3db](https://server/cf032fad475c3db)

Add feature A

paragraph one

 * item one
 * item two

### Alice - 2020-01-01 00:00:00
[3b2a25d3e86b87c](https://server/3b2a25d3e86b87c)

Initial commit


""");
  }

  @Test
  public void testIssueTitles() throws Exception {
    final String rendered =
        this.baseBuilder.withTemplatePath("templatetest/testIssueTitles.mustache").render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
Bugs Mixed bugs
CQ 55
GitHub Parameterized Jenkins&#x27; job error
GitHub Create resource that can be invoked from scripts to trigger events
Incident INC123
Jira Title of jira 1234
Jira The Title of jira 5262
No issue supplied

""");
  }

  // @Test
  // Enable when this is fixed: https://github.com/jknack/handlebars.java/issues/951
  public void testIssueType() throws Exception {
    // Disabled (see above), so left unasserted - just keeping it exercisable for when it's
    // re-enabled.
    this.baseBuilder.withTemplatePath("templatetest/testIssueType.mustache").render();
  }

  @Test
  public void testIssueLabels() throws Exception {
    final String rendered =
        this.baseBuilder.withTemplatePath("templatetest/testIssueLabels.mustache").render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
## test
 false
 false
 false
 true
  label1
  label2
 false
 false
## 1.0
### Enhancements

  Found a enhancement

### Bugs
  Found a bug


 true
  bug
 true
  enhancement
 false

""");
  }

  @Test
  public void testIssueLinkedIssues() throws Exception {
    final String rendered =
        this.baseBuilder.withTemplatePath("templatetest/testIssueLinkedIssues.mustache").render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
        JIR-5262 - The Title of jira 5262 [ JIRSD-490  JIRSD-567 ]

""");
  }

  @Test
  public void testIssueTypesIssuesCommits() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplatePath("templatetest/testIssueTypesIssuesCommits.mustache")
            .render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
# Git Changelog changelog

Changelog of Git Changelog.

## test
### Bugs

#### Mixed bugs


**General bug sweep flagged in**


[fa34fdd74dc8aec](https://github.com/tomasbjerre/git-changelog-lib/commit/fa34fdd74dc8aec) Alice *2020-01-01 00:10:00*

### CQ
#### [CQ55](http://cq/55) 55



**Address code quality flagged in**


[127169f614ede3a](https://github.com/tomasbjerre/git-changelog-lib/commit/127169f614ede3a) Bob *2020-01-01 00:09:00*

### Incident
#### [INC123](http://inc/INC123) INC123



**Resolve incident logged in**


[1e90a9c9ef6e7dc](https://github.com/tomasbjerre/git-changelog-lib/commit/1e90a9c9ef6e7dc) Alice *2020-01-01 00:08:00*

### Jira
#### [JIR-1234](https://jiraserver/jira/browse/JIR-1234) Title of jira 1234



**Improve reliability addressed in**


[18d2df17b08e6e1](https://github.com/tomasbjerre/git-changelog-lib/commit/18d2df17b08e6e1) Alice *2020-01-01 00:06:00*

#### [JIR-5262](https://jiraserver/jira/browse/JIR-5262) The Title of jira 5262



**Refactor module addressed in**


[f01290c7a6cc563](https://github.com/tomasbjerre/git-changelog-lib/commit/f01290c7a6cc563) Bob *2020-01-01 00:07:00*

### No issue supplied


These commits has no issue.

**Tidy up whitespace**


[9427e3ef765e48e](https://github.com/tomasbjerre/git-changelog-lib/commit/9427e3ef765e48e) Bob *2020-01-01 00:11:00*

## 1.0
### GitHub
#### [#20](https://github.com/tomasbjerre/made-up-mocked-repo/issues/20) Parameterized Jenkins&#x27; job error



**Fix serious problem reported in**


[24043e3a72ec224](https://github.com/tomasbjerre/git-changelog-lib/commit/24043e3a72ec224) Bob *2020-01-01 00:04:00*

#### [#80](https://github.com/tomasbjerre/made-up-mocked-repo/issues/80) Create resource that can be invoked from scripts to trigger events



**Add cool feature requested in**


[68af4c90cc26bde](https://github.com/tomasbjerre/git-changelog-lib/commit/68af4c90cc26bde) Bob *2020-01-01 00:05:00*

### No issue supplied


These commits has no issue.

**Add feature A**

 * paragraph one
 * item one
 * item two

[cf032fad475c3db](https://github.com/tomasbjerre/git-changelog-lib/commit/cf032fad475c3db) Alice *2020-01-01 00:01:00*

**Initial commit**


[3b2a25d3e86b87c](https://github.com/tomasbjerre/git-changelog-lib/commit/3b2a25d3e86b87c) Alice *2020-01-01 00:00:00*


""");
  }

  @Test
  public void testIssueAdditionalField() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplatePath("templatetest/testIssueAdditionalField.mustache")
            .withJiraIssueAdditionalField("customfield_10000")
            .withJiraIssueAdditionalField("customfield_10002")
            .render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
# Git Changelog changelog

Changelog of Git Changelog.

## Bugs Mixed bugs  does not have customfield_10000
## Bugs Mixed bugs does not have customfield_10001
## Bugs Mixed bugs does not have customfield_10002
### We do not have additional fields
## CQ 55  does not have customfield_10000
## CQ 55 does not have customfield_10001
## CQ 55 does not have customfield_10002
### We do not have additional fields
## GitHub Parameterized Jenkins&#x27; job error  does not have customfield_10000
## GitHub Parameterized Jenkins&#x27; job error does not have customfield_10001
## GitHub Parameterized Jenkins&#x27; job error does not have customfield_10002
### We do not have additional fields
## GitHub Create resource that can be invoked from scripts to trigger events  does not have customfield_10000
## GitHub Create resource that can be invoked from scripts to trigger events does not have customfield_10001
## GitHub Create resource that can be invoked from scripts to trigger events does not have customfield_10002
### We do not have additional fields
## Incident INC123  does not have customfield_10000
## Incident INC123 does not have customfield_10001
## Incident INC123 does not have customfield_10002
### We do not have additional fields
## Jira Title of jira 1234 Custom Field 10000 for jira 1234 has customfield_10000
## Jira Title of jira 1234 does not have customfield_10001
## Jira Title of jira 1234 does not have customfield_10002
### We have additional fields
#### for key "customfield_10000" we have value "Custom Field 10000 for jira 1234"
## Jira The Title of jira 5262 Custom Field 10000 for jira 5262 has customfield_10000
## Jira The Title of jira 5262 does not have customfield_10001
## Jira The Title of jira 5262 does not have customfield_10002
### We have additional fields
#### for key "customfield_10000" we have value "Custom Field 10000 for jira 5262"
## No issue supplied   does not have customfield_10000
## No issue supplied  does not have customfield_10001
## No issue supplied  does not have customfield_10002
### We do not have additional fields

""");
  }

  @Test
  public void testTagsCommits() throws Exception {
    final String rendered =
        this.baseBuilder.withTemplatePath("templatetest/testTagsCommits.mustache").render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
# Git Changelog changelog

Changelog of Git Changelog.

## test (2020-01-01 00:11:00)

### Bob - 2020-01-01 00:11:00
[9427e3ef765e48e](https://server/9427e3ef765e48e)

Tidy up whitespace

### Alice - 2020-01-01 00:10:00
[fa34fdd74dc8aec](https://server/fa34fdd74dc8aec)

General bug sweep flagged in

### Bob - 2020-01-01 00:09:00
[127169f614ede3a](https://server/127169f614ede3a)

Address code quality flagged in

### Alice - 2020-01-01 00:08:00
[1e90a9c9ef6e7dc](https://server/1e90a9c9ef6e7dc)

Resolve incident logged in

### Bob - 2020-01-01 00:07:00
[f01290c7a6cc563](https://server/f01290c7a6cc563)

Refactor module addressed in

### Alice - 2020-01-01 00:06:00
[18d2df17b08e6e1](https://server/18d2df17b08e6e1)

Improve reliability addressed in

## 1.0 (2020-01-01 00:05:00)

### Bob - 2020-01-01 00:05:00
[68af4c90cc26bde](https://server/68af4c90cc26bde)

Add cool feature requested in

### Bob - 2020-01-01 00:04:00
[24043e3a72ec224](https://server/24043e3a72ec224)

Fix serious problem reported in

### Alice - 2020-01-01 00:01:00
[cf032fad475c3db](https://server/cf032fad475c3db)

Add feature A

paragraph one

 * item one
 * item two

### Alice - 2020-01-01 00:00:00
[3b2a25d3e86b87c](https://server/3b2a25d3e86b87c)

Initial commit


""");
  }

  @Test
  public void testOnlyLastTag() throws Exception {
    final String rendered =
        this.baseBuilder.withTemplatePath("changelog-prepend.mustache").render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
## test (2020-01-01)

### Other changes

**Tidy up whitespace**


[9427e](https://github.com/tomasbjerre/git-changelog-lib/commit/9427e3ef765e48e) Bob *2020-01-01 00:11:00*

**General bug sweep flagged in**


[fa34f](https://github.com/tomasbjerre/git-changelog-lib/commit/fa34fdd74dc8aec) Alice *2020-01-01 00:10:00*

**Address code quality flagged in**


[12716](https://github.com/tomasbjerre/git-changelog-lib/commit/127169f614ede3a) Bob *2020-01-01 00:09:00*

**Resolve incident logged in**


[1e90a](https://github.com/tomasbjerre/git-changelog-lib/commit/1e90a9c9ef6e7dc) Alice *2020-01-01 00:08:00*

**Refactor module addressed in**


[f0129](https://github.com/tomasbjerre/git-changelog-lib/commit/f01290c7a6cc563) Bob *2020-01-01 00:07:00*

**Improve reliability addressed in**


[18d2d](https://github.com/tomasbjerre/git-changelog-lib/commit/18d2df17b08e6e1) Alice *2020-01-01 00:06:00*



""");
  }

  @Test
  public void testTagsIssuesAuthorsCommits() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplatePath("templatetest/testTagsIssuesAuthorsCommits.mustache")
            .render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
# Git Changelog changelog

Changelog of Git Changelog.

## test

### Bugs Mixed bugs
* Alice
[fa34fdd74dc8aec](https://server/fa34fdd74dc8aec) *2020-01-01 00:10:00*
General bug sweep flagged in


### CQ [CQ55](http://cq/55) 55
* Bob
[127169f614ede3a](https://server/127169f614ede3a) *2020-01-01 00:09:00*
Address code quality flagged in


### Incident [INC123](http://inc/INC123) INC123
* Alice
[1e90a9c9ef6e7dc](https://server/1e90a9c9ef6e7dc) *2020-01-01 00:08:00*
Resolve incident logged in


### Jira [JIR-1234](https://jiraserver/jira/browse/JIR-1234) Title of jira 1234
* Alice
[18d2df17b08e6e1](https://server/18d2df17b08e6e1) *2020-01-01 00:06:00*
Improve reliability addressed in


### Jira [JIR-5262](https://jiraserver/jira/browse/JIR-5262) The Title of jira 5262
* Bob
[f01290c7a6cc563](https://server/f01290c7a6cc563) *2020-01-01 00:07:00*
Refactor module addressed in


### No issue supplied
* Bob
[9427e3ef765e48e](https://server/9427e3ef765e48e) *2020-01-01 00:11:00*
Tidy up whitespace


## 1.0

### GitHub [#20](https://github.com/tomasbjerre/made-up-mocked-repo/issues/20) Parameterized Jenkins&#x27; job error
* Bob
[24043e3a72ec224](https://server/24043e3a72ec224) *2020-01-01 00:04:00*
Fix serious problem reported in


### GitHub [#80](https://github.com/tomasbjerre/made-up-mocked-repo/issues/80) Create resource that can be invoked from scripts to trigger events
* Bob
[68af4c90cc26bde](https://server/68af4c90cc26bde) *2020-01-01 00:05:00*
Add cool feature requested in


### No issue supplied
* Alice
[cf032fad475c3db](https://server/cf032fad475c3db) *2020-01-01 00:01:00*
Add feature A

paragraph one

 * item one
 * item two

[3b2a25d3e86b87c](https://server/3b2a25d3e86b87c) *2020-01-01 00:00:00*
Initial commit



""");
  }

  @Test
  public void testTagsIssuesCommits() throws Exception {
    final String rendered =
        this.baseBuilder.withTemplatePath("templatetest/testTagsIssuesCommits.mustache").render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
# Git Changelog changelog

Changelog of Git Changelog.

## test

### Bugs Mixed bugs
[fa34fdd74dc8aec](https://server/fa34fdd74dc8aec) 2020-01-01 00:10:00
General bug sweep flagged in

### CQ [CQ55](http://cq/55) 55
[127169f614ede3a](https://server/127169f614ede3a) 2020-01-01 00:09:00
Address code quality flagged in

### Incident [INC123](http://inc/INC123) INC123
[1e90a9c9ef6e7dc](https://server/1e90a9c9ef6e7dc) 2020-01-01 00:08:00
Resolve incident logged in

### Jira [JIR-1234](https://jiraserver/jira/browse/JIR-1234) Title of jira 1234
[18d2df17b08e6e1](https://server/18d2df17b08e6e1) 2020-01-01 00:06:00
Improve reliability addressed in

### Jira [JIR-5262](https://jiraserver/jira/browse/JIR-5262) The Title of jira 5262
[f01290c7a6cc563](https://server/f01290c7a6cc563) 2020-01-01 00:07:00
Refactor module addressed in

### No issue supplied
[9427e3ef765e48e](https://server/9427e3ef765e48e) 2020-01-01 00:11:00
Tidy up whitespace

## 1.0

### GitHub [#20](https://github.com/tomasbjerre/made-up-mocked-repo/issues/20) Parameterized Jenkins&#x27; job error
[24043e3a72ec224](https://server/24043e3a72ec224) 2020-01-01 00:04:00
Fix serious problem reported in

### GitHub [#80](https://github.com/tomasbjerre/made-up-mocked-repo/issues/80) Create resource that can be invoked from scripts to trigger events
[68af4c90cc26bde](https://server/68af4c90cc26bde) 2020-01-01 00:05:00
Add cool feature requested in

### No issue supplied
[cf032fad475c3db](https://server/cf032fad475c3db) 2020-01-01 00:01:00
Add feature A

paragraph one

 * item one
 * item two

[3b2a25d3e86b87c](https://server/3b2a25d3e86b87c) 2020-01-01 00:00:00
Initial commit


""");
  }

  @Test
  public void testThatIgnoreCommitsIfMessageMatchesCanBeEmptyToDisableTheFeature()
      throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplatePath("mix/testMerges.mustache")
            .withIgnoreCommitsWithMessage("")
            .render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
## Not merge: Tidy up whitespace
## Not merge: General bug sweep flagged in
## Not merge: Address code quality flagged in
## Not merge: Resolve incident logged in
## Not merge: Refactor module addressed in
## Not merge: Improve reliability addressed in
## Not merge: Add cool feature requested in
## Not merge: Fix serious problem reported in
## Merge: Merge branch &#x27;feature-x&#x27; into master
## Not merge: [Gradle Release Plugin] plumbing commit, filtered out by default
## Not merge: Add feature A
## Not merge: Initial commit

""");
  }

  @Test
  public void testThatPartialsCanBeIncluded() throws Exception {
    final String rendered =
        this.baseBuilder
            .withTemplatePath("templatetest/testThatPartialsCanBeIncluded.mustache")
            .withTemplateBaseDir("./src/test/resources/templatetest")
            .withTemplateSuffix(".partial")
            .render();
    assertThat(rendered)
        .isEqualToIgnoringWhitespace(
"""
# Git Changelog changelog

Changelog of Git Changelog.

## Bob - 2020-01-01 00:11:00
[9427e3ef765e48e25c66e074386bec122cb21d40](https://server/9427e3ef765e48e)

Tidy up whitespace
## Alice - 2020-01-01 00:10:00
[fa34fdd74dc8aecaad8eaa5f9fd6bece00d32c2b](https://server/fa34fdd74dc8aec)

General bug sweep flagged in
## Bob - 2020-01-01 00:09:00
[127169f614ede3af56f0e71908fc69f6766256da](https://server/127169f614ede3a)

Address code quality flagged in
## Alice - 2020-01-01 00:08:00
[1e90a9c9ef6e7dc97e72400652d1ee7ade585a9a](https://server/1e90a9c9ef6e7dc)

Resolve incident logged in
## Bob - 2020-01-01 00:07:00
[f01290c7a6cc56373854eb0fb690658851466574](https://server/f01290c7a6cc563)

Refactor module addressed in
## Alice - 2020-01-01 00:06:00
[18d2df17b08e6e15ac61fc0054286313d4f49c63](https://server/18d2df17b08e6e1)

Improve reliability addressed in
## Bob - 2020-01-01 00:05:00
[68af4c90cc26bde3c64932dbbda86eb334dc6efb](https://server/68af4c90cc26bde)

Add cool feature requested in
## Bob - 2020-01-01 00:04:00
[24043e3a72ec224994a2669ee98472588a4f68cd](https://server/24043e3a72ec224)

Fix serious problem reported in
## Alice - 2020-01-01 00:01:00
[cf032fad475c3dbb5b33995269a0b842c7f0276b](https://server/cf032fad475c3db)

Add feature A

paragraph one

 * item one
 * item two
## Alice - 2020-01-01 00:00:00
[3b2a25d3e86b87cd0de5df623be686e3823c5506](https://server/3b2a25d3e86b87c)

Initial commit

""");
  }
}
