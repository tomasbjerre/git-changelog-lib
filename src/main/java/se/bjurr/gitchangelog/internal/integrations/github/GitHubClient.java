package se.bjurr.gitchangelog.internal.integrations.github;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.LinkedHashMap;

import net.minidev.json.JSONArray;

import org.slf4j.Logger;

import se.bjurr.gitchangelog.internal.integrations.rest.RestClient;

import com.google.common.base.Optional;
import com.jayway.jsonpath.JsonPath;

public class GitHubClient {
 private static Logger logger = getLogger(GitHubClient.class);

 private final String api;
 private final RestClient client;

 public GitHubClient(String api) {
  this.api = api;
  this.client = new RestClient(1, MINUTES);
 }

 public Optional<GitHubIssue> getIssue(String issue) {
  try {
   if (issue.startsWith("#")) {
    issue = issue.substring(1);
   }
   String json = client.get(api + "/issues?state=all");
   JSONArray jsonArray = (JSONArray) JsonPath.read(json, "$.*");
   for (Object jsonIssue : jsonArray) {
    LinkedHashMap o = (LinkedHashMap) jsonIssue;
    String htmlUrl = o.get("html_url").toString();
    String title = o.get("title").toString();
    String number = o.get("number").toString();
    if (number.equals(issue)) {
     return of(new GitHubIssue(title, htmlUrl, number));
    }
   }
  } catch (Exception e) {
   logger.error("Error while getting issue " + issue, e);
  }
  return absent();
 }
}
