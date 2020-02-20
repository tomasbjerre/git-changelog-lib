package se.bjurr.gitchangelog.internal.integrations.jira;

import static com.jayway.jsonpath.JsonPath.read;

import com.google.common.base.Optional;
import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import java.util.List;
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
    String endpoint =
        api
            + "/rest/api/2/issue/"
            + issue
            + "?fields=parent,summary,issuetype,labels,description,issuelinks";
    return endpoint;
  }

  protected JiraIssue toJiraIssue(String issue, String json) {
    String title = read(json, "$.fields.summary");
    String description = read(json, "$.fields.description");
    String type = read(json, "$.fields.issuetype.name");
    String link = api + "/browse/" + issue;
    List<String> labels = JsonPath.read(json, "$.fields.labels");
    List<String> linkedIssues = new ArrayList<>();
    List<String> inwardKey = JsonPath.read(json, "$.fields.issuelinks[*].inwardIssue.key");
    List<String> outwardKey = JsonPath.read(json, "$.fields.issuelinks[*].outwardIssue.key");
    linkedIssues.addAll(inwardKey);
    linkedIssues.addAll(outwardKey);

    JiraIssue jiraIssue =
        new JiraIssue(title, description, link, issue, type, linkedIssues, labels);
    return jiraIssue;
  }

  public abstract JiraClient withBasicCredentials(String username, String password);

  public abstract JiraClient withTokenCredentials(String token);

  public abstract Optional<JiraIssue> getIssue(String matched)
      throws GitChangelogIntegrationException;
}
