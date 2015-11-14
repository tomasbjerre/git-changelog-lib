package se.bjurr.gitchangelog.internal.settings;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;

public class CustomIssue {
 private final String name;
 private final String pattern;
 private final String link;

 public CustomIssue(String name, String pattern, String link) {
  this.name = checkNotNull(name, "name");
  this.pattern = checkNotNull(pattern, "pattern");
  this.link = link;
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
