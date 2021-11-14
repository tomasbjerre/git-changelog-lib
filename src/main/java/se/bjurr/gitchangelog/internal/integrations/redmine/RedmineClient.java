package se.bjurr.gitchangelog.internal.integrations.redmine;

import static com.jayway.jsonpath.JsonPath.read;

import java.util.Map;
import java.util.Optional;

import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;

public abstract class RedmineClient {
    private final String api;

    public RedmineClient(final String api) {
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
        final String issueNo = getIssueNumber(issue);
        final String endpoint = this.api + "/issues/" + issueNo + ".json";
        return endpoint;
    }

    protected RedmineIssue toRedmineIssue(final String issue, final String json) {
        final String issueNo = getIssueNumber(issue);
        final String title = read(json, "$.issue.subject");
        final String description = read(json, "$.issue.description");
        final String type = read(json, "$.issue.tracker.name");
        final String link = this.api + "/issues/" + issueNo;

        final RedmineIssue redmineIssue = new RedmineIssue(title, description, link, issue, type);
        return redmineIssue;
    }

    protected String getIssueNumber( String issue ){
        return issue.startsWith("#") ? issue.substring(1) : issue;
    }

    public abstract RedmineClient withBasicCredentials(String username, String password);

    public abstract RedmineClient withTokenCredentials(String token);

    public abstract RedmineClient withHeaders(Map<String, String> headers);

    public abstract Optional<RedmineIssue> getIssue(String matched) throws GitChangelogIntegrationException;
}
