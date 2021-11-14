package se.bjurr.gitchangelog.api;

public final class GitChangelogApiConstants {

  public static final String ZERO_COMMIT = "0000000000000000000000000000000000000000";
  public static final String REF_MASTER = "master";
  public static final boolean DEFAULT_REMOVE_ISSUE = true;
  public static final String DEFAULT_TIMEZONE = "UTC";
  public static final String DEFAULT_DATEFORMAT = "YYYY-MM-dd HH:mm:ss";
  public static final String DEFAULT_IGNORE_COMMITS_REGEXP = "";
  public static final String DEFAULT_UNTAGGED_NAME = "Unreleased";
  public static final String DEFAULT_READABLE_TAG_NAME = "/([^/]+?)$";
  public static final String DEFAULT_NO_ISSUE_NAME = "No issue";
  public static final String DEFAULT_GITHUB_ISSUE_PATTERN = "#([0-9]+)";
  public static final String DEFAULT_GITLAB_ISSUE_PATTERN = "#([0-9]+)";
  public static final String DEFAULT_JIRA_ISSUE_PATTEN = "\\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\\b";
  public static final String DEFAULT_REDMINE_ISSUE_PATTEN = "#([0-9]+)";
  public static final String DEFAULT_MINOR_PATTERN = "^[Ff]eat.*";
  public static final String DEFAULT_PATCH_PATTERN = null;

  private GitChangelogApiConstants() {}
}
