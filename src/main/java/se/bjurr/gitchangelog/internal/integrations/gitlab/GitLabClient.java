package se.bjurr.gitchangelog.internal.integrations.gitlab;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static java.util.concurrent.TimeUnit.MINUTES;

import com.google.common.base.Optional;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabIssue;
import org.gitlab.api.models.GitlabProject;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;

public class GitLabClient {

  private static LoadingCache<GitLabProjectIssuesCacheKey, List<GitlabIssue>> cache =
      newBuilder()
          .maximumSize(10)
          .expireAfterWrite(1, MINUTES)
          .build(
              new CacheLoader<GitLabProjectIssuesCacheKey, List<GitlabIssue>>() {
                @Override
                public List<GitlabIssue> load(GitLabProjectIssuesCacheKey cacheKey)
                    throws IOException {
                  return getAllIssues(cacheKey);
                }
              });
  private final String hostUrl;
  private final String apiToken;

  public GitLabClient(String hostUrl, String apiToken) {
    this.hostUrl = hostUrl;
    this.apiToken = apiToken;
  }

  public Optional<GitLabIssue> getIssue(String projectName, Integer matchedIssue)
      throws GitChangelogIntegrationException {
    GitlabAPI gitLabApi = GitlabAPI.connect(hostUrl, apiToken);
    GitlabProject project;
    try {
      project = gitLabApi.getProject(projectName);
    } catch (Exception e) {
      throw new GitChangelogIntegrationException(
          "Unable to find project \""
              + projectName
              + "\". It should be \"tomas.bjerre85/violations-test\" for a repo like: https://gitlab.com/tomas.bjerre85/violations-test",
          e);
    }
    Integer projectId = project.getId();
    String httpUrl = project.getHttpUrl();
    try {
      List<GitlabIssue> issues =
          cache.get(new GitLabProjectIssuesCacheKey(hostUrl, apiToken, projectId));
      for (GitlabIssue candidate : issues) {
        if (candidate.getIid() == matchedIssue) {
          return Optional.of(createGitLabIssue(httpUrl, candidate));
        }
      }
      return Optional.absent();
    } catch (Exception e) {
      throw new GitChangelogIntegrationException(e.getMessage(), e);
    }
  }

  private GitLabIssue createGitLabIssue(String projectUrl, GitlabIssue candidate) {
    String title = candidate.getTitle();
    String link = projectUrl + "/issues/" + candidate.getIid();
    List<String> labels = new ArrayList<>();
    for (String l : candidate.getLabels()) {
      labels.add(l);
    }
    return new GitLabIssue(title, link, labels);
  }

  private static List<GitlabIssue> getAllIssues(GitLabProjectIssuesCacheKey cacheKey)
      throws IOException {
    String hostUrl = cacheKey.getHostUrl();
    String apiToken = cacheKey.getApiToken();
    GitlabAPI gitLabApi = GitlabAPI.connect(hostUrl, apiToken);
    GitlabProject project = new GitlabProject();
    project.setId(cacheKey.getProjectId());
    return gitLabApi.getIssues(project);
  }
}
