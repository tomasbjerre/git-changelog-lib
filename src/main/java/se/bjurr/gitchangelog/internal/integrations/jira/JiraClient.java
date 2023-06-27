package se.bjurr.gitchangelog.internal.integrations.jira;

import static com.jayway.jsonpath.JsonPath.read;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;
import se.bjurr.gitchangelog.internal.settings.SettingsJiraIssueFieldFilter;

public abstract class JiraClient {

  private final String api;
  private List<String> fields;

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
            + "?fields=parent,summary,issuetype,labels,description,issuelinks"
            + (hasIssueAdditionalFields() ? "," + getIssueAdditionalFieldsQuery() : "");
    return endpoint;
  }

  public JiraClient withIssueAdditionalFields(final List<String> fields) {
    this.fields = fields;
    return this;
  }

  protected List<String> getIssueAdditionalFields() {
    return fields;
  }

  protected boolean hasIssueAdditionalFields() {
      return fields != null && !fields.isEmpty();
  }

  protected String getIssueAdditionalFieldsQuery() {
    return String.join(",", fields);
  }

  protected JiraIssue toJiraIssue(final String issue, final String json) {
    final String title = read(json, "$.fields.summary");
    final String description = read(json, "$.fields.description");
    final String type = read(json, "$.fields.issuetype.name");
    final String link = this.api + "/browse/" + issue;
    final List<String> labels = read(json, "$.fields.labels");
    final List<String> linkedIssues = new ArrayList<>();
    final List<String> inwardKey = read(json, "$.fields.issuelinks[*].inwardIssue.key");
    final List<String> outwardKey = read(json, "$.fields.issuelinks[*].outwardIssue.key");
    linkedIssues.addAll(inwardKey);
    linkedIssues.addAll(outwardKey);

    final Map<String, Object> additionalFields =
        fields.stream().collect(Collectors.toMap(Function.identity(), (field) -> read(json, "$.fields." + field)));

    final JiraIssue jiraIssue =
        new JiraIssue(title, description, link, issue, type, linkedIssues, labels, additionalFields);
    return jiraIssue;
  }

  public abstract JiraClient withBasicCredentials(String username, String password);

  public abstract JiraClient withBearer(String bearerToken);

  public abstract JiraClient withTokenCredentials(String token);

  public abstract JiraClient withHeaders(Map<String, String> headers);

  public abstract JiraClient withIssueFieldFilters(List<SettingsJiraIssueFieldFilter> filters);

  public abstract Optional<JiraIssue> getIssue(String matched)
      throws GitChangelogIntegrationException;
}
