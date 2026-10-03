package se.bjurr.gitchangelog.internal.model;

import static java.util.TimeZone.getTimeZone;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import se.bjurr.gitchangelog.api.model.Author;
import se.bjurr.gitchangelog.api.model.Commit;
import se.bjurr.gitchangelog.api.model.Issue;
import se.bjurr.gitchangelog.api.model.IssueType;
import se.bjurr.gitchangelog.api.model.Tag;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;
import se.bjurr.gitchangelog.internal.settings.IssuesUtil;
import se.bjurr.gitchangelog.internal.settings.Settings;
import se.bjurr.gitchangelog.internal.settings.SettingsIssue;

public class Transformer {

  public static final Pattern PATTERN_THIS_REVERTS =
      Pattern.compile("This reverts commit ([a-z0-9]+).", Pattern.MULTILINE);
  private final Settings settings;

  public Transformer(final Settings settings) {
    this.settings = settings;
  }

  public List<Author> toAuthors(final List<GitCommit> gitCommits) {
    final Map<String, Set<GitCommit>> authors = new TreeMap<>();
    final List<String> keys = new ArrayList<>();
    for (final GitCommit gitCommit : gitCommits) {
      final String key = this.getKey(gitCommit.getAuthorName(), gitCommit.getAuthorEmailAddress());
      if (!authors.containsKey(key)) {
        authors.put(key, new TreeSet<>());
        keys.add(key);
      }
      authors.get(key).add(gitCommit);
    }
    return keys.stream()
        .map(authors::get)
        .map(this::toCommits)
        .filter(it -> !it.isEmpty())
        .map(
            (final List<Commit> commits) ->
                new Author(
                    commits.get(0).getAuthorName(),
                    commits.get(0).getAuthorEmailAddress(),
                    commits))
        .collect(toList());
  }

  private String getKey(final String authorName, final String authorEmail) {
    return ("name:" + authorName + "-email:" + authorEmail).toLowerCase(Locale.ENGLISH);
  }

  private boolean isRevertCommit(final GitCommit commit) {
    return this.getRevertCommitHash(commit) != null;
  }

  @SuppressFBWarnings("UNSAFE_HASH_EQUALS")
  private boolean isRevertedCommit(
      final GitCommit commit, final Iterable<GitCommit> revertCommits) {
    for (final GitCommit revertCommit : revertCommits) {
      final String revertedHash = this.getRevertCommitHash(revertCommit);
      if (commit.getHash().equals(revertedHash)) {
        return true;
      }
    }
    return false;
  }

  private String getRevertCommitHash(final GitCommit revertCommit) {
    final Matcher matcher = PATTERN_THIS_REVERTS.matcher(revertCommit.getMessage());
    if (!matcher.find()) {
      return null;
    }
    return matcher.group(1);
  }

  public List<Commit> toCommits(final Collection<GitCommit> from) {
    final List<GitCommit> revertCommits =
        from.stream().filter(this::isRevertCommit).collect(Collectors.toList());
    final List<GitCommit> revertedCommits =
        from.stream()
            .filter(it -> this.isRevertedCommit(it, revertCommits))
            .collect(Collectors.toList());
    final List<GitCommit> revertCommitsToRemove =
        revertedCommits.stream()
            .map(
                it -> {
                  return revertCommits.stream()
                      .filter(rc -> this.getRevertCommitHash(rc).equals(it.getHash()))
                      .findFirst()
                      .get();
                })
            .collect(toList());
    final List<GitCommit> fromWithoutReverted =
        from.stream()
            .filter(it -> !revertedCommits.contains(it) && !revertCommitsToRemove.contains(it))
            .collect(toList());

    final List<GitCommit> filteredCommits =
        fromWithoutReverted.stream()
            .filter(
                gitCommit -> {
                  final boolean messageMatches =
                      compile(this.settings.getIgnoreCommitsIfMessageMatches(), DOTALL)
                          .matcher(gitCommit.getMessage())
                          .matches();
                  if (messageMatches) {
                    return false;
                  }

                  if (this.settings.getIgnoreCommitsIfOlderThan().isPresent()) {
                    final boolean olderThan =
                        gitCommit
                            .getCommitTime()
                            .isBefore(this.settings.getIgnoreCommitsIfOlderThan().get());
                    if (olderThan) {
                      return false;
                    }
                  }
                  return true;
                })
            .collect(toList());

    return filteredCommits.stream().map(this::toCommit).collect(toList());
  }

  public List<Issue> toIssues(final List<ParsedIssue> issues) {
    final List<ParsedIssue> issuesWithCommits = this.filterWithCommits(issues);
    return issuesWithCommits.stream().map(this::parsedIssueToIssue).collect(toList());
  }

  public List<IssueType> toIssueTypes(final List<ParsedIssue> issues) {
    final Map<String, List<Issue>> issuesPerName = new TreeMap<>();

    for (final ParsedIssue parsedIssue : this.filterWithCommits(issues)) {
      if (!issuesPerName.containsKey(parsedIssue.getName())) {
        issuesPerName.put(parsedIssue.getName(), new ArrayList<>());
      }
      final Issue transformedIssues = this.parsedIssueToIssue(parsedIssue);
      issuesPerName
          .get(parsedIssue.getName()) //
          .add(transformedIssues);
    }

    final List<IssueType> issueTypes = new ArrayList<>();
    for (final Entry<String, List<Issue>> entry : issuesPerName.entrySet()) {
      issueTypes.add(new IssueType(entry.getValue(), entry.getKey()));
    }
    return issueTypes;
  }

  public List<Tag> toTags(final List<GitTag> gitTags, final List<ParsedIssue> allParsedIssues) {

    final List<Tag> tags =
        gitTags.stream()
            .map(
                input -> {
                  final List<GitCommit> gitCommits = input.getGitCommits();
                  final List<ParsedIssue> parsedIssues =
                      this.reduceParsedIssuesToOnlyGitCommits(allParsedIssues, gitCommits);
                  final List<Commit> commits = Transformer.this.toCommits(gitCommits);
                  final List<Author> authors = Transformer.this.toAuthors(gitCommits);
                  final List<Issue> issues = Transformer.this.toIssues(parsedIssues);
                  final List<IssueType> issueTypes = Transformer.this.toIssueTypes(parsedIssues);
                  return new Tag(
                      Transformer.toReadableTagName(
                          input.getName(), this.settings.getReadableTagName()),
                      input.findAnnotation().orElse(null),
                      commits,
                      authors,
                      issues,
                      issueTypes,
                      input.getTagTime() != null ? Transformer.this.format(input.getTagTime()) : "",
                      input.getTagTime() != null ? input.getTagTime().toEpochMilli() : -1L);
                })
            .collect(Collectors.toList());

    return tags.stream()
        .filter(input -> !input.getAuthors().isEmpty() && !input.getCommits().isEmpty())
        .collect(Collectors.toList());
  }

  private List<ParsedIssue> reduceParsedIssuesToOnlyGitCommits(
      final List<ParsedIssue> allParsedIssues, final List<GitCommit> gitCommits) {
    final List<ParsedIssue> parsedIssues = new ArrayList<>();
    for (final ParsedIssue candidate : allParsedIssues) {
      final List<GitCommit> candidateCommits =
          candidate.getGitCommits().stream()
              .filter(gitCommits::contains)
              .collect(Collectors.toList());
      if (!candidateCommits.isEmpty()) {
        final ParsedIssue parsedIssue =
            new ParsedIssue(
                candidate.getSettingsIssueType(),
                candidate.getName(),
                candidate.getIssue(),
                candidate.getDescription(),
                candidate.getLink(),
                candidate.getTitle().orElse(null),
                candidate.getIssueType(),
                candidate.getLinkedIssues(),
                candidate.getLabels(),
                candidate.getAdditionalFields());
        parsedIssue.addCommits(candidateCommits);
        parsedIssues.add(parsedIssue);
      }
    }
    return parsedIssues;
  }

  private List<ParsedIssue> filterWithCommits(final List<ParsedIssue> issues) {
    return issues.stream()
        .filter(input -> !Transformer.this.toCommits(input.getGitCommits()).isEmpty())
        .collect(toList());
  }

  private String format(final Instant commitTime) {
    final DateTimeFormatter df =
        DateTimeFormatter.ofPattern(this.settings.getDateFormat(), Locale.ENGLISH)
            .withZone(getTimeZone(this.settings.getTimeZone()).toZoneId());
    return df.format(commitTime);
  }

  private Issue parsedIssueToIssue(final ParsedIssue input) {
    final List<GitCommit> gitCommits = input.getGitCommits();
    return new Issue( //
        Transformer.this.toCommits(gitCommits), //
        Transformer.this.toAuthors(gitCommits), //
        input.getName(), //
        input.getTitle().orElse(""), //
        input.getIssue(), //
        input.getSettingsIssueType(), //
        input.getDescription(),
        input.getLink(), //
        input.getIssueType(), //
        input.getLinkedIssues(), //
        input.getLabels(),
        input.getAdditionalFields());
  }

  private String removeIssuesFromString(
      final boolean removeIssueFromMessage, final List<SettingsIssue> issues, String string) {
    if (removeIssueFromMessage) {
      for (final SettingsIssue issue : issues) {
        string = string.replaceAll(issue.getPattern(), "");
      }
    }
    return string;
  }

  private Commit toCommit(final GitCommit gitCommit) {
    return new Commit( //
        gitCommit.getAuthorName(), //
        gitCommit.getAuthorEmailAddress(), //
        this.format(gitCommit.getCommitTime()), //
        gitCommit.getCommitTime().toEpochMilli(), //
        this.toMessage(
            this.settings.removeIssueFromMessage(),
            new IssuesUtil(this.settings).getIssues(),
            gitCommit.getMessage()), //
        gitCommit.getHash(), //
        gitCommit.isMerge(), //
        gitCommit.getMessageNotes(), //
        gitCommit.getFiles(), //
        gitCommit.getCommitCount());
  }

  public static String toReadableTagName(final String input, final String readableTagName) {
    final Matcher matcher = compile(readableTagName).matcher(input);
    if (matcher.find()) {
      if (matcher.groupCount() == 0) {
        throw new RuntimeException(
            "Pattern: \"" + readableTagName + "\" did not match any group in: \"" + input + "\"");
      }
      return matcher.group(1);
    }
    return input;
  }

  String toMessage(
      final boolean removeIssueFromMessage,
      final List<SettingsIssue> issues,
      final String message) {
    return this.removeIssuesFromString(removeIssueFromMessage, issues, message);
  }
}
