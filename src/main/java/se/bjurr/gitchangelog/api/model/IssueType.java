package se.bjurr.gitchangelog.api.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

public class IssueType implements Serializable {

 private static final long serialVersionUID = 8850522973130773606L;
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
