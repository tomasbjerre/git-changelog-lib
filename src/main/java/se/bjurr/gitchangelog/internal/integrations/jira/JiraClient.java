package se.bjurr.gitchangelog.internal.integrations.jira;

import static com.jayway.jsonpath.JsonPath.read;

import com.google.common.base.Optional;

public abstract class JiraClient {

 private final String api;

 public JiraClient(String api) {
  this.api = api;
 }

 public String getApi() {
  return api;
 }

 protected String getEndpoint(String issue) {
  String endpoint = api + "/rest/api/2/issue/" + issue + "?fields=parent,summary";
  return endpoint;
 }

 protected JiraIssue toJiraIssue(String issue, Optional<String> json) {
  String title = read(json.get(), "$.fields.summary");
  String link = api + "/browse/";
  JiraIssue jiraIssue = new JiraIssue(title, link, issue);
  return jiraIssue;
 }

 public abstract void withBasicCredentials(String username, String password);

 public abstract Optional<JiraIssue> getIssue(String matched);

}
