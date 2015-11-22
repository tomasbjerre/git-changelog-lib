package se.bjurr.gitchangelog.internal.integrations.github;

public class GitHubIssue {
 private final String title;
 private final String link;
 private final String number;

 public GitHubIssue(String title, String link, String number) {
  this.title = title;
  this.link = link;
  this.number = number;
 }

 public String getNumber() {
  return number;
 }

 public String getLink() {
  return link;
 }

 public String getTitle() {
  return title;
 }
}
