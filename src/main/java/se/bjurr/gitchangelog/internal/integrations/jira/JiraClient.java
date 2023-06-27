package se.bjurr.gitchangelog.internal.integrations.jira;

import static com.jayway.jsonpath.JsonPath.read;

import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;

public abstract class JiraClient {

  private final String api;

  public JiraClient(final String api) {
    if (api.endsWith("/")) {
      this.api = api.substring(0, api.length() - 1);
    } else {
      this.api = api;
    }
  }

  public String getApi() {
    return this.api;
  }

  protected String getEndpoint(final String issue) {
    final String endpoint =
        this.api
            + "/rest/api/2/issue/"
            + issue
            + "?fields=parent,summary,issuetype,labels,description,issuelinks";
    return endpoint;
  }

  protected JiraIssue toJiraIssue(final String issue, final String json) {
    final String title = read(json, "$.fields.summary");
    final String description = read(json, "$.fields.description");
    final String type = read(json, "$.fields.issuetype.name");
    final String link = this.api + "/browse/" + issue;
    final List<String> labels = JsonPath.read(json, "$.fields.labels");
    final List<String> linkedIssues = new ArrayList<>();
    final List<String> inwardKey = JsonPath.read(json, "$.fields.issuelinks[*].inwardIssue.key");
    final List<String> outwardKey = JsonPath.read(json, "$.fields.issuelinks[*].outwardIssue.key");
    linkedIssues.addAll(inwardKey);
    linkedIssues.addAll(outwardKey);

    final JiraIssue jiraIssue =
        new JiraIssue(title, description, link, issue, type, linkedIssues, labels);
    return jiraIssue;
  }

  public abstract JiraClient withBasicCredentials(String username, String password);

  public abstract JiraClient withBearer(String bearerToken);

  public abstract JiraClient withTokenCredentials(String token);

  public abstract JiraClient withHeaders(Map<String, String> headers);

  public abstract JiraClient withAdditionalFields(Map<String, String> fields);

  public abstract Optional<JiraIssue> getIssue(String matched)
      throws GitChangelogIntegrationException;
}
