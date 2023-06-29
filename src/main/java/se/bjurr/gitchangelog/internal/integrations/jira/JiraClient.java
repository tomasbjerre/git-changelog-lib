package se.bjurr.gitchangelog.internal.integrations.jira;

import static com.jayway.jsonpath.JsonPath.read;
import static org.slf4j.LoggerFactory.getLogger;

import com.jayway.jsonpath.PathNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;
import se.bjurr.gitchangelog.internal.settings.SettingsJiraIssueFieldFilter;

public abstract class JiraClient {
  private static final String SEARCH_API = "/search?jql=issue=";
  private static final String ISSUE_API = "/issue/";
  private static final String ISSUE_API_FIELD_PREFIX = "$.fields.";
  private static final String SEARCH_API_FIELD_PREFIX = "$.issues[0].fields.";
  private static final String AND = " AND ";
  private static final String SINGLE_QUOTE = "'";
  private static final String COMMA = ",";
  private static final String QUESTION_MARK = "?";
  private static final String AMPERSAND = "&";
  private static final String EMPTY_STRING = "";
  private static final String DEFAULT_FIELDS =
      "fields=parent,summary,issuetype,labels,description,issuelinks";
  private static final String BASE_PATH = "/rest/api/2";
  private static final Logger LOG = getLogger(JiraClient.class);

  private final String api;
  private List<String> fields = Collections.unmodifiableList(new ArrayList<>());
  private List<SettingsJiraIssueFieldFilter> filters =
      Collections.unmodifiableList(new ArrayList<>());

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
    return this.hasIssueFieldFilters() ? SEARCH_API : ISSUE_API;
  }

  protected String getEndpoint(final String issue) {
    return this.api
        + BASE_PATH
        + getIssuePath()
        + issue
        + (hasIssueFieldFilters() ? getIssueFieldFiltersQuery() + AMPERSAND : QUESTION_MARK)
        + DEFAULT_FIELDS
        + (hasIssueAdditionalFields() ? COMMA + getIssueAdditionalFieldsQuery() : EMPTY_STRING);
  }

  private boolean hasIssueFieldFilters() {
    return this.filters != null && !filters.isEmpty();
  }

  private String getIssueFieldFiltersQuery() {
    final StringBuffer queryBuffer = new StringBuffer();

    for (SettingsJiraIssueFieldFilter filter : filters) {
      queryBuffer.append(
          AND
              + filter.getKey()
              + filter.getOperator()
              + SINGLE_QUOTE
              + filter.getValue()
              + SINGLE_QUOTE);
    }
    try {
      return URLEncoder.encode(queryBuffer.toString(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return "";
    }
  }

  public JiraClient withIssueAdditionalFields(final List<String> fields) {
    this.fields = fields;
    return this;
  }

  public JiraClient withIssueFieldFilters(final List<SettingsJiraIssueFieldFilter> filters) {
    this.filters = filters;
    return this;
  }

  private boolean hasIssueAdditionalFields() {
    return fields != null && !fields.isEmpty();
  }

  private String getIssueAdditionalFieldsQuery() {
    return String.join(COMMA, fields);
  }

  private String getFieldPrefix() {
    return this.hasIssueFieldFilters() ? SEARCH_API_FIELD_PREFIX : ISSUE_API_FIELD_PREFIX;
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
        fields.stream()
            .reduce(
                (Map<String, Object>) new HashMap<String, Object>(),
                (fields, field) -> getAdditionalField(json, fieldPrefix, fields, field),
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
    } catch (PathNotFoundException e) {
      LOG.warn("Could not find the additional field", e);
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
