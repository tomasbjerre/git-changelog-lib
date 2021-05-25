package se.bjurr.gitchangelog.api.helpers;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Options.Buffer;
import com.github.jknack.handlebars.helper.EachHelper;
import java.io.IOException;
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
import se.bjurr.gitchangelog.internal.model.Transformer;

public class Helpers {
  private static final Pattern CONVENTIONAL_PATTERN =
      Pattern.compile("^(\\w+)(\\(([\\w:]+)\\)?)?:(.+)");

  public static final Map<String, Helper<?>> COMMIT_HELPERS = new TreeMap<String, Helper<?>>();
  public static final Map<String, Helper<?>> COMMITS_HELPERS = new TreeMap<String, Helper<?>>();
  public static final Map<String, Helper<?>> TAG_HELPERS = new TreeMap<String, Helper<?>>();

  static {
    TAG_HELPERS.put(
        "ifReleaseTag",
        (final Tag tag, final Options options) -> {
          return conditional(options, isReleaseTag(tag));
        });

    TAG_HELPERS.put(
        "tagDate",
        (final Tag tag, final Options options) -> {
          return getDate(tag.getTagTime());
        });

    COMMITS_HELPERS.put(
        "commitDate",
        (final Commit commit, final Options options) -> {
          return getDate(commit.getCommitTime());
        });
    COMMITS_HELPERS.put(
        "ifContainsType",
        (final List<Commit> commits, final Options options) -> {
          return conditional(options, containsType(commits, options));
        });

    COMMIT_HELPERS.put(
        "ifCommitType",
        (final Commit commit, final Options options) -> {
          return conditional(options, commitType(commit.getMessage(), options));
        });

    COMMIT_HELPERS.put(
        "ifCommitScope",
        (final Commit commit, final Options options) -> {
          return conditional(options, commitScope(commit, options));
        });

    COMMIT_HELPERS.put(
        "eachCommitScope",
        (final Commit commit, final Options options) -> {
          return each(options, commitScopes(commit.getMessage()));
        });

    COMMIT_HELPERS.put(
        "commitDescription",
        (final Commit commit, final Options options) -> {
          return commitDescription(commit.getMessage());
        });

    COMMIT_HELPERS.put(
        "eachCommitRefs",
        (final Commit commit, final Options options) -> {
          return each(options, commitRefs(commit.getMessage()));
        });

    COMMIT_HELPERS.put(
        "eachCommitFixes",
        (final Commit commit, final Options options) -> {
          return each(options, commitFixes(commit.getMessage()));
        });

    COMMIT_HELPERS.put(
        "revertedCommit",
        (final Commit commit, final Options options) -> {
          return revertedCommit(commit.getMessage());
        });
  }

  private static Object each(final Options options, final List<String> elements)
      throws IOException {
    return new EachHelper().apply(elements, options);
  }

  private static Buffer conditional(final Options options, final boolean condition)
      throws IOException {
    final Buffer buffer = options.buffer();
    if (condition) {
      buffer.append(options.fn());
    } else {
      buffer.append(options.inverse());
    }
    return buffer;
  }

  private static String getDate(final String tagTime) {
    if (tagTime == null || !tagTime.contains(" ")) {
      return "";
    }
    return tagTime.split(" ")[0];
  }

  private static boolean commitScope(final Commit commit, final Options options) {
    final String scope = options.hash("scope").toString();
    return commitScopes(commit.getMessage()).contains(scope);
  }

  private static String revertedCommit(final Object commitMessage) {
    final Matcher matcher = Transformer.PATTERN_THIS_REVERTS.matcher(commitMessage.toString());
    if (!matcher.find()) {
      return "";
    }
    return matcher.group(1);
  }

  private static List<String> commitFixes(final Object commitMessage) {
    final Matcher matcher = Pattern.compile("\\(fixes ([^)]+)").matcher(commitMessage.toString());
    if (!matcher.find()) {
      return new ArrayList<String>();
    }
    final String group = matcher.group(1);
    final String value = group == null ? "" : group;
    return Arrays.stream(value.split(" ")).map((it) -> it.trim()).collect(Collectors.toList());
  }

  private static List<String> commitRefs(final Object commitMessage) {
    final Matcher matcher = Pattern.compile("\\(refs ([^)]+)").matcher(commitMessage.toString());
    if (!matcher.find()) {
      return new ArrayList<String>();
    }
    final String group = matcher.group(1);
    final String value = group == null ? "" : group;
    return Arrays.stream(value.split(" ")).map((it) -> it.trim()).collect(Collectors.toList());
  }

  private static String commitDescription(final Object commitMessage) {
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
  }

  private static List<String> commitScopes(final Object commitMessage) {
    final Matcher matcher = Helpers.CONVENTIONAL_PATTERN.matcher(commitMessage.toString());
    if (!matcher.find()) {
      return new ArrayList<String>();
    }
    final String group = matcher.group(3);
    if (group == null) {
      return new ArrayList<String>();
    }
    return Arrays.stream(group.split(":")).map((it) -> it.trim()).collect(Collectors.toList());
  }

  private static boolean containsType(final List<Commit> commits, final Options options) {
    for (final Commit commit : commits) {
      if (commitType(commit.getMessage(), options)) {
        return true;
      }
    }
    return false;
  }

  private static boolean commitType(final String commitMessage, final Options options) {
    final String type = options.hash("type").toString();
    return getType(commitMessage).equalsIgnoreCase(type);
  }

  private static String getType(final String commitMessage) {
    final Matcher matcher = Helpers.CONVENTIONAL_PATTERN.matcher(commitMessage.toString());
    if (!matcher.find()) {
      final boolean matchesRevert = Transformer.PATTERN_THIS_REVERTS.matcher(commitMessage).find();
      if (matchesRevert) {
        return "revert";
      } else {
        return "";
      }
    }
    final String group = matcher.group(1);
    return group == null ? "" : group.trim();
  }

  private static boolean isReleaseTag(final Tag tag) {
    return Pattern.matches("^[0-9]+\\.[0-9]+\\.[0-9]+", tag.getName().replaceAll("[^0-9\\.]", ""));
  }
}
