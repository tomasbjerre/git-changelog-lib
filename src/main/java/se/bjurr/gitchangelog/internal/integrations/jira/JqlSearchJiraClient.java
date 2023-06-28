package se.bjurr.gitchangelog.internal.integrations.jira;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import se.bjurr.gitchangelog.internal.settings.SettingsJiraIssueFieldFilter;

public class JqlSearchJiraClient extends DefaultJiraClient {

    private List<SettingsJiraIssueFieldFilter> filters = List.of();

    public JqlSearchJiraClient(String api) {
        super(api);
    }

    protected String getFieldPrefix() {
        return "$.issues[0].fields.";
    }

    public JiraClient withIssueFieldFilters(List<SettingsJiraIssueFieldFilter> filters)  {
        this.filters = filters;
        return this;
    }

    @Override
    protected String getEndpoint(String issue) {
        return getApi()
                + "/rest/api/2/search?jql=issue="
                + issue
                + (hasIssueFieldFilters() ? getIssueFieldFiltersQuery() : "")
                + "&fields=parent,summary,issuetype,labels,description,issuelinks"
                + (hasIssueAdditionalFields() ? "," + getIssueAdditionalFieldsQuery() : "");
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
