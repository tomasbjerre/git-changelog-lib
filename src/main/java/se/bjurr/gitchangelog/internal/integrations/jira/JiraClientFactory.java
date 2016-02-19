package se.bjurr.gitchangelog.internal.integrations.jira;

public class JiraClientFactory {

 private static JiraClient jiraClient;

 public static void reset() {
  jiraClient = null;
 }

 /**
  * The Bitbucket Server plugin uses this method to inject the Atlassian Client.
  */
 public static void setJiraClient(JiraClient jiraClient) {
  JiraClientFactory.jiraClient = jiraClient;
 }

 public static JiraClient createJiraClient(String apiUrl) {
  if (jiraClient == null) {
   jiraClient = new DefaultJiraClient(apiUrl);
  }
  return jiraClient;
 }

}
