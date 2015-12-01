package se.bjurr.gitchangelog.internal.integrations.github;

public class GitHubClientFactory {

 private static GitHubClient gitHubClient;

 public static GitHubClient createGitHubClient(String apiUrl) {
  if (gitHubClient == null) {
   gitHubClient = new GitHubClient(apiUrl);
  }
  return gitHubClient;
 }

}
