package se.bjurr.gitchangelog.internal.integrations.gitlab;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;

public class GitLabClientTest {

  private static final String PROJECT_ID = "42";

  private WireMockServer wireMockServer;
  private GitLabClient gitLabClient;

  @BeforeEach
  public void setUp() {
    this.wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    this.wireMockServer.start();
    final String baseUrl = "http://localhost:" + this.wireMockServer.port();
    this.gitLabClient = new GitLabClient(baseUrl, "some-token");
  }

  @AfterEach
  public void tearDown() {
    if (this.wireMockServer != null) {
      this.wireMockServer.stop();
    }
  }

  @Test
  public void testGetIssue() throws GitChangelogIntegrationException {
    final String mockResponse =
        """
        {
          "title": "Test issue",
          "web_url": "https://gitlab.com/tomas.bjerre85/violations-test/-/issues/1",
          "labels": ["bug", "l1"]
        }
        """;

    this.wireMockServer.stubFor(
        get(urlPathEqualTo("/api/v4/projects/" + PROJECT_ID + "/issues/1"))
            .withHeader("PRIVATE-TOKEN", equalTo("some-token"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mockResponse)));

    final Optional<GitLabIssue> issueOpt = this.gitLabClient.getIssue(PROJECT_ID, 1);

    assertThat(issueOpt).isPresent();
    final GitLabIssue issue = issueOpt.get();
    assertThat(issue.getTitle()).isEqualTo("Test issue");
    assertThat(issue.getLink())
        .isEqualTo("https://gitlab.com/tomas.bjerre85/violations-test/-/issues/1");
    assertThat(issue.getLabels()).containsOnly("bug", "l1");
  }

  @Test
  public void testGetIssueNotFound() throws GitChangelogIntegrationException {
    this.wireMockServer.stubFor(
        get(urlPathEqualTo("/api/v4/projects/" + PROJECT_ID + "/issues/404"))
            .willReturn(
                aResponse().withStatus(404).withBody("{\"message\":\"404 Issue Not Found\"}")));

    final Optional<GitLabIssue> issueOpt = this.gitLabClient.getIssue(PROJECT_ID, 404);

    assertThat(issueOpt).isEmpty();
  }

  @Test
  public void testGetIssueSendsExtendedHeadersAlongsideToken()
      throws GitChangelogIntegrationException {
    final String mockResponse =
        """
        {
          "title": "Test issue",
          "web_url": "https://gitlab.com/tomas.bjerre85/violations-test/-/issues/1",
          "labels": []
        }
        """;

    this.wireMockServer.stubFor(
        get(urlPathEqualTo("/api/v4/projects/" + PROJECT_ID + "/issues/1"))
            .withHeader("PRIVATE-TOKEN", equalTo("some-token"))
            .withHeader("X-Extra", equalTo("extra-value"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mockResponse)));

    this.gitLabClient.withHeaders(Map.of("X-Extra", "extra-value"));
    final Optional<GitLabIssue> issueOpt = this.gitLabClient.getIssue(PROJECT_ID, 1);

    assertThat(issueOpt).isPresent();
  }

  @Test
  public void testGetIssueServerError() throws GitChangelogIntegrationException {
    this.wireMockServer.stubFor(
        get(urlPathEqualTo("/api/v4/projects/" + PROJECT_ID + "/issues/500"))
            .willReturn(aResponse().withStatus(500).withBody("Internal Server Error")));

    final Optional<GitLabIssue> issueOpt = this.gitLabClient.getIssue(PROJECT_ID, 500);

    assertThat(issueOpt).isEmpty();
  }
}
