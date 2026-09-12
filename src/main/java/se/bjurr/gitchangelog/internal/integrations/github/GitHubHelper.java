package se.bjurr.gitchangelog.internal.integrations.github;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

public class GitHubHelper {

  private static final JsonMapper JSON_MAPPER = new JsonMapper();
  private static final int PER_PAGE = 100;

  private final String api;
  private final RestClient client;
  private final Map<String, String> headers = new HashMap<>();

  public GitHubHelper(final String api, final Optional<String> token) {
    this.api = api.endsWith("/") ? api : api + "/";
    this.client = new RestClient();
    if (token != null && token.isPresent() && !token.get().isEmpty()) {
      this.headers.put("Authorization", "token " + token.get());
      this.client.withHeaders(this.headers);
    }
  }

  public GitHubHelper withHeaders(final Map<String, String> headers) {
    if (headers != null) {
      this.headers.putAll(headers);
      this.client.withHeaders(this.headers);
    }
    return this;
  }

  public Optional<GitHubIssue> getIssueFromAll(String issue)
      throws GitChangelogIntegrationException {
    if (issue.startsWith("#")) {
      issue = issue.substring(1);
    }

    int page = 1;
    while (true) {
      final String endpoint = this.api + "issues?state=all&per_page=" + PER_PAGE + "&page=" + page;
      final Optional<String> json = this.client.get(endpoint);
      if (json.isEmpty()) {
        return Optional.empty();
      }

      try {
        final JsonNode issuesNode = JSON_MAPPER.readTree(json.get());
        int count = 0;
        for (final JsonNode issueNode : issuesNode) {
          count++;
          if (issue.equals(issueNode.get("number").asString())) {
            return Optional.of(this.toGitHubIssue(issueNode));
          }
        }
        if (count < PER_PAGE) {
          return Optional.empty();
        }
      } catch (final Exception e) {
        throw new GitChangelogIntegrationException(issue, e);
      }
      page++;
    }
  }

  private GitHubIssue toGitHubIssue(final JsonNode issueNode) {
    final String title = issueNode.get("title").asString();
    final String link = issueNode.get("html_url").asString();
    final String number = issueNode.get("number").asString();
    final List<GitHubLabel> labels = new ArrayList<>();
    final JsonNode labelsNode = issueNode.get("labels");
    if (labelsNode != null) {
      for (final JsonNode labelNode : labelsNode) {
        labels.add(new GitHubLabel(labelNode.get("name").asString()));
      }
    }
    return new GitHubIssue(title, link, number, labels);
  }
}
