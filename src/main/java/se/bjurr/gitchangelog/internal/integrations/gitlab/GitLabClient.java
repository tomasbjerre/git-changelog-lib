package se.bjurr.gitchangelog.internal.integrations.gitlab;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

public class GitLabClient {

  private static final JsonMapper JSON_MAPPER = new JsonMapper();

  private final String api;
  private final RestClient client;
  private final Map<String, String> headers = new HashMap<>();

  public GitLabClient(final String hostUrl, final String apiToken) {
    final String trimmed =
        hostUrl.endsWith("/") ? hostUrl.substring(0, hostUrl.length() - 1) : hostUrl;
    this.api = trimmed + "/api/v4";
    this.client = new RestClient();
    if (apiToken != null && !apiToken.isEmpty()) {
      this.headers.put("PRIVATE-TOKEN", apiToken);
      this.client.withHeaders(this.headers);
    }
  }

  public GitLabClient withHeaders(final Map<String, String> headers) {
    if (headers != null) {
      this.headers.putAll(headers);
      this.client.withHeaders(this.headers);
    }
    return this;
  }

  public Optional<GitLabIssue> getIssue(final String projectName, final Integer matchedIssue)
      throws GitChangelogIntegrationException {
    final String endpoint =
        this.api
            + "/projects/"
            + URLEncoder.encode(projectName, StandardCharsets.UTF_8)
            + "/issues/"
            + matchedIssue;
    final Optional<String> json = this.client.get(endpoint);
    if (json.isEmpty()) {
      return Optional.empty();
    }
    final String jsonString = json.get();
    try {
      final JsonNode node = JSON_MAPPER.readTree(jsonString);
      final String title = node.get("title").asString();
      final String webUrl = node.get("web_url").asString();
      final List<String> labels = new ArrayList<>();
      final JsonNode labelsNode = node.get("labels");
      if (labelsNode != null) {
        for (final JsonNode label : labelsNode) {
          labels.add(label.asString());
        }
      }
      return Optional.of(new GitLabIssue(title, webUrl, labels));
    } catch (final Exception e) {
      throw new GitChangelogIntegrationException("Unable to parse:\n" + jsonString, e);
    }
  }
}
