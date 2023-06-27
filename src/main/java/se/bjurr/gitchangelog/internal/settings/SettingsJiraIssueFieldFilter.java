package se.bjurr.gitchangelog.internal.settings;

import static se.bjurr.gitchangelog.internal.util.Preconditions.checkNotNull;

import java.io.Serializable;

public class SettingsJiraIssueFieldFilter implements Serializable {
  private static final long serialVersionUID = -658106272421601880L;
  /** TODO: This could in theory be a defined list/enum of all available operators. */
  private final String operator;
  /** Key of the filter. */
  private final String key;
  /** Value of the filter. */
  private final String value;

  public SettingsJiraIssueFieldFilter(
      final String key, final String value) {
    this.operator = "=";
    this.key = checkNotNull(key, "key");
    this.value = checkNotNull(value, "value");
  }

  public SettingsJiraIssueFieldFilter(
      final String operator,
      final String key,
      final String value) {
    this.operator = operator;
    this.key = checkNotNull(key, "key");
    this.value = checkNotNull(value, "value");
  }

  public String getOperator() {
    return this.operator;
  }

  public String getKey() {
    return this.key;
  }

  public String getValue() {
    return this.value;
  }
}
