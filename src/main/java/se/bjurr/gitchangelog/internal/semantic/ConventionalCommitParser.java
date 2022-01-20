package se.bjurr.gitchangelog.internal.semantic;

import com.github.jknack.handlebars.Options;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import se.bjurr.gitchangelog.api.model.Commit;
import se.bjurr.gitchangelog.api.model.Tag;
import se.bjurr.gitchangelog.internal.model.Transformer;

public class ConventionalCommitParser {
  private static final Pattern CONVENTIONAL_PATTERN =
      Pattern.compile("^(\\w+)(\\(([\\w\\-:]+)\\)?)?(\\!?):(.+)");
  private static final Pattern FOOTER_PATTERN =
      Pattern.compile("^(BREAKING[ -]CHANGE|[^ ]+)(((: )|( #))(.+))");

  public static String getDate(final String tagTime) {
    if (tagTime == null || !tagTime.contains(" ")) {
      return "";
    }
    return tagTime.split(" ")[0];
  }

  public static boolean commitScope(final Commit commit, final Options options) {
    final String scope = options.hash("scope").toString();
    return commitScopes(commit.getMessage()).stream()
        .filter(it -> it.matches(scope))
        .findFirst()
        .isPresent();
  }

  public static String revertedCommit(final Object commitMessage) {
    final Matcher matcher = Transformer.PATTERN_THIS_REVERTS.matcher(commitMessage.toString());
    if (!matcher.find()) {
      return "";
    }
    return matcher.group(1);
  }

  public static List<String> commitFixes(final Object commitMessage) {
    final Matcher matcher = Pattern.compile("\\(fixes ([^)]+)").matcher(commitMessage.toString());
    if (!matcher.find()) {
      return new ArrayList<>();
    }
    final String group = matcher.group(1);
    final String value = group == null ? "" : group;
    return Arrays.stream(value.split(" ")).map((it) -> it.trim()).collect(Collectors.toList());
  }

  public static List<String> commitRefs(final Object commitMessage) {
    final Matcher matcher = Pattern.compile("\\(refs ([^)]+)").matcher(commitMessage.toString());
    if (!matcher.find()) {
      return new ArrayList<>();
    }
    final String group = matcher.group(1);
    final String value = group == null ? "" : group;
    return Arrays.stream(value.split(" ")).map((it) -> it.trim()).collect(Collectors.toList());
  }

  public static String commitDescription(final Object commitMessage) {
    final Matcher matcher = CONVENTIONAL_PATTERN.matcher(commitMessage.toString());
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

  public static List<String> commitScopes(final Object commitMessage) {
    final Matcher matcher = CONVENTIONAL_PATTERN.matcher(commitMessage.toString());
    if (!matcher.find()) {
      return new ArrayList<>();
    }
    final String group = matcher.group(3);
    if (group == null) {
      return new ArrayList<>();
    }
    return Arrays.stream(group.split(":")).map((it) -> it.trim()).collect(Collectors.toList());
  }

  public static boolean commitBreaking(final String commitMessage) {
    final String type = getType(commitMessage);
    if (type.matches("[Bb]reaking")) {
      return true;
    }
    final Matcher matcher = CONVENTIONAL_PATTERN.matcher(commitMessage.toString());
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

  public static boolean containsBreaking(final List<Commit> commits, final Options options) {
    for (final Commit commit : commits) {
      if (commitBreaking(commit.getMessage())) {
        return true;
      }
    }
    return false;
  }

  public static boolean containsType(final List<Commit> commits, final Options options) {
    for (final Commit commit : commits) {
      if (commitType(commit.getMessage(), options)) {
        return true;
      }
    }
    return false;
  }

  public static boolean containsTypeOtherThan(final List<Commit> commits, final Options options) {
    for (final Commit commit : commits) {
      if (!commitType(commit.getMessage(), options)) {
        return true;
      }
    }
    return false;
  }

  public static boolean commitType(final String commitMessage, final Options options) {
    final String type = options.hash("type").toString();
    return getType(commitMessage).matches(type);
  }

  private static String getType(final String commitMessage) {
    final Matcher matcher = CONVENTIONAL_PATTERN.matcher(commitMessage.toString());
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

  public static boolean isReleaseTag(final Tag tag) {
    return Pattern.matches("^[0-9]+\\.[0-9]+\\.[0-9]+", tag.getName().replaceAll("[^0-9\\.]", ""));
  }

  public static MessageParts getMessageParts(final String commitMessage) {
    final MessageParts mp = new MessageParts();
    final List<String> lines = Arrays.asList(commitMessage.trim().split("\\r?\\n"));
    boolean paragraphs = true;
    Footer currentFooter = null;
    String currentParagraph = null;
    for (int i = 1; i < lines.size(); i++) {
      final String line = lines.get(i);

      final boolean empty = line.trim().isEmpty();

      final Footer footerCandidate = toFooter(line);
      final boolean isFooter = footerCandidate != null;
      if (isFooter && currentFooter == null) {
        paragraphs = false;
        currentFooter = footerCandidate;
      } else if (currentFooter != null) {
        currentFooter.value += "\n" + line;
      }

      if (paragraphs && !empty && paragraphs) {
        if (currentParagraph == null) {
          currentParagraph = line;
        } else {
          currentParagraph += "\n" + line;
        }
      }

      final boolean isLastLine = i == lines.size() - 1;
      final boolean isNextLineEmpty = !isLastLine && lines.get(i + 1).trim().isEmpty();
      final boolean isNextLineNewFooter = !isLastLine && toFooter(lines.get(i + 1)) != null;
      final boolean shouldSaveState = isLastLine || isNextLineEmpty || isNextLineNewFooter;
      if (shouldSaveState) {
        if (currentFooter != null) {
          mp.footers.add(currentFooter);
          currentFooter = null;
        }
        if (currentParagraph != null) {
          mp.paragraphs.add(currentParagraph);
          currentParagraph = null;
        }
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

  public static class MessageParts {
    public List<String> paragraphs = new ArrayList<>();
    public List<Footer> footers = new ArrayList<>();

    public List<Footer> getFooters() {
      return this.footers;
    }

    public List<String> getParagraphs() {
      return this.paragraphs;
    }
  }

  public static class Footer {
    public String token = "";
    public String value = "";

    public String getToken() {
      return this.token;
    }

    public String getValue() {
      return this.value;
    }
  }
}
