package se.bjurr.gitchangelog.internal.integrations.jira;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;
import se.bjurr.gitchangelog.test.FileUtils;

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

  @Test
  public void testThatEndPointCanBeConstructed() {
    final JiraClient client = this.createClient("https://server.com/jira");
    assertThat(client.getEndpoint("ABC-123")) //
        .isEqualTo(
            "https://server.com/jira/rest/api/2/issue/ABC-123?fields=parent,summary,issuetype,labels,description,issuelinks");
  }

  @Test
  public void testThatEndPointCanBeConstructedWithExtraField() {
    final JiraClient client = this.createClient("https://server.com/jira");
    client.withIssueAdditionalFields(Arrays.asList("theextrafield"));
    assertThat(client.getEndpoint("ABC-123")) //
        .isEqualTo(
            "https://server.com/jira/rest/api/2/issue/ABC-123?fields=parent,summary,issuetype,labels,description,issuelinks,theextrafield");
  }

  @Test
  public void testThatEndPointCanBeConstructedWithExtraFields() {
    final JiraClient client = this.createClient("https://server.com/jira");
    client.withIssueAdditionalFields(Arrays.asList("theextrafield", "abcd", "xyz", "123"));
    assertThat(client.getEndpoint("ABC-123")) //
        .isEqualTo(
            "https://server.com/jira/rest/api/2/issue/ABC-123?fields=parent,summary,issuetype,labels,description,issuelinks,123,abcd,theextrafield,xyz");
  }

  @Test
  public void testThatJiraIssueCanBeConstructed() throws Exception {
    final JiraClient client = this.createClient("https://server.com/jira");
    client.withIssueAdditionalFields(Arrays.asList("theextrafield", "abcd", "xyz", "123"));

    final String json = FileUtils.readResource("/jira-issue-jir-5262.json");

    final JiraIssue actual = client.toJiraIssue("ABC-123", json);

    assertThat(actual.toString()) //
        .isEqualTo(
            "JiraIssue [title=The Title of jira 5262, link=https://server.com/jira/browse/ABC-123, issue=ABC-123, issueType=Bug, description=Description, linkedIssues=[JIRSD-490, JIRSD-567], labels=[], additionalFields={abcd=abcd adasd, theextrafield=theextrafield adasd, xyz=xyz adasd}]");
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
    };
  }
}
