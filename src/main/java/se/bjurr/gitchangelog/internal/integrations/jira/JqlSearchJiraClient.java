package se.bjurr.gitchangelog.internal.integrations.jira;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static com.jayway.jsonpath.JsonPath.read;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;
import se.bjurr.gitchangelog.internal.integrations.rest.RestClient;

public class JqlSearchJiraClient extends JiraClient {

    private RestClient client;
    private Map<String, String> fieldMap;

    public JqlSearchJiraClient(String api) {
        super(api);
        this.client = new RestClient(1, MINUTES);
    }

    protected JiraIssue toJiraIssue(String issue, String json) {
        try {
            String title = read(json, "$.issues[0].fields.summary");
            String description = read(json, "$.issues[0].fields.description");
            String type = read(json, "$.issues[0].fields.issuetype.name");
            String link = getApi() + "/browse/" + issue;
            List<String> labels = JsonPath.read(json, "$.issues[0].fields.labels");
            List<String> linkedIssues = new ArrayList<>();
            List<String> inwardKey = JsonPath.read(json, "$.issues[0].fields.issuelinks[*].inwardIssue.key");
            List<String> outwardKey = JsonPath.read(json, "$.issues[0].fields.issuelinks[*].outwardIssue.key");
            linkedIssues.addAll(inwardKey);
            linkedIssues.addAll(outwardKey);

            JiraIssue jiraIssue = new JiraIssue(title, description, link, issue, type, linkedIssues, labels);
            return jiraIssue;
        } catch (PathNotFoundException e) {
            return null;
        }
    }

    @Override
    public JiraClient withBasicCredentials(String username, String password) {
        this.client = client.withBasicAuthCredentials(username, password);
        return this;
    }

    @Override
    public JiraClient withTokenCredentials(String token) {
        this.client = client.withTokenAuthCredentials(token);
        return this;
    }

    @Override
    public JiraClient withHeaders(Map<String, String> headers) {
        this.client = client.withHeaders(headers);
        return this;
    }

    public JiraClient withAdditionalFields(Map<String, String> fields) {
        this.fieldMap = fields;
        return this;
    }

    @Override
    public Optional<JiraIssue> getIssue(String issue) throws GitChangelogIntegrationException {
        String endpoint = getEndpoint(issue);
        Optional<String> json = client.get(endpoint);
        if (json.isPresent()) {
            JiraIssue jiraIssue = toJiraIssue(issue, json.get());
            return fromNullable(jiraIssue);
        }
        return absent();
    }

    protected String getEndpoint(String issue) {
        String endpoint = getApi()
                + "/rest/api/2/search?jql=issue="
                + issue
                + (hasAdditionalFields() ? getAdditionalFieldsQuery() : "")
                + "&fields=parent,summary,issuetype,labels,description,issuelinks";
        return endpoint;
    }

    private boolean hasAdditionalFields() {
        return !fieldMap.isEmpty();
    }

    private String getAdditionalFieldsQuery() {
        String query = "";
        for (Map.Entry<String, String> field : fieldMap.entrySet()) {
            query += " AND " + field.getKey() + "='" + field.getValue() + "'";
        }
        try {
            return URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return query;
        }
    }
}
