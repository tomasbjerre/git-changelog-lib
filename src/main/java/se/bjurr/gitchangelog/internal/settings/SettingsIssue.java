package se.bjurr.gitchangelog.internal.settings;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.CUSTOM;

import com.google.common.base.Optional;
import java.io.Serializable;
import se.bjurr.gitchangelog.api.model.Issue;

public class SettingsIssue implements Serializable {
  private static final long serialVersionUID = -658106272421601880L;
  /** Use {@link SettingsIssueType#CUSTOM} when adding custom issues. */
  private final SettingsIssueType type;
  /** Name of the issue manager. This is the {@link Issue#getName()}. Perhaps "GitHub" or "JIRA". */
  private final String name;
  /**
   * Title of the issues. Optional and can, for example, be used when when grouping issues per issue
   * type.
   */
  private final String title;
  /**
   * Regular expression that is evaluated in commit comment. If true, the commit is available in
   * {@link Issue#getCommits()}.
   */
  private final String pattern;
  /**
   * Link pointing at the issue. It supports variables like:<br>
   * <code>${PATTERN_GROUP}</code><br>
   * <code>${PATTERN_GROUP_1}</code><br>
   */
  private final String link;

  public SettingsIssue(String name, String pattern, String link, String title) {
    this.type = CUSTOM;
    this.name = checkNotNull(name, "name");
    this.pattern = checkNotNull(pattern, "pattern");
    this.link = link;
    this.title = title;
  }

  public SettingsIssue(
      SettingsIssueType type, String name, String pattern, String link, String title) {
    this.type = checkNotNull(type, "type");
    this.name = checkNotNull(name, "name");
    this.pattern = checkNotNull(pattern, "pattern");
    this.link = link;
    this.title = title;
  }

  public SettingsIssueType getType() {
    return type;
  }

  public Optional<String> getLink() {
    return fromNullable(link);
  }

  public Optional<String> getTitle() {
    return fromNullable(title);
  }

  public String getName() {
    return name;
  }

  public String getPattern() {
    return pattern;
  }
}
