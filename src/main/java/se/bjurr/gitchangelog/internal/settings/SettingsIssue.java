package se.bjurr.gitchangelog.internal.settings;

import static java.util.Optional.ofNullable;
import static se.bjurr.gitchangelog.internal.settings.SettingsIssueType.CUSTOM;
import static se.bjurr.gitchangelog.internal.util.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
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

  public SettingsIssue(
      final String name, final String pattern, final String link, final String title) {
    this.type = CUSTOM;
    this.name = checkNotNull(name, "name");
    this.pattern = checkNotNull(pattern, "pattern");
    this.link = link;
    this.title = title;
  }

  public SettingsIssue(
      final SettingsIssueType type,
      final String name,
      final String pattern,
      final String link,
      final String title) {
    this.type = checkNotNull(type, "type");
    this.name = checkNotNull(name, "name");
    this.pattern = checkNotNull(pattern, "pattern");
    this.link = link;
    this.title = title;
  }

  public SettingsIssueType getType() {
    return this.type;
  }

  public Optional<String> getLink() {
    return ofNullable(this.link);
  }

  public Optional<String> getTitle() {
    return ofNullable(this.title);
  }

  public String getName() {
    return this.name;
  }

  public String getPattern() {
    return this.pattern;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.link, this.name, this.pattern, this.title, this.type);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final SettingsIssue other = (SettingsIssue) obj;
    return Objects.equals(this.link, other.link)
        && Objects.equals(this.name, other.name)
        && Objects.equals(this.pattern, other.pattern)
        && Objects.equals(this.title, other.title)
        && this.type == other.type;
  }

  @Override
  public String toString() {
    return "SettingsIssue [type="
        + this.type
        + ", name="
        + this.name
        + ", title="
        + this.title
        + ", pattern="
        + this.pattern
        + ", link="
        + this.link
        + "]";
  }
}
