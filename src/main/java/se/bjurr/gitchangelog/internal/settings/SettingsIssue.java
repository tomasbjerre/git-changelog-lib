package se.bjurr.gitchangelog.internal.settings;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.CUSTOM;
import se.bjurr.gitchangelog.api.model.Issue;

import com.google.common.base.Optional;

public class SettingsIssue {
 /**
  * Use {@link SettingsIssueType#CUSTOM} when adding custom issues.
  */
 private final SettingsIssueType type;
 /**
  * Name of the issue manager. This is the {@link Issue#getName()}. Perhaps
  * "GitHub" or "JIRA".
  */
 private final String name;
 /**
  * Regular expression that is evaluated in commit comment. If true, the commit
  * is available in {@link Issue#getCommits()}.
  */
 private final String pattern;
 /**
  * Link pointing at the issue. It supports variables like:<br>
  * <code>${PATTERN_GROUP}</code><br>
  * <code>${PATTERN_GROUP_1}</code><br>
  */
 private final String link;

 public SettingsIssue(String name, String pattern, String link) {
  this.type = CUSTOM;
  this.name = checkNotNull(name, "name");
  this.pattern = checkNotNull(pattern, "pattern");
  this.link = link;
 }

 public SettingsIssue(SettingsIssueType type, String name, String pattern, String link) {
  this.type = checkNotNull(type, "type");
  this.name = checkNotNull(name, "name");
  this.pattern = checkNotNull(pattern, "pattern");
  this.link = link;
 }

 public SettingsIssueType getType() {
  return type;
 }

 public Optional<String> getLink() {
  return fromNullable(link);
 }

 public String getName() {
  return name;
 }

 public String getPattern() {
  return pattern;
 }
}
