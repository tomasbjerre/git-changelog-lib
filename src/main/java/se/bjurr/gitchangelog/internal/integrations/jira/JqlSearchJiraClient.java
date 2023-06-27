package se.bjurr.gitchangelog.internal.integrations.jira;

import static com.jayway.jsonpath.JsonPath.read;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.jayway.jsonpath.PathNotFoundException;

import se.bjurr.gitchangelog.internal.settings.SettingsJiraIssueFieldFilter;

public class JqlSearchJiraClient extends DefaultJiraClient {

    private List<SettingsJiraIssueFieldFilter> filters;

    public JqlSearchJiraClient(String api) {
        super(api);
    }

    @Override
    protected JiraIssue toJiraIssue(String issue, String json) {
        try {
            String title = read(json, "$.issues[0].fields.summary");
            String description = read(json, "$.issues[0].fields.description");
            String type = read(json, "$.issues[0].fields.issuetype.name");
            String link = getApi() + "/browse/" + issue;
            List<String> labels = read(json, "$.issues[0].fields.labels");
            List<String> linkedIssues = new ArrayList<>();
            List<String> inwardKey = read(json, "$.issues[0].fields.issuelinks[*].inwardIssue.key");
            List<String> outwardKey = read(json, "$.issues[0].fields.issuelinks[*].outwardIssue.key");
            linkedIssues.addAll(inwardKey);
            linkedIssues.addAll(outwardKey);

            final Map<String, Object> additionalFields =
                getIssueAdditionalFields().stream()
                    .collect(Collectors.toMap(Function.identity(), (field) -> read(json, "$.issues[0].fields." + field)));

            final JiraIssue jiraIssue =
                new JiraIssue(title, description, link, issue, type, linkedIssues, labels, additionalFields);

            return jiraIssue;
        } catch (PathNotFoundException e) {
            return null;
        }
    }


    public JiraClient withIssueFieldFilters(List<SettingsJiraIssueFieldFilter> filters)  {
        this.filters = filters;
        return this;
    }

    @Override
    protected String getEndpoint(String issue) {
        String endpoint = getApi()
                + "/rest/api/2/search?jql=issue="
                + issue
                + (hasIssueFieldFilters() ? getIssueFieldFiltersQuery() : "")
                + "&fields=parent,summary,issuetype,labels,description,issuelinks"
                + (hasIssueAdditionalFields() ? "," + getIssueAdditionalFieldsQuery() : "");
        return endpoint;
    }

    private boolean hasIssueFieldFilters() {
        return !filters.isEmpty();
    }

    private String getIssueFieldFiltersQuery() {
        String query = "";
        for (SettingsJiraIssueFieldFilter filter : filters) {
            query += " AND " + filter.getKey() + filter.getOperator() + "'" + filter.getValue() + "'";
        }
        try {
            return URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return query;
        }
    }
}
