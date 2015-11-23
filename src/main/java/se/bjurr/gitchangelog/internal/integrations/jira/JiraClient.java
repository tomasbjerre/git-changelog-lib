package se.bjurr.gitchangelog.internal.integrations.jira;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.jayway.jsonpath.JsonPath.read;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import se.bjurr.gitchangelog.internal.integrations.rest.RestClient;

import com.google.common.base.Optional;

public class JiraClient {
 private static Logger logger = getLogger(JiraClient.class);

 private final String api;
 private RestClient client;

 public JiraClient(String api) {
  this.api = api;
  this.client = new RestClient(1, MINUTES);
 }

 public JiraClient withBasicCredentials(String username, String password) {
  this.client = client.withBasicAuthCredentials(username, password);
  return this;
 }

 public Optional<JiraIssue> getIssue(String issue) {
  String endpoint = api + "/rest/api/2/issue/" + issue + "?fields=parent,summary";
  try {
   String json = client.get(endpoint);
   String title = read(json, "$.fields.summary");
   String link = api + "/browse/";
   return of(new JiraIssue(title, link, issue));
  } catch (Exception e) {
   logger.info("Did not find " + issue + " at " + endpoint, e);
  }
  return absent();
 }
}
