package se.bjurr.gitchangelog.internal.integrations.redmine;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

public class RedmineClientIntegrationTest {

  private static final String EXISTING_REDMINE = "X";
  private static final String REDMINE_API_URL = "https://redmineserver/";
  private static final String USER = "U";
  private static final String PASSWORD = "P";
  private static final String TOKEN = "API_KEY";
  private static final String API_HEADER = "X-Redmine-API-Key";

  // @Test
  public void testThatIssueCanBeFound() throws Exception {
    final RedmineClient client =
        new DefaultRedmineClient(REDMINE_API_URL) //
            .withBasicCredentials(USER, PASSWORD);
    final java.util.Optional<RedmineIssue> issue = client.getIssue(EXISTING_REDMINE);
    assertThat(issue.get().getTitle()) //
        .isEqualTo("git-changelog redmine test");
  }

  // @Test
  public void testThatIssueCanBeFoundWithToken() throws Exception {
    final RedmineClient client =
        new DefaultRedmineClient(REDMINE_API_URL) //
            .withTokenCredentials(TOKEN);
    final java.util.Optional<RedmineIssue> issue = client.getIssue(EXISTING_REDMINE);
    assertThat(issue.get().getTitle()) //
        .isEqualTo("git-changelog redmine test");
  }

  // @Test
  public void testThatIssueCanBeFoundWithHeaders() throws Exception {

    Map<String, String> headers = new HashMap<>();
    headers.put(API_HEADER, TOKEN);
    final RedmineClient client =
        new DefaultRedmineClient(REDMINE_API_URL) //
            .withHeaders(headers);
    final java.util.Optional<RedmineIssue> issue = client.getIssue(EXISTING_REDMINE);
    assertThat(issue.get().getTitle()) //
        .isEqualTo("git-changelog redmine test");
  }
}
