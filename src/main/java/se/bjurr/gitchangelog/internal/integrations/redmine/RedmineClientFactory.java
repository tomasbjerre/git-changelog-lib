package se.bjurr.gitchangelog.internal.integrations.redmine;

public class RedmineClientFactory {

  private static RedmineClient redmineClient;

  public static void reset() {
    redmineClient = null;
  }

  /** The Bitbucket Server plugin uses this method to inject the Atlassian Client. */
  public static void setRedmineClient(final RedmineClient redmineClient) {
    RedmineClientFactory.redmineClient = redmineClient;
  }

  public static RedmineClient createRedmineClient(final String apiUrl) {
    if (redmineClient != null) {
      return redmineClient;
    }
    return new DefaultRedmineClient(apiUrl);
  }
}
