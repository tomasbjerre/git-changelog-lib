package se.bjurr.gitchangelog.internal.integrations.jira;

import static com.jayway.jsonpath.JsonPath.read;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jayway.jsonpath.PathNotFoundException;

import org.slf4j.Logger;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;
import se.bjurr.gitchangelog.internal.settings.SettingsJiraIssueFieldFilter;

public abstract class JiraClient {

  private static final Logger LOG = getLogger(JiraClient.class);

  private final String api;
  private List<String> fields = List.of();

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
    return this.api
            + "/rest/api/2/issue/"
            + issue
            + "?fields=parent,summary,issuetype,labels,description,issuelinks"
            + (hasIssueAdditionalFields() ? "," + getIssueAdditionalFieldsQuery() : "");
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

  protected String getFieldPrefix() {
    return "$.fields.";
  }

  protected JiraIssue toJiraIssue(final String issue, final String json) {
    final String fieldPrefix = getFieldPrefix();

    final String title = read(json, fieldPrefix + "summary");
    final String description = read(json, fieldPrefix + "description");
    final String type = read(json, fieldPrefix + "issuetype.name");
    final String link = this.api + "/browse/" + issue;
    final List<String> labels = read(json, fieldPrefix + "labels");
    final List<String> linkedIssues = new ArrayList<>();
    final List<String> inwardKey = read(json, fieldPrefix + "issuelinks[*].inwardIssue.key");
    final List<String> outwardKey = read(json, fieldPrefix + "issuelinks[*].outwardIssue.key");
    linkedIssues.addAll(inwardKey);
    linkedIssues.addAll(outwardKey);

    final Map<String, Object> additionalFields =
        fields.stream().reduce(
            (Map<String, Object>) new HashMap<String, Object>(),
              (fields, field) -> getAdditionalField(json, fieldPrefix, fields, field),
              (leftSide, rightSide) -> 
                  Stream.of(leftSide, rightSide)
                      .map(Map::entrySet)
                      .flatMap(Collection::stream)
                      .collect(
                          Collectors.toMap(
                              Map.Entry::getKey,
                              Map.Entry::getValue)));

    return new JiraIssue(title, description, link, issue, type, linkedIssues, labels, additionalFields);
  }

  private Map<String, Object> getAdditionalField(
      final String json,
      String fieldPrefix,
      final Map<String, Object> additionalFields,
      final String additionalFieldString) {
    try {
      additionalFields.put(additionalFieldString, read(json, fieldPrefix + additionalFieldString));
    } catch (PathNotFoundException e) {
      LOG.warn("Could not find the additional field", e);
    }

    return additionalFields;
  }

  public abstract JiraClient withBasicCredentials(String username, String password);

  public abstract JiraClient withBearer(String bearerToken);

  public abstract JiraClient withTokenCredentials(String token);

  public abstract JiraClient withHeaders(Map<String, String> headers);

  public abstract JiraClient withIssueFieldFilters(List<SettingsJiraIssueFieldFilter> filters);

  public abstract Optional<JiraIssue> getIssue(String matched)
      throws GitChangelogIntegrationException;
}
