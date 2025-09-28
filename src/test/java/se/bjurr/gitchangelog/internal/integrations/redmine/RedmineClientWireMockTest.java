package se.bjurr.gitchangelog.internal.integrations.redmine;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogIntegrationException;

public class RedmineClientWireMockTest {

  private WireMockServer wireMockServer;
  private DefaultRedmineClient redmineClient;
  private String baseUrl;

  @BeforeEach
  public void setUp() {
    wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wireMockServer.start();
    baseUrl = "http://localhost:" + wireMockServer.port();
    redmineClient = new DefaultRedmineClient(baseUrl);
  }

  @AfterEach
  public void tearDown() {
    if (wireMockServer != null) {
      wireMockServer.stop();
    }
  }

  @Test
  public void testGetIssueSuccess() throws GitChangelogIntegrationException {
    String mockResponse =
        """
        {
          "issue": {
            "id": 1234,
            "subject": "Test Issue Subject",
            "description": "Test Issue Description",
            "status": {"name": "New"},
            "priority": {"name": "Normal"},
            "tracker": {"name": "Bug"}
          }
        }
        """;

    wireMockServer.stubFor(
        get(urlPathEqualTo("/issues/1234.json"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mockResponse)));

    Optional<RedmineIssue> result = redmineClient.getIssue("1234");

    assertThat(result).isPresent();
    RedmineIssue issue = result.get();
    assertThat(issue.getIssue()).isEqualTo("1234");
    assertThat(issue.getTitle()).isEqualTo("Test Issue Subject");
    assertThat(issue.getDescription()).isEqualTo("Test Issue Description");
    assertThat(issue.getLink()).isEqualTo(baseUrl + "/issues/1234");
  }

  @Test
  public void testGetIssueNotFound() throws GitChangelogIntegrationException {
    wireMockServer.stubFor(
        get(urlPathEqualTo("/issues/NOTFOUND.json"))
            .willReturn(aResponse().withStatus(404).withBody("Issue not found")));

    Optional<RedmineIssue> result = redmineClient.getIssue("NOTFOUND");

    assertThat(result).isEmpty();
  }

  @Test
  public void testGetIssueServerError() throws GitChangelogIntegrationException {
    wireMockServer.stubFor(
        get(urlPathEqualTo("/issues/ERROR.json"))
            .willReturn(aResponse().withStatus(500).withBody("Internal Server Error")));

    Optional<RedmineIssue> result = redmineClient.getIssue("ERROR");

    assertThat(result).isEmpty();
  }

  @Test
  public void testGetIssueWithBasicAuth() throws GitChangelogIntegrationException {
    String mockResponse =
        """
        {
          "issue": {
            "id": 5555,
            "subject": "Authenticated Issue",
            "description": "This issue requires auth",
            "status": {"name": "In Progress"},
            "priority": {"name": "High"},
            "tracker": {"name": "Feature"}
          }
        }
        """;

    wireMockServer.stubFor(
        get(urlPathEqualTo("/issues/5555.json"))
            .withHeader("Authorization", equalTo("Basic dXNlcjpwYXNz"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mockResponse)));

    RedmineClient authenticatedClient = redmineClient.withBasicCredentials("user", "pass");
    Optional<RedmineIssue> result = authenticatedClient.getIssue("5555");

    assertThat(result).isPresent();
    assertThat(result.get().getTitle()).isEqualTo("Authenticated Issue");
  }
}
