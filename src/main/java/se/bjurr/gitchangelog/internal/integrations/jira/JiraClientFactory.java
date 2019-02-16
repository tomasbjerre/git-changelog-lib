package se.bjurr.gitchangelog.internal.integrations.jira;

public class JiraClientFactory {

  private static JiraClient jiraClient;

  public static void reset() {
    jiraClient = null;
  }

  /** The Bitbucket Server plugin uses this method to inject the Atlassian Client. */
  public static void setJiraClient(final JiraClient jiraClient) {
    JiraClientFactory.jiraClient = jiraClient;
  }

  public static JiraClient createJiraClient(final String apiUrl) {
    if (jiraClient != null) {
      return jiraClient;
    }
    return new DefaultJiraClient(apiUrl);
  }
}
