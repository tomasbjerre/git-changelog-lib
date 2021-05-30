package se.bjurr.gitchangelog.internal.integrations.gitlab;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;

public class GitLabClientTest {
  private static Logger LOG = LoggerFactory.getLogger(GitLabClientTest.class);

  private GitLabClient sut;
  private boolean disabled;

  @Before
  public void before() throws IOException {
    final String hostUrl = "https://gitlab.com/";
    String apiToken = null;
    try {
      apiToken =
          new String(
                  Files.readAllBytes(new File("/home/bjerre/gitlabapitoken.txt").toPath()),
                  StandardCharsets.UTF_8)
              .trim();
    } catch (final Exception e) {
      this.disabled = true;
      return;
    }
    this.sut = new GitLabClient(hostUrl, apiToken);
  }

  @Test
  public void testGetIssue() throws GitChangelogIntegrationException {
    if (this.disabled) {
      return;
    }
    final String projectName = "tomas.bjerre85/violations-test";
    final int issueId = 1;

    final Optional<GitLabIssue> issueOpt = this.sut.getIssue(projectName, issueId);

    assertThat(issueOpt.isPresent()) //
        .isTrue();

    final GitLabIssue issue = issueOpt.get();
    LOG.info("\n" + issue.getTitle() + " " + issue.getLink() + " " + issue.getLabels());
    assertThat(issue.getLabels()) //
        .containsOnly("bug", "l1");
    assertThat(issue.getTitle()) //
        .isEqualTo("Test issue");
    assertThat(issue.getLink()) //
        .isEqualTo("https://gitlab.com/tomas.bjerre85/violations-test.git/issues/1");
  }
}
