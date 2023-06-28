package se.bjurr.gitchangelog.internal.integrations.jira;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Test;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;
import se.bjurr.gitchangelog.internal.settings.SettingsJiraIssueFieldFilter;

public class JiraClientTest {

  @Test
  public void testThatTrailingSlashIsRemoved() {
    final JiraClient client = this.createClient("https://server.com/jira/");
    assertThat(client.getApi()) //
        .isEqualTo("https://server.com/jira");
  }

  @Test
  public void testThatNoTrailingSlashUrlIsUntouched() {
    final JiraClient client = this.createClient("https://server.com/jira");
    assertThat(client.getApi()) //
        .isEqualTo("https://server.com/jira");
  }

  private JiraClient createClient(final String api) {
    return new JiraClient(api) {

      @Override
      public JiraClient withBasicCredentials(final String username, final String password) {
        return null;
      }

      @Override
      public JiraClient withBearer(final String bearer) {
        return null;
      }

      @Override
      public JiraClient withTokenCredentials(final String token) {
        return null;
      }

      @Override
      public JiraClient withHeaders(final Map<String, String> headers) {
        return null;
      }

      @Override
      public Optional<JiraIssue> getIssue(final String matched)
          throws GitChangelogIntegrationException {
        return null;
      }

      @Override
      public JiraClient withIssueFieldFilters(List<SettingsJiraIssueFieldFilter> filters) {
        return null;
      }
    };
  }
}
