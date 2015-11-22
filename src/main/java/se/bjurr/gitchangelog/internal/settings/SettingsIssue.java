package se.bjurr.gitchangelog.internal.settings;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.CUSTOM;

import com.google.common.base.Optional;

public class SettingsIssue {
 private final SettingsIssueType type;
 private final String name;
 private final String pattern;
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
