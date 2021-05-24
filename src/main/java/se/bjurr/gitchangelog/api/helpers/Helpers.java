package se.bjurr.gitchangelog.api.helpers;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import se.bjurr.gitchangelog.api.model.Commit;
import se.bjurr.gitchangelog.api.model.Tag;

public class Helpers {
  private static final Pattern CONVENTIONAL_PATTERN =
      Pattern.compile("^(\\w+)(\\(([\\w:]+)\\)?)?:(.+)");

  public static final Map<String, Helper<?>> COMMIT_HELPERS = new TreeMap<String, Helper<?>>();
  public static final Map<String, Helper<?>> COMMITS_HELPERS = new TreeMap<String, Helper<?>>();
  public static final Map<String, Helper<?>> TAG_HELPERS = new TreeMap<String, Helper<?>>();

  public static String getCommitType(final String commitMessage) {
    final Matcher matcher = Helpers.CONVENTIONAL_PATTERN.matcher(commitMessage.toString());
    if (!matcher.find()) {
      return "";
    }
    final String group = matcher.group(1);
    return group == null ? "" : group.trim();
  }

  static {
    TAG_HELPERS.put(
        "isReleaseTag",
        (final Tag tag, final Options options) -> {
          return Pattern.matches(
              "^[0-9]+\\.[0-9]+\\.[0-9]+", tag.getName().replaceAll("^[0-9\\.]", ""));
        });

    COMMITS_HELPERS.put(
        "containsType",
        (final List<Commit> commits, final Options options) -> {
          final String type = options.hash("type").toString();
          for (final Commit commit : commits) {
            if (Helpers.getCommitType(commit.getMessage()).equals(type)) {
              return true;
            }
          }
          return false;
        });

    COMMIT_HELPERS.put(
        "commitType",
        (final String commitMessage, final Options options) -> {
          return Helpers.getCommitType(commitMessage);
        });

    COMMIT_HELPERS.put(
        "commitScopes",
        (commitMessage, options) -> {
          final Matcher matcher = Helpers.CONVENTIONAL_PATTERN.matcher(commitMessage.toString());
          if (!matcher.find()) {
            return new ArrayList<String>();
          }
          final String group = matcher.group(3);
          if (group == null) {
            return new ArrayList<String>();
          }
          return Arrays.stream(group.split(":"))
              .map((it) -> it.trim())
              .collect(Collectors.toList());
        });

    COMMIT_HELPERS.put(
        "commitDescription",
        (commitMessage, options) -> {
          final Matcher matcher = Helpers.CONVENTIONAL_PATTERN.matcher(commitMessage.toString());
          if (!matcher.find()) {
            return "";
          }
          final String group = matcher.group(4);
          String value = group == null ? "" : group;
          if (value.indexOf("(refs ") != -1) {
            value = value.substring(0, value.indexOf("(refs "));
          }
          if (value.indexOf("(fixes ") != -1) {
            value = value.substring(0, value.indexOf("(fixes "));
          }
          return value.trim();
        });

    COMMIT_HELPERS.put(
        "commitRefs",
        (commitMessage, options) -> {
          final Matcher matcher =
              Pattern.compile("\\(refs ([^)]+)").matcher(commitMessage.toString());
          if (!matcher.find()) {
            return new ArrayList<String>();
          }
          final String group = matcher.group(1);
          final String value = group == null ? "" : group;
          return Arrays.stream(value.split(" "))
              .map((it) -> it.trim())
              .collect(Collectors.toList());
        });

    COMMIT_HELPERS.put(
        "commitFixes",
        (commitMessage, options) -> {
          final Matcher matcher =
              Pattern.compile("\\(fixes ([^)]+)").matcher(commitMessage.toString());
          if (!matcher.find()) {
            return new ArrayList<String>();
          }
          final String group = matcher.group(1);
          final String value = group == null ? "" : group;
          return Arrays.stream(value.split(" "))
              .map((it) -> it.trim())
              .collect(Collectors.toList());
        });

    COMMIT_HELPERS.put(
        "isType",
        (commitMessage, options) -> {
          final String type = options.hash("type").toString();
          return commitMessage.toString().startsWith(type);
        });
  }
}
