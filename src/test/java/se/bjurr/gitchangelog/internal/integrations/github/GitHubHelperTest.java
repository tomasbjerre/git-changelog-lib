package se.bjurr.gitchangelog.internal.integrations.github;

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

public class GitHubHelperTest {

  private WireMockServer wireMockServer;
  private GitHubHelper gitHubHelper;

  @BeforeEach
  public void setUp() {
    this.wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    this.wireMockServer.start();
    final String baseUrl = "http://localhost:" + this.wireMockServer.port() + "/repos/org/repo";
    this.gitHubHelper = new GitHubHelper(baseUrl, Optional.of("some-token"));
  }

  @AfterEach
  public void tearDown() {
    if (this.wireMockServer != null) {
      this.wireMockServer.stop();
    }
  }

  @Test
  public void testGetIssueFromAll() throws GitChangelogIntegrationException {
    final String mockResponse =
        """
        [
          {
            "number": 1,
            "title": "Test issue",
            "html_url": "https://github.com/org/repo/issues/1",
            "labels": [{"name": "bug"}]
          }
        ]
        """;

    this.wireMockServer.stubFor(
        get(urlPathEqualTo("/repos/org/repo/issues"))
            .withHeader("Authorization", equalTo("token some-token"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mockResponse)));

    final Optional<GitHubIssue> issueOpt = this.gitHubHelper.getIssueFromAll("1");

    assertThat(issueOpt).isPresent();
    assertThat(issueOpt.get().getTitle()).isEqualTo("Test issue");
    assertThat(issueOpt.get().getLink()).isEqualTo("https://github.com/org/repo/issues/1");
  }

  @Test
  public void testGetIssueFromAllSendsExtendedHeadersAlongsideToken()
      throws GitChangelogIntegrationException {
    final String mockResponse =
        """
        [
          {
            "number": 1,
            "title": "Test issue",
            "html_url": "https://github.com/org/repo/issues/1",
            "labels": []
          }
        ]
        """;

    this.wireMockServer.stubFor(
        get(urlPathEqualTo("/repos/org/repo/issues"))
            .withHeader("Authorization", equalTo("token some-token"))
            .withHeader("X-Extra", equalTo("extra-value"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mockResponse)));

    this.gitHubHelper.withHeaders(Map.of("X-Extra", "extra-value"));
    final Optional<GitHubIssue> issueOpt = this.gitHubHelper.getIssueFromAll("1");

    assertThat(issueOpt).isPresent();
  }

  @Test
  public void testGetIssueFromAllNotFound() throws GitChangelogIntegrationException {
    this.wireMockServer.stubFor(
        get(urlPathEqualTo("/repos/org/repo/issues"))
            .willReturn(aResponse().withStatus(200).withBody("[]")));

    final Optional<GitHubIssue> issueOpt = this.gitHubHelper.getIssueFromAll("404");

    assertThat(issueOpt).isEmpty();
  }
}
