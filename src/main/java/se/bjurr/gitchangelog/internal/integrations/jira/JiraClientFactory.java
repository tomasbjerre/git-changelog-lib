package se.bjurr.gitchangelog.internal.integrations.jira;

import se.bjurr.gitchangelog.internal.settings.Settings;

public class JiraClientFactory {

  private static JiraClient jiraClient;

  public static void reset() {
    jiraClient = null;
  }

  /** The Bitbucket Server plugin uses this method to inject the Atlassian Client. */
  public static void setJiraClient(final JiraClient jiraClient) {
    JiraClientFactory.jiraClient = jiraClient;
  }

  public static JiraClient createJiraClient(Settings settings) {
    if (jiraClient != null) {
      return jiraClient;
    }
    if (!settings.getJiraIssueFieldsFilter().isEmpty()) {
      return new JqlSearchJiraClient(settings.getJiraServer().get());
    } else {
      return new DefaultJiraClient(settings.getJiraServer().get());
    }
  }
}
