package se.bjurr.gitreleasenotes.internal.settings;

import se.bjurr.gitreleasenotes.internal.model.ParsedIssueType;

public class SettingsIssue {
 private final ParsedIssueType type;
 private final String pattern;
 private final String description;
 private final String name;

 public SettingsIssue(ParsedIssueType type, String name, String pattern, String description) {
  this.type = type;
  this.name = name;
  this.pattern = pattern;
  this.description = description;
 }

 public String getDescription() {
  return description;
 }

 public String getPattern() {
  return pattern;
 }

 public ParsedIssueType getType() {
  return type;
 }

 public String getName() {
  return name;
 }
}
