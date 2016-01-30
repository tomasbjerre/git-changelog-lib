package se.bjurr.gitchangelog.internal.integrations.github;

public class GitHubClientFactory {

 private static GitHubClient gitHubClient;

 public static void reset() {
  gitHubClient = null;
 }

 public static GitHubClient createGitHubClient(String apiUrl) {
  if (gitHubClient == null) {
   gitHubClient = new GitHubClient(apiUrl);
  }
  return gitHubClient;
 }

}
