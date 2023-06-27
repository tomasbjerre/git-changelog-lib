package se.bjurr.gitchangelog.internal.integrations.jira;

import java.util.Map;
import java.util.Optional;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClient;

public class DefaultJiraClient extends JiraClient {

  private RestClient client;

  public DefaultJiraClient(final String api) {
    super(api);
    this.client = new RestClient();
  }

  @Override
  public JiraClient withBasicCredentials(final String username, final String password) {
    this.client = this.client.withBasicAuthCredentials(username, password);
    return this;
  }

  @Override
  public JiraClient withBearer(final String bearerToken) {
    this.client = this.client.withBearer(bearerToken);
    return this;
  }

  @Override
  public JiraClient withTokenCredentials(final String token) {
    this.client = this.client.withTokenAuthCredentials(token);
    return this;
  }

  @Override
  public JiraClient withHeaders(final Map<String, String> headers) {
    this.client = this.client.withHeaders(headers);
    return this;
  }

  @Override
  public Optional<JiraIssue> getIssue(final String issue) throws GitChangelogIntegrationException {
    final String endpoint = this.getEndpoint(issue);
    final Optional<String> json = this.client.get(endpoint);
    if (json.isPresent()) {
      final String jsonString = json.get();
      try {
        final JiraIssue jiraIssue = this.toJiraIssue(issue, jsonString);
        return Optional.of(jiraIssue);
      } catch (final Exception e) {
        throw new GitChangelogIntegrationException("Unable to parse:\n" + jsonString, e);
      }
    }
    return Optional.empty();
  }

  public JiraClient withAdditionalFields(Map<String, String> fields) {
    return this;
  }
}
