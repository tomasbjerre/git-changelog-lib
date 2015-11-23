package se.bjurr.gitchangelog.internal.integrations.jira;

public class JiraIssue {
 private final String title;
 private final String link;
 private final String issue;

 public JiraIssue(String title, String link, String issue) {
  this.title = title;
  this.link = link;
  this.issue = issue;
 }

 public String getIssue() {
  return issue;
 }

 public String getLink() {
  return link;
 }

 public String getTitle() {
  return title;
 }
}
