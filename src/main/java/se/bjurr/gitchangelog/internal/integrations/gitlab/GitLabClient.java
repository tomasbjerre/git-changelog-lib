package se.bjurr.gitchangelog.internal.integrations.gitlab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabIssue;
import org.gitlab.api.models.GitlabProject;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;

public class GitLabClient {

  private final Map<GitLabProjectIssuesCacheKey, List<GitlabIssue>> cache =
      new ConcurrentHashMap<>();

  private final String hostUrl;
  private final String apiToken;

  private List<GitlabIssue> issues;

  public GitLabClient(final String hostUrl, final String apiToken) {
    this.hostUrl = hostUrl;
    this.apiToken = apiToken;
  }

  public Optional<GitLabIssue> getIssue(final String projectName, final Integer matchedIssue)
      throws GitChangelogIntegrationException {
    final GitlabAPI gitLabApi = GitlabAPI.connect(this.hostUrl, this.apiToken);
    GitlabProject project;
    try {
      project = gitLabApi.getProject(projectName);
    } catch (final Exception e) {
      throw new GitChangelogIntegrationException(
          "Unable to find project \""
              + projectName
              + "\". It should be \"tomas.bjerre85/violations-test\" for a repo like: https://gitlab.com/tomas.bjerre85/violations-test",
          e);
    }
    final Integer projectId = project.getId();
    final String httpUrl = project.getHttpUrl();
    try {
      if (this.issues == null) {
        this.issues = this.getAllIssues(this.hostUrl, this.apiToken, projectId);
      }
      for (final GitlabIssue candidate : this.issues) {
        if (candidate.getIid() == matchedIssue) {
          return Optional.of(this.createGitLabIssue(httpUrl, candidate));
        }
      }
      return Optional.empty();
    } catch (final Exception e) {
      throw new GitChangelogIntegrationException(e.getMessage(), e);
    }
  }

  private GitLabIssue createGitLabIssue(final String projectUrl, final GitlabIssue candidate) {
    final String title = candidate.getTitle();
    final String link = projectUrl + "/issues/" + candidate.getIid();
    final List<String> labels = new ArrayList<>();
    for (final String l : candidate.getLabels()) {
      labels.add(l);
    }
    return new GitLabIssue(title, link, labels);
  }

  private List<GitlabIssue> getAllIssues(
      final String hostUrl, final String apiToken, final Integer projectId) throws IOException {
    final GitlabAPI gitLabApi = GitlabAPI.connect(hostUrl, apiToken);
    final GitlabProject project = new GitlabProject();
    project.setId(projectId);
    return gitLabApi.getIssues(project);
  }
}
