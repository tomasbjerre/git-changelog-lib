package se.bjurr.gitchangelog.internal.model;

import static com.google.common.base.Predicates.in;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.newTreeMap;
import static com.google.common.collect.Multimaps.index;
import static java.util.TimeZone.getTimeZone;
import static java.util.regex.Pattern.compile;
import static se.bjurr.gitchangelog.internal.common.GitPredicates.ignoreCommits;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
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

  private final Settings settings;

  public Transformer(Settings settings) {
    this.settings = settings;
  }

  public List<Author> toAuthors(List<GitCommit> gitCommits) {
    final Multimap<String, GitCommit> commitsPerAuthor =
        index(
            gitCommits,
            new Function<GitCommit, String>() {
              @Override
              public String apply(GitCommit input) {
                return input.getAuthorEmailAddress() + "-" + input.getAuthorName();
              }
            });

    Iterable<String> authorsWithCommits =
        filter(
            commitsPerAuthor.keySet(),
            new Predicate<String>() {
              @Override
              public boolean apply(String input) {
                return toCommits(commitsPerAuthor.get(input)).size() > 0;
              }
            });

    return newArrayList(
        transform(
            authorsWithCommits,
            new Function<String, Author>() {
              @Override
              public Author apply(String input) {
                List<GitCommit> gitCommitsOfSameAuthor = newArrayList(commitsPerAuthor.get(input));
                List<Commit> commitsOfSameAuthor = toCommits(gitCommitsOfSameAuthor);
                return new Author( //
                    commitsOfSameAuthor.get(0).getAuthorName(), //
                    commitsOfSameAuthor.get(0).getAuthorEmailAddress(), //
                    commitsOfSameAuthor);
              }
            }));
  }

  public List<Commit> toCommits(Collection<GitCommit> from) {
    Iterable<GitCommit> filteredCommits =
        filter(from, ignoreCommits(this.settings.getIgnoreCommitsIfMessageMatches()));
    return newArrayList(
        transform(
            filteredCommits,
            new Function<GitCommit, Commit>() {
              @Override
              public Commit apply(GitCommit c) {
                return toCommit(c);
              }
            }));
  }

  public List<Issue> toIssues(List<ParsedIssue> issues) {
    Iterable<ParsedIssue> issuesWithCommits = filterWithCommits(issues);

    return newArrayList(transform(issuesWithCommits, parsedIssueToIssue()));
  }

  public List<IssueType> toIssueTypes(List<ParsedIssue> issues) {
    Map<String, List<Issue>> issuesPerName = newTreeMap();

    for (ParsedIssue parsedIssue : filterWithCommits(issues)) {
      if (!issuesPerName.containsKey(parsedIssue.getName())) {
        issuesPerName.put(parsedIssue.getName(), new ArrayList<Issue>());
      }
      Issue transformedIssues = parsedIssueToIssue().apply(parsedIssue);
      issuesPerName
          .get(parsedIssue.getName()) //
          .add(transformedIssues);
    }

    List<IssueType> issueTypes = newArrayList();
    for (String name : issuesPerName.keySet()) {
      issueTypes.add(new IssueType(issuesPerName.get(name), name));
    }
    return issueTypes;
  }

  public List<Tag> toTags(List<GitTag> gitTags, final List<ParsedIssue> allParsedIssues) {

    Iterable<Tag> tags =
        transform(
            gitTags,
            new Function<GitTag, Tag>() {
              @Override
              public Tag apply(GitTag input) {
                List<GitCommit> gitCommits = input.getGitCommits();
                List<ParsedIssue> parsedIssues =
                    reduceParsedIssuesToOnlyGitCommits(allParsedIssues, gitCommits);
                List<Commit> commits = toCommits(gitCommits);
                List<Author> authors = toAuthors(gitCommits);
                List<Issue> issues = toIssues(parsedIssues);
                List<IssueType> issueTypes = toIssueTypes(parsedIssues);
                return new Tag(
                    toReadableTagName(input.getName()),
                    input.findAnnotation().orNull(),
                    commits,
                    authors,
                    issues,
                    issueTypes);
              }

              private List<ParsedIssue> reduceParsedIssuesToOnlyGitCommits(
                  final List<ParsedIssue> allParsedIssues, List<GitCommit> gitCommits) {
                List<ParsedIssue> parsedIssues = newArrayList();
                for (ParsedIssue candidate : allParsedIssues) {
                  List<GitCommit> candidateCommits =
                      newArrayList(filter(candidate.getGitCommits(), in(gitCommits)));
                  if (!candidateCommits.isEmpty()) {
                    ParsedIssue parsedIssue =
                        new ParsedIssue(
                            candidate.getName(),
                            candidate.getIssue(),
                            candidate.getLink(),
                            candidate.getTitle().orNull(),
                            candidate.getIssueType(),
                            candidate.getLabels());
                    parsedIssue.addCommits(candidateCommits);
                    parsedIssues.add(parsedIssue);
                  }
                }
                return parsedIssues;
              }
            });

    tags =
        filter(
            tags,
            new Predicate<Tag>() {
              @Override
              public boolean apply(Tag input) {
                return !input.getAuthors().isEmpty() && !input.getCommits().isEmpty();
              }
            });

    return newArrayList(tags);
  }

  private Iterable<ParsedIssue> filterWithCommits(List<ParsedIssue> issues) {
    Iterable<ParsedIssue> issuesWithCommits =
        filter(
            issues,
            new Predicate<ParsedIssue>() {
              @Override
              public boolean apply(ParsedIssue input) {
                return !toCommits(input.getGitCommits()).isEmpty();
              }
            });
    return issuesWithCommits;
  }

  private String format(Date commitTime) {
    SimpleDateFormat df = new SimpleDateFormat(this.settings.getDateFormat());
    df.setTimeZone(getTimeZone(this.settings.getTimeZone()));
    return df.format(commitTime);
  }

  private Function<ParsedIssue, Issue> parsedIssueToIssue() {
    return new Function<ParsedIssue, Issue>() {
      @Override
      public Issue apply(ParsedIssue input) {
        List<GitCommit> gitCommits = input.getGitCommits();
        return new Issue( //
            toCommits(gitCommits), //
            toAuthors(gitCommits), //
            input.getName(), //
            input.getTitle().or(""), //
            input.getIssue(), //
            input.getLink(), //
            input.getIssueType(), //
            input.getLabels());
      }
    };
  }

  private String removeIssuesFromString(
      boolean removeIssueFromMessage, List<SettingsIssue> issues, String string) {
    if (removeIssueFromMessage) {
      for (SettingsIssue issue : issues) {
        string = string.replaceAll(issue.getPattern(), "");
      }
    }
    return string;
  }

  private Commit toCommit(GitCommit gitCommit) {
    return new Commit( //
        gitCommit.getAuthorName(), //
        gitCommit.getAuthorEmailAddress(), //
        format(gitCommit.getCommitTime()), //
        gitCommit.getCommitTime().getTime(), //
        toMessage(
            this.settings.removeIssueFromMessage(),
            new IssuesUtil(this.settings).getIssues(),
            gitCommit.getMessage()), //
        gitCommit.getHash(), //
        gitCommit.isMerge());
  }

  private String toReadableTagName(String input) {
    Matcher matcher = compile(this.settings.getReadableTagName()).matcher(input);
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
  String toMessage(boolean removeIssueFromMessage, List<SettingsIssue> issues, String message) {
    return removeIssuesFromString(removeIssueFromMessage, issues, message);
  }
}
