package se.bjurr.gitchangelog.internal.integrations.jira;

import static com.jayway.jsonpath.JsonPath.read;

import com.google.common.base.Optional;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;

public abstract class JiraClient {

  private final String api;

  public JiraClient(String api) {
    if (api.endsWith("/")) {
      this.api = api.substring(0, api.length() - 1);
    } else {
      this.api = api;
    }
  }

  public String getApi() {
    return api;
  }

  protected String getEndpoint(String issue) {
    String endpoint = api + "/rest/api/2/issue/" + issue + "?fields=parent,summary,issuetype";
    return endpoint;
  }

  protected JiraIssue toJiraIssue(String issue, String json) {
    String title = read(json, "$.fields.summary");
    String type = read(json, "$.fields.issuetype.name");
    String link = api + "/browse/" + issue;
    JiraIssue jiraIssue = new JiraIssue(title, link, issue, type);
    return jiraIssue;
  }

  public abstract JiraClient withBasicCredentials(String username, String password);

  public abstract Optional<JiraIssue> getIssue(String matched)
      throws GitChangelogIntegrationException;
}
