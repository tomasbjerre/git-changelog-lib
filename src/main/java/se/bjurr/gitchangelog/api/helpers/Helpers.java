package se.bjurr.gitchangelog.api.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import se.bjurr.gitchangelog.api.model.Commit;
import se.bjurr.gitchangelog.api.model.Tag;
import se.bjurr.gitchangelog.internal.model.Transformer;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Options.Buffer;
import com.github.jknack.handlebars.helper.EachHelper;

public class Helpers {
  private static final Pattern CONVENTIONAL_PATTERN =
      Pattern.compile("^(\\w+)(\\(([\\w:]+)\\)?)?(\\!?):(.+)");
  private static final Pattern FOOTER_PATTERN =
      Pattern.compile("^(BREAKING[ -]CHANGE|[^ ]+)(((: )|( #))(.+))");

  public static final Map<String, Helper<?>> ALL = new TreeMap<String, Helper<?>>();

  static {
    ALL.put(
        "ifReleaseTag",
        (final Tag tag, final Options options) -> {
          return conditional(options, isReleaseTag(tag));
        });

    ALL.put(
        "tagDate",
        (final Tag tag, final Options options) -> {
          return getDate(tag.getTagTime());
        });

    ALL.put(
        "commitDate",
        (final Commit commit, final Options options) -> {
          return getDate(commit.getCommitTime());
        });
    ALL.put(
        "ifContainsType",
        (final List<Commit> commits, final Options options) -> {
          return conditional(options, containsType(commits, options));
        });
    ALL.put(
        "ifContainsBreaking",
        (final List<Commit> commits, final Options options) -> {
          return conditional(options, containsBreaking(commits, options));
        });

    ALL.put(
        "ifCommitType",
        (final Commit commit, final Options options) -> {
          return conditional(options, commitType(commit.getMessage(), options));
        });

    ALL.put(
        "ifCommitBreaking",
        (final Commit commit, final Options options) -> {
          return conditional(options, commitBreaking(commit.getMessage()));
        });

    ALL.put(
        "ifCommitScope",
        (final Commit commit, final Options options) -> {
          return conditional(options, commitScope(commit, options));
        });

    ALL.put(
        "ifCommitHasFooters",
        (final Commit commit, final Options options) -> {
          return conditional(options, getMessageParts(commit.getMessage()).footers.size() > 0);
        });

    ALL.put(
        "ifCommitHasParagraphs",
        (final Commit commit, final Options options) -> {
          return conditional(options, getMessageParts(commit.getMessage()).paragraphs.size() > 0);
        });

    ALL.put(
        "eachCommitScope",
        (final Commit commit, final Options options) -> {
          return each(options, commitScopes(commit.getMessage()));
        });

    ALL.put(
        "commitDescription",
        (final Commit commit, final Options options) -> {
          return commitDescription(commit.getMessage());
        });

    ALL.put(
        "eachCommitRefs",
        (final Commit commit, final Options options) -> {
          return each(options, commitRefs(commit.getMessage()));
        });

    ALL.put(
        "eachCommitFixes",
        (final Commit commit, final Options options) -> {
          return each(options, commitFixes(commit.getMessage()));
        });

    ALL.put(
        "revertedCommit",
        (final Commit commit, final Options options) -> {
          return revertedCommit(commit.getMessage());
        });

    ALL.put(
        "eachCommitParagraph",
        (final Commit commit, final Options options) -> {
          return each(options, getMessageParts(commit.getMessage()).paragraphs);
        });

    ALL.put(
        "eachCommitFooter",
        (final Commit commit, final Options options) -> {
          return each(options, getMessageParts(commit.getMessage()).footers);
        });

    ALL.put(
        "ifFooterHasValue",
        (final Footer footer, final Options options) -> {
          return conditional(options, !footer.value.trim().isEmpty());
        });
  }

  private static Object each(final Options options, final List<?> elements) throws IOException {
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
    final String group = matcher.group(5);
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

  private static boolean commitBreaking(final String commitMessage) {
    final String type = getType(commitMessage);
    if (type.matches("[Bb]reaking")) {
      return true;
    }
    final Matcher matcher = Helpers.CONVENTIONAL_PATTERN.matcher(commitMessage.toString());
    if (!matcher.find()) {
      return false;
    }
    final String group = matcher.group(4);
    final String value = group == null ? "" : group;
    if (value.equals("!")) {
      return true;
    }

    for (final Footer footer : getMessageParts(commitMessage).footers) {
      if (footer.token.equals("BREAKING CHANGE") || footer.token.equals("BREAKING-CHANGE")) {
        return true;
      }
    }
    return false;
  }

  private static boolean containsBreaking(final List<Commit> commits, final Options options) {
    for (final Commit commit : commits) {
      if (commitBreaking(commit.getMessage())) {
        return true;
      }
    }
    return false;
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

  private static MessageParts getMessageParts(final String commitMessage) {
    final MessageParts mp = new MessageParts();
    final List<String> lines = Arrays.asList(commitMessage.trim().split("\\r?\\n"));
    Collections.reverse(lines);
    boolean footers = true;
    for (int i = 0; i < lines.size(); i++) {
      final String line = lines.get(i);
      final boolean isHeader = i == lines.size() - 1;
      if (isHeader) {
        break;
      }
      if (footers) {
        final Footer footer = toFooter(line);
        if (footer == null) {
          footers = false;
        } else {
          mp.footers.add(footer);
        }
      } else {
        // Paragraphs
        if (line.trim().isEmpty()) {
          continue;
        }
        mp.paragraphs.add(line.trim());
      }
    }
    return mp;
  }

  private static Footer toFooter(final String from) {
    final Matcher m = FOOTER_PATTERN.matcher(from);
    final Footer f = new Footer();
    if (m.find()) {
      f.token = m.group(1);
      f.value = m.group(6);
      return f;
    }
    return null;
  }

  static class MessageParts {
    List<String> paragraphs = new ArrayList<>();
    List<Footer> footers = new ArrayList<>();

    public List<Footer> getFooters() {
      return this.footers;
    }

    public List<String> getParagraphs() {
      return this.paragraphs;
    }
  }

  static class Footer {
    String token = "";
    String value = "";

    public String getToken() {
      return this.token;
    }

    public String getValue() {
      return this.value;
    }
  }
}
