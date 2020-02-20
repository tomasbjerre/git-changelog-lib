package se.bjurr.gitchangelog.internal.integrations.jira;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.base.Optional;
import org.junit.Test;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;

public class JiraClientTest {

  @Test
  public void testThatTrailingSlashIsRemoved() {
    JiraClient client = createClient("https://server.com/jira/");
    assertThat(client.getApi()) //
        .isEqualTo("https://server.com/jira");
  }

  @Test
  public void testThatNoTrailingSlashUrlIsUntouched() {
    JiraClient client = createClient("https://server.com/jira");
    assertThat(client.getApi()) //
        .isEqualTo("https://server.com/jira");
  }

  private JiraClient createClient(String api) {
    return new JiraClient(api) {

      @Override
      public JiraClient withBasicCredentials(String username, String password) {
        return null;
      }

      @Override
      public JiraClient withTokenCredentials(String token) {
        return null;
      }

      @Override
      public Optional<JiraIssue> getIssue(String matched) throws GitChangelogIntegrationException {
        return null;
      }
    };
  }
}
