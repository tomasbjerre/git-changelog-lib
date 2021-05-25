package se.bjurr.gitchangelog.internal.model;

import static com.google.common.base.Predicates.in;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.newTreeMap;
import static com.google.common.collect.Multimaps.index;
import static java.util.TimeZone.getTimeZone;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.Multimap;

public class Transformer {

  private static final String PATTERN_THIS_REVERTS = "This reverts commit ([a-z0-9]+).";
  private final Settings settings;

  public Transformer(final Settings settings) {
    this.settings = settings;
  }

  public List<Author> toAuthors(final List<GitCommit> gitCommits) {
    final Multimap<String, GitCommit> commitsPerAuthor =
        index(
            gitCommits,
            (Function<GitCommit, String>)
                input -> input.getAuthorEmailAddress() + "-" + input.getAuthorName());

    final Iterable<String> authorsWithCommits =
        filter(
            commitsPerAuthor.keySet(),
            input -> Transformer.this.toCommits(commitsPerAuthor.get(input)).size() > 0);

    return newArrayList(
        transform(
            authorsWithCommits,
            input -> {
              final List<GitCommit> gitCommitsOfSameAuthor =
                  newArrayList(commitsPerAuthor.get(input));
              final List<Commit> commitsOfSameAuthor =
                  Transformer.this.toCommits(gitCommitsOfSameAuthor);
              return new Author( //
                  commitsOfSameAuthor.get(0).getAuthorName(), //
                  commitsOfSameAuthor.get(0).getAuthorEmailAddress(), //
                  commitsOfSameAuthor);
            }));
  }

  private boolean isRevertCommit(final GitCommit commit) {
    return this.getRevertCommitHash(commit) != null;
  }

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
    final Matcher matcher = Pattern.compile(PATTERN_THIS_REVERTS).matcher(revertCommit.getMessage());
    if (!matcher.find()) {
    	return null;
    }
	return matcher.group(1);
  }

  public List<Commit> toCommits(final Collection<GitCommit> from) {
    final List<GitCommit> revertCommits =
        from.stream().filter(it -> this.isRevertCommit(it)).collect(Collectors.toList());
    final List<GitCommit> revertedCommits =
        from.stream()
            .filter(it -> this.isRevertedCommit(it, revertCommits))
            .collect(Collectors.toList());
    final List<GitCommit> revertCommitsToRemove =
        revertedCommits
            .stream()
            .map(
                it -> {
                  return revertCommits
                      .stream()
                      .filter(rc -> this.getRevertCommitHash(rc).equals(it.getHash()))
                      .findFirst()
                      .get();
                })
            .collect(toList());
    final List<GitCommit> fromWithoutReverted =
    		from.stream().filter(it -> !revertedCommits.contains(it) && !revertCommitsToRemove.contains(it)).collect(toList());

    final List<GitCommit> filteredCommits =
    		fromWithoutReverted.stream().filter(
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
    				            gitCommit.getCommitTime().before(this.settings.getIgnoreCommitsIfOlderThan().get());
    				        if (olderThan) {
    				          return false;
    				        }
    				      }
    				      return true;
    				    }
    				).collect(toList());
    return newArrayList(transform(filteredCommits, c -> Transformer.this.toCommit(c)));
  }

  public List<Issue> toIssues(final List<ParsedIssue> issues) {
    final Iterable<ParsedIssue> issuesWithCommits = this.filterWithCommits(issues);

    return newArrayList(transform(issuesWithCommits, this.parsedIssueToIssue()));
  }

  public List<IssueType> toIssueTypes(final List<ParsedIssue> issues) {
    final Map<String, List<Issue>> issuesPerName = newTreeMap();

    for (final ParsedIssue parsedIssue : this.filterWithCommits(issues)) {
      if (!issuesPerName.containsKey(parsedIssue.getName())) {
        issuesPerName.put(parsedIssue.getName(), new ArrayList<Issue>());
      }
      final Issue transformedIssues = this.parsedIssueToIssue().apply(parsedIssue);
      issuesPerName
          .get(parsedIssue.getName()) //
          .add(transformedIssues);
    }

    final List<IssueType> issueTypes = newArrayList();
    for (final String name : issuesPerName.keySet()) {
      issueTypes.add(new IssueType(issuesPerName.get(name), name));
    }
    return issueTypes;
  }

  public List<Tag> toTags(final List<GitTag> gitTags, final List<ParsedIssue> allParsedIssues) {

    final List<Tag> tags =
    		gitTags.stream().map(input -> {
            final List<GitCommit> gitCommits = input.getGitCommits();
            final List<ParsedIssue> parsedIssues =
                this.reduceParsedIssuesToOnlyGitCommits(allParsedIssues, gitCommits);
            final List<Commit> commits = Transformer.this.toCommits(gitCommits);
            final List<Author> authors = Transformer.this.toAuthors(gitCommits);
            final List<Issue> issues = Transformer.this.toIssues(parsedIssues);
            final List<IssueType> issueTypes = Transformer.this.toIssueTypes(parsedIssues);
            return new Tag(
                Transformer.this.toReadableTagName(input.getName()),
                input.findAnnotation().orNull(),
                commits,
                authors,
                issues,
                issueTypes,
                input.getTagTime() != null ? Transformer.this.format(input.getTagTime()) : "",
                input.getTagTime() != null ? input.getTagTime().getTime() : -1);
    		}
    				).collect(Collectors.toList());


    return tags.stream().filter(input -> !input.getAuthors().isEmpty() && !input.getCommits().isEmpty())
    		.collect(Collectors.toList());
  }

  private List<ParsedIssue> reduceParsedIssuesToOnlyGitCommits(
      final List<ParsedIssue> allParsedIssues, final List<GitCommit> gitCommits) {
    final List<ParsedIssue> parsedIssues = newArrayList();
    for (final ParsedIssue candidate : allParsedIssues) {
      final List<GitCommit> candidateCommits =
          newArrayList(filter(candidate.getGitCommits(), in(gitCommits)));
      if (!candidateCommits.isEmpty()) {
        final ParsedIssue parsedIssue =
            new ParsedIssue(
                candidate.getSettingsIssueType(),
                candidate.getName(),
                candidate.getIssue(),
                candidate.getDescription(),
                candidate.getLink(),
                candidate.getTitle().orNull(),
                candidate.getIssueType(),
                candidate.getLinkedIssues(),
                candidate.getLabels());
        parsedIssue.addCommits(candidateCommits);
        parsedIssues.add(parsedIssue);
      }
    }
    return parsedIssues;
  }

  private Iterable<ParsedIssue> filterWithCommits(final List<ParsedIssue> issues) {
    final Iterable<ParsedIssue> issuesWithCommits =
        filter(issues, input -> !Transformer.this.toCommits(input.getGitCommits()).isEmpty());
    return issuesWithCommits;
  }

  private String format(final Date commitTime) {
    final SimpleDateFormat df = new SimpleDateFormat(this.settings.getDateFormat());
    df.setTimeZone(getTimeZone(this.settings.getTimeZone()));
    return df.format(commitTime);
  }

  private Function<ParsedIssue, Issue> parsedIssueToIssue() {
    return input -> {
      final List<GitCommit> gitCommits = input.getGitCommits();
      return new Issue( //
          Transformer.this.toCommits(gitCommits), //
          Transformer.this.toAuthors(gitCommits), //
          input.getName(), //
          input.getTitle().or(""), //
          input.getIssue(), //
          input.getSettingsIssueType(), //
          input.getDescription(),
          input.getLink(), //
          input.getIssueType(), //
          input.getLinkedIssues(), //
          input.getLabels());
    };
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
        gitCommit.getCommitTime().getTime(), //
        this.toMessage(
            this.settings.removeIssueFromMessage(),
            new IssuesUtil(this.settings).getIssues(),
            gitCommit.getMessage()), //
        gitCommit.getHash(), //
        gitCommit.isMerge());
  }

  private String toReadableTagName(final String input) {
    final Matcher matcher = compile(this.settings.getReadableTagName()).matcher(input);
    if (matcher.find()) {
      if (matcher.groupCount() == 0) {
        throw new RuntimeException(
            "Pattern: \""
                + this.settings.getReadableTagName()
                + "\" did not match any group in: \""
                + input
                + "\"");
      }
      return matcher.group(1);
    }
    return input;
  }

  @VisibleForTesting
  String toMessage(
      final boolean removeIssueFromMessage,
      final List<SettingsIssue> issues,
      final String message) {
    return this.removeIssuesFromString(removeIssueFromMessage, issues, message);
  }
}
