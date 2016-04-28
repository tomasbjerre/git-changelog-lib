package se.bjurr.gitchangelog.internal.integrations.jira;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.base.Optional;

public class JiraClientIntegrationTest {

 private static final String EXISTING_JIRA = "X";
 private static final String JIRA_API_URL = "https://server.se/jira/";
 private static final String USER = "u";
 private static final String PASSWORD = "p";

 // @Test
 public void testThatIssueCanBeFound() throws Exception {
  JiraClient client = new DefaultJiraClient(JIRA_API_URL)//
    .withBasicCredentials(USER, PASSWORD);
  Optional<JiraIssue> issue = client.getIssue(EXISTING_JIRA);
  assertThat(issue.get().getTitle())//
    .isEqualTo("Stoppa funktion ta beslut i 5.0");
 }

}
