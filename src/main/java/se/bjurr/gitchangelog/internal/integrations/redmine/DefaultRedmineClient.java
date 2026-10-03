package se.bjurr.gitchangelog.internal.integrations.redmine;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClient;

public class DefaultRedmineClient extends RedmineClient {

  private RestClient client;

  public DefaultRedmineClient(final String api) {
    super(api);
    this.client = new RestClient();
  }

  @Override
  public RedmineClient withBasicCredentials(final String username, final String password) {
    this.client = this.client.withBasicAuthCredentials(username, password);
    return this;
  }

  @Override
  public RedmineClient withTokenCredentials(final String token) {
    final String authToken =
        Base64.getEncoder().encodeToString((token + ":changelog").getBytes(StandardCharsets.UTF_8));
    this.client = this.client.withTokenAuthCredentials(authToken);
    return this;
  }

  @Override
  public RedmineClient withHeaders(final Map<String, String> headers) {
    this.client = this.client.withHeaders(headers);
    return this;
  }

  @Override
  public Optional<RedmineIssue> getIssue(final String issue)
      throws GitChangelogIntegrationException {
    final String endpoint = this.getEndpoint(issue);
    final Optional<String> json = this.client.get(endpoint);
    if (json.isPresent()) {
      final String jsonString = json.get();
      try {
        final RedmineIssue redmineIssue = this.toRedmineIssue(issue, jsonString);
        return Optional.of(redmineIssue);
      } catch (final Exception e) {
        throw new GitChangelogIntegrationException("Unable to parse:\n" + jsonString, e);
      }
    }
    return Optional.empty();
  }
}
