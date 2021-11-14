package se.bjurr.gitchangelog.internal.integrations.redmine;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;

public class RedmineClientTest {

  @Test
  public void testThatTrailingSlashIsRemoved() {
    final RedmineClient client = this.createClient("https://server.com/redmine/");
    assertThat(client.getApi()) //
        .isEqualTo("https://server.com/redmine");
  }

  @Test
  public void testThatNoTrailingSlashUrlIsUntouched() {
    final RedmineClient client = this.createClient("https://server.com/redmine");
    assertThat(client.getApi()) //
        .isEqualTo("https://server.com/redmine");
  }

  @Test
  public void testRemineMatcher() {

      Pattern p = Pattern.compile("#([0-9*])");
      final Matcher issueMatcher = p.matcher("refs #1234 ve #234 bişiler ve diğer şeyler");
      while (issueMatcher.find()) {
        final String matchedIssue = issueMatcher.group();
        if (matchedIssue.isEmpty()) {
          continue;
        }
        System.out.println(issueMatcher.group(1));
      }

  }

  private RedmineClient createClient(final String api) {
    return new RedmineClient(api) {

      @Override
      public RedmineClient withBasicCredentials(final String username, final String password) {
        return null;
      }

      @Override
      public RedmineClient withTokenCredentials(final String token) {
        return null;
      }

      @Override
      public RedmineClient withHeaders(final Map<String, String> headers) {
        return null;
      }

      @Override
      public Optional<RedmineIssue> getIssue(final String matched) throws GitChangelogIntegrationException {
        return null;
      }
    };
  }
}
