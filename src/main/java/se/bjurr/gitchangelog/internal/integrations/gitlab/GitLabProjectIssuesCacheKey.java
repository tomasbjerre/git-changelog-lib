package se.bjurr.gitchangelog.internal.integrations.gitlab;

public class GitLabProjectIssuesCacheKey {
  private final String hostUrl;
  private final String apiToken;
  private final Integer projectId;

  public GitLabProjectIssuesCacheKey(String hostUrl, String apiToken, Integer projectId) {
    this.hostUrl = hostUrl;
    this.apiToken = apiToken;
    this.projectId = projectId;
  }

  public String getHostUrl() {
    return hostUrl;
  }

  public String getApiToken() {
    return apiToken;
  }

  public Integer getProjectId() {
    return projectId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (apiToken == null ? 0 : apiToken.hashCode());
    result = prime * result + (hostUrl == null ? 0 : hostUrl.hashCode());
    result = prime * result + (projectId == null ? 0 : projectId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    GitLabProjectIssuesCacheKey other = (GitLabProjectIssuesCacheKey) obj;
    if (apiToken == null) {
      if (other.apiToken != null) {
        return false;
      }
    } else if (!apiToken.equals(other.apiToken)) {
      return false;
    }
    if (hostUrl == null) {
      if (other.hostUrl != null) {
        return false;
      }
    } else if (!hostUrl.equals(other.hostUrl)) {
      return false;
    }
    if (projectId == null) {
      if (other.projectId != null) {
        return false;
      }
    } else if (!projectId.equals(other.projectId)) {
      return false;
    }
    return true;
  }
}
