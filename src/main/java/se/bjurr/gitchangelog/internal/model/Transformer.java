package se.bjurr.gitchangelog.internal.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Multimaps.index;
import static java.util.TimeZone.getTimeZone;
import static java.util.regex.Pattern.compile;
import static se.bjurr.gitchangelog.internal.common.GitPredicates.ignoreCommits;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import se.bjurr.gitchangelog.api.model.Author;
import se.bjurr.gitchangelog.api.model.Commit;
import se.bjurr.gitchangelog.api.model.Issue;
import se.bjurr.gitchangelog.api.model.Tag;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;
import se.bjurr.gitchangelog.internal.issues.IssueParser;
import se.bjurr.gitchangelog.internal.model.interfaces.IGitCommitReferer;
import se.bjurr.gitchangelog.internal.settings.IssuesUtil;
import se.bjurr.gitchangelog.internal.settings.Settings;
import se.bjurr.gitchangelog.internal.settings.SettingsIssue;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

public class Transformer {

 public Transformer(Settings settings) {
  this.settings = settings;
 }

 private final Settings settings;

 public List<Tag> toTags(List<GitCommit> allCommits, List<GitTag> gitTags) {
  final Map<String, List<GitCommit>> commitsPerTag = getCommitsPerGitCommitReferer(allCommits, gitTags);

  List<String> withUnfilteredCommits = withUnfilteredCommits(commitsPerTag);

  List<Tag> tags = newArrayList(transform(withUnfilteredCommits, new Function<String, Tag>() {
   @Override
   public Tag apply(String input) {
    List<GitCommit> gitCommits = commitsPerTag.get(input);
    List<Commit> commits = toCommits(gitCommits);
    List<Author> authors = toAuthors(gitCommits);
    List<ParsedIssue> parsedIssues = new IssueParser(settings, gitCommits).parseForIssues();
    List<Issue> issues = toIssues(parsedIssues);
    return new Tag(toReadableTagName(input), commits, authors, issues);
   }
  }));

  return order(tags);
 }

 private String toReadableTagName(String input) {
  Matcher matcher = compile(settings.getReadableTagName()).matcher(input);
  if (matcher.find()) {
   if (matcher.groupCount() == 0) {
    throw new RuntimeException("Pattern: \"" + settings.getReadableTagName() + "\" did not match any group in: \""
      + input + "\"");
   }
   return matcher.group(1);
  }
  return input;
 }

 private List<Tag> order(Iterable<Tag> tags) {
  return Ordering.from(new Comparator<Tag>() {
   @Override
   public int compare(Tag o1, Tag o2) {
    return o2.getCommit().getCommitTime().compareTo(o1.getCommit().getCommitTime());
   }
  }).sortedCopy(tags);
 }

 private List<String> withUnfilteredCommits(final Map<String, List<GitCommit>> commitsPerTag) {
  List<String> tagsWithCommits = newArrayList(filter(commitsPerTag.keySet(), new Predicate<String>() {
   @Override
   public boolean apply(String input) {
    return toCommits(commitsPerTag.get(input)).size() > 0;
   }
  }));
  return tagsWithCommits;
 }

 private <T extends IGitCommitReferer> Map<String, List<GitCommit>> getCommitsPerGitCommitReferer(
   List<GitCommit> allCommits, List<T> gitCommitReferer) {
  final Map<GitCommit, T> perCommit = uniqueIndex(gitCommitReferer, new Function<T, GitCommit>() {
   @Override
   public GitCommit apply(T input) {
    return input.getGitCommit();
   }
  });

  Map<GitCommit, String> stringPerCommit = newHashMap();
  for (GitCommit input : perCommit.keySet()) {
   if (perCommit.containsKey(input)) {
    stringPerCommit.put(input, perCommit.get(input).getName());
   } else {
    stringPerCommit.put(null, perCommit.get(input).getName());
   }
  }

  return commitsPerString(allCommits, stringPerCommit, settings.getUntaggedName());
 }

 private Map<String, List<GitCommit>> commitsPerString(List<GitCommit> allCommits,
   Map<GitCommit, String> stringPerCommit, String unMappedName) {
  final Map<String, List<GitCommit>> commitsPerString = newHashMap();
  String currentName = unMappedName;
  for (GitCommit gitCommit : allCommits) {
   String tag = stringPerCommit.get(gitCommit);
   if (tag != null) {
    currentName = tag;
   }
   if (!commitsPerString.containsKey(currentName)) {
    commitsPerString.put(checkNotNull(currentName, "currentTagName"), new ArrayList<GitCommit>());
   }

   commitsPerString.get(currentName).add(gitCommit);
  }
  return commitsPerString;
 }

 public List<Commit> toCommits(Collection<GitCommit> from) {
  List<GitCommit> filteredCommits = newArrayList(filter(from,
    ignoreCommits(settings.getIgnoreCommitsIfMessageMatches())));
  return newArrayList(transform(filteredCommits, new Function<GitCommit, Commit>() {
   @Override
   public Commit apply(GitCommit c) {
    return toCommit(c);
   }
  }));
 }

 public List<Issue> toIssues(List<ParsedIssue> issues) {
  Iterable<ParsedIssue> issuesWithCommits = filter(issues, new Predicate<ParsedIssue>() {
   @Override
   public boolean apply(ParsedIssue input) {
    return !toCommits(input.getGitCommits()).isEmpty();
   }
  });

  return newArrayList(transform(issuesWithCommits, new Function<ParsedIssue, Issue>() {
   @Override
   public Issue apply(ParsedIssue input) {
    List<GitCommit> gitCommits = input.getGitCommits();
    return new Issue(//
      toCommits(gitCommits), //
      toAuthors(gitCommits),//
      input.getName(), //
      input.getTitle().or(""), //
      input.getIssue(), //
      input.getLink());
   }

  }));
 }

 private Commit toCommit(GitCommit gitCommit) {
  return new Commit(//
    gitCommit.getAuthorName(), //
    gitCommit.getAuthorEmailAddress(), //
    format(gitCommit.getCommitTime()), //
    toMessage(gitCommit.getMessage()), //
    gitCommit.getHash());
 }

 private String toMessage(String message) {
  if (settings.removeIssueFromMessage()) {
   for (SettingsIssue issue : new IssuesUtil(settings).getIssues()) {
    message = message.replaceAll(issue.getPattern(), "");
   }
  }
  return message;
 }

 private String format(Date commitTime) {
  SimpleDateFormat df = new SimpleDateFormat(settings.getDateFormat());
  df.setTimeZone(getTimeZone(settings.getTimeZone()));
  return df.format(commitTime);
 }

 public List<Author> toAuthors(List<GitCommit> gitCommits) {
  final Multimap<String, GitCommit> commitsPerAuthor = index(gitCommits, new Function<GitCommit, String>() {
   @Override
   public String apply(GitCommit input) {
    return input.getAuthorEmailAddress() + "-" + input.getAuthorName();
   }
  });

  List<String> authorsWithCommits = newArrayList(filter(commitsPerAuthor.keySet(), new Predicate<String>() {
   @Override
   public boolean apply(String input) {
    return toCommits(commitsPerAuthor.get(input)).size() > 0;
   }
  }));

  return newArrayList(transform(authorsWithCommits, new Function<String, Author>() {
   @Override
   public Author apply(String input) {
    List<GitCommit> gitCommitsOfSameAuthor = newArrayList(commitsPerAuthor.get(input));
    List<Commit> commitsOfSameAuthor = toCommits(gitCommitsOfSameAuthor);
    return new Author(//
      commitsOfSameAuthor.get(0).getAuthorName(), //
      commitsOfSameAuthor.get(0).getAuthorEmailAddress(), //
      commitsOfSameAuthor);
   }
  }));
 }
}
