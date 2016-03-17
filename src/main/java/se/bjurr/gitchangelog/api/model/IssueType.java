package se.bjurr.gitchangelog.api.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

public class IssueType {
 private final String name;
 private final List<Issue> issues;

 public IssueType(List<Issue> issues, String name) {
  this.name = checkNotNull(name, "name");
  this.issues = checkNotNull(issues, "issues");
 }

 public String getName() {
  return name;
 }

 public List<Issue> getIssues() {
  return issues;
 }

 @Override
 public String toString() {
  return "IssueType: " + name;
 }
}
