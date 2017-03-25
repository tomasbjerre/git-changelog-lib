package se.bjurr.gitchangelog.internal.integrations.gitlab;

import static com.google.common.base.Charsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.base.Optional;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;

public class GitLabClientTest {

  private GitLabClient sut;
  private boolean disabled;

  @Before
  public void before() throws IOException {
    String hostUrl = "https://gitlab.com/";
    String apiToken = null;
    try {
      apiToken = Files.toString(new File("/home/bjerre/gitlabapitoken.txt"), UTF_8).trim();
    } catch (Exception e) {
      disabled = true;
      return;
    }
    this.sut = new GitLabClient(hostUrl, apiToken);
  }

  @Test
  public void testGetIssue() throws GitChangelogIntegrationException {
    if (disabled) {
      return;
    }
    String projectName = "violations-test";
    int issueId = 1;

    Optional<GitLabIssue> issueOpt = sut.getIssue(projectName, issueId);

    assertThat(issueOpt.isPresent()) //
        .isTrue();

    GitLabIssue issue = issueOpt.get();
    assertThat(issue.getLabels()) //
        .containsOnly("bug", "l1");
    assertThat(issue.getTitle()) //
        .isEqualTo("Test issue");
    assertThat(issue.getLink()) //
        .isEqualTo("https://gitlab.com/tomas.bjerre85/violations-test.git/issues/1");
  }
}
