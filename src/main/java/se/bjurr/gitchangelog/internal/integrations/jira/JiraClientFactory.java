package se.bjurr.gitchangelog.internal.integrations.jira;

public class JiraClientFactory {

 private static JiraClient jiraClient;

 public static JiraClient createJiraClient(String apiUrl) {
  if (jiraClient == null) {
   jiraClient = new JiraClient(apiUrl);
  }
  return jiraClient;
 }

}
