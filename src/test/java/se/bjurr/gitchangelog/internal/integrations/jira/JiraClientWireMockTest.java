package se.bjurr.gitchangelog.internal.integrations.jira;

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

public class JiraClientWireMockTest {

  private WireMockServer wireMockServer;
  private DefaultJiraClient jiraClient;
  private String baseUrl;

  @BeforeEach
  public void setUp() {
    wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wireMockServer.start();
    baseUrl = "http://localhost:" + wireMockServer.port();
    jiraClient = new DefaultJiraClient(baseUrl);
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
          "key": "TEST-123",
          "fields": {
            "summary": "Test Summary",
            "description": "Test Description",
            "issuetype": {"name": "Bug"},
            "labels": ["test"],
            "parent": null,
            "issuelinks": []
          }
        }
        """;

    wireMockServer.stubFor(
        get(urlPathEqualTo("/rest/api/2/issue/TEST-123"))
            .withQueryParam(
                "fields", equalTo("parent,summary,issuetype,labels,description,issuelinks"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mockResponse)));

    Optional<JiraIssue> result = jiraClient.getIssue("TEST-123");

    assertThat(result).isPresent();
    JiraIssue issue = result.get();
    assertThat(issue.getIssue()).isEqualTo("TEST-123");
    assertThat(issue.getTitle()).isEqualTo("Test Summary");
    assertThat(issue.getDescription()).isEqualTo("Test Description");
    assertThat(issue.getIssueType()).isEqualTo("Bug");
    assertThat(issue.getLink()).isEqualTo(baseUrl + "/browse/TEST-123");
  }

  @Test
  public void testGetIssueNotFound() throws GitChangelogIntegrationException {
    wireMockServer.stubFor(
        get(urlPathEqualTo("/rest/api/2/issue/NOTFOUND-123"))
            .willReturn(aResponse().withStatus(404).withBody("Issue not found")));

    Optional<JiraIssue> result = jiraClient.getIssue("NOTFOUND-123");

    assertThat(result).isEmpty();
  }

  @Test
  public void testGetIssueServerError() throws GitChangelogIntegrationException {
    wireMockServer.stubFor(
        get(urlPathEqualTo("/rest/api/2/issue/ERROR-123"))
            .willReturn(aResponse().withStatus(500).withBody("Internal Server Error")));

    Optional<JiraIssue> result = jiraClient.getIssue("ERROR-123");

    assertThat(result).isEmpty();
  }
}
