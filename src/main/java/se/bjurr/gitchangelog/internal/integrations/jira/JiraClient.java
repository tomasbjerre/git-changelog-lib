package se.bjurr.gitchangelog.internal.integrations.jira;

import static com.jayway.jsonpath.JsonPath.read;
import static org.slf4j.LoggerFactory.getLogger;

import com.jayway.jsonpath.PathNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;

public abstract class JiraClient {
  private static final String ISSUE_API = "/issue/";
  private static final String ISSUE_API_FIELD_PREFIX = "$.fields.";
  private static final String COMMA = ",";
  private static final String QUESTION_MARK = "?";
  private static final String EMPTY_STRING = "";
  private static final String DEFAULT_FIELDS =
      "fields=parent,summary,issuetype,labels,description,issuelinks";
  private static final String BASE_PATH = "/rest/api/2";
  private static final Logger LOG = getLogger(JiraClient.class);

  private final String api;
  private List<String> fields = Collections.unmodifiableList(new ArrayList<>());

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

  protected String getIssuePath() {
    return JiraClient.ISSUE_API;
  }

  protected String getEndpoint(final String issue) {
    return this.api
        + JiraClient.BASE_PATH
        + this.getIssuePath()
        + issue
        + JiraClient.QUESTION_MARK
        + JiraClient.DEFAULT_FIELDS
        + (this.hasIssueAdditionalFields()
            ? JiraClient.COMMA + this.getIssueAdditionalFieldsQuery()
            : JiraClient.EMPTY_STRING);
  }

  public JiraClient withIssueAdditionalFields(final List<String> fields) {
    final List<String> newFields = new ArrayList<String>(fields);
    Collections.sort(newFields);
    this.fields = Collections.unmodifiableList(newFields);
    return this;
  }

  private boolean hasIssueAdditionalFields() {
    return this.fields != null && !this.fields.isEmpty();
  }

  private String getIssueAdditionalFieldsQuery() {
    return String.join(JiraClient.COMMA, this.fields);
  }

  private String getFieldPrefix() {
    return JiraClient.ISSUE_API_FIELD_PREFIX;
  }

  protected JiraIssue toJiraIssue(final String issue, final String json) {
    final String fieldPrefix = this.getFieldPrefix();

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
        this.fields.stream()
            .reduce(
                (Map<String, Object>) new TreeMap<String, Object>(),
                (fields, field) -> this.getAdditionalField(json, fieldPrefix, fields, field),
                (leftSide, rightSide) ->
                    Stream.of(leftSide, rightSide)
                        .map(Map::entrySet)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

    return new JiraIssue(
        title, description, link, issue, type, linkedIssues, labels, additionalFields);
  }

  private Map<String, Object> getAdditionalField(
      final String json,
      final String fieldPrefix,
      final Map<String, Object> additionalFields,
      final String additionalFieldString) {
    try {
      additionalFields.put(additionalFieldString, read(json, fieldPrefix + additionalFieldString));
    } catch (final PathNotFoundException e) {
      JiraClient.LOG.warn("Could not find the additional field", e);
    }

    return additionalFields;
  }

  public abstract JiraClient withBasicCredentials(String username, String password);

  public abstract JiraClient withBearer(String bearerToken);

  public abstract JiraClient withTokenCredentials(String token);

  public abstract JiraClient withHeaders(Map<String, String> headers);

  public abstract Optional<JiraIssue> getIssue(String matched)
      throws GitChangelogIntegrationException;
}
