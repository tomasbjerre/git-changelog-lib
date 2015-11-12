package se.bjurr.gitchangelog.internal.model;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Multimaps.index;
import static com.google.common.collect.Ordering.from;
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
import se.bjurr.gitchangelog.internal.settings.Settings;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public class Transformer {
 public Transformer(Settings settings) {
  this.settings = settings;
 }

 private final Settings settings;

 public List<Tag> toTags(List<GitCommit> allCommits, List<GitTag> gitTags, final List<ParsedIssue> parsedIssues) {
  final Map<String, List<GitCommit>> commitsPerTag = getCommitsPerTag(allCommits, gitTags);

  List<String> withUnfilteredCommits = withUnfilteredCommits(commitsPerTag);

  Iterable<Tag> tags = transform(withUnfilteredCommits, new Function<String, Tag>() {
   @Override
   public Tag apply(String input) {
    List<GitCommit> gitCommits = commitsPerTag.get(input);
    List<Commit> commits = toCommits(gitCommits);
    List<Author> authors = toAuthors(gitCommits);
    List<Issue> issues = toIssues(gitCommits, parsedIssues);
    return new Tag(toReadableTagName(input), commits, authors, issues);
   }
  });

  return order(tags);
 }

 private String toReadableTagName(String input) {
  Matcher matcher = compile(settings.getReadableTagPattern()).matcher(input);
  if (matcher.find()) {
   return matcher.group(1);
  }
  return input;
 }

 private List<Tag> order(Iterable<Tag> tags) {
  return from(new Comparator<Tag>() {
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

 private Map<String, List<GitCommit>> getCommitsPerTag(List<GitCommit> allCommits, List<GitTag> gitTags) {
  Map<GitCommit, GitTag> tagPerCommit = uniqueIndex(gitTags, new Function<GitTag, GitCommit>() {
   @Override
   public GitCommit apply(GitTag input) {
    return input.getGitCommit();
   }
  });

  final Map<String, List<GitCommit>> commitsPerTag = newHashMap();
  String currentTagName = null;
  for (GitCommit gitCommit : allCommits) {
   GitTag tag = tagPerCommit.get(gitCommit);
   if (tag == null && currentTagName == null) {
    currentTagName = settings.getUntaggedName();
   } else if (tag != null) {
    currentTagName = tag.getName();
   }
   if (!commitsPerTag.containsKey(currentTagName)) {
    commitsPerTag.put(currentTagName, new ArrayList<GitCommit>());
   }

   commitsPerTag.get(currentTagName).add(gitCommit);
  }
  return commitsPerTag;
 }

 public List<Commit> toCommits(Collection<GitCommit> from) {
  List<GitCommit> filteredCommits = newArrayList(Iterables.filter(from,
    ignoreCommits(settings.getIgnoreCommitsIfMessageMatches())));
  return transform(filteredCommits, new Function<GitCommit, Commit>() {
   @Override
   public Commit apply(GitCommit c) {
    return toCommit(c);
   }
  });
 }

 public List<Issue> toIssues(List<GitCommit> diff, List<ParsedIssue> issues) {
  return transform(issues, new Function<ParsedIssue, Issue>() {
   @Override
   public Issue apply(ParsedIssue input) {
    List<Author> authors = null;
    return new Issue(//
      newArrayList(toCommit(input.getGitCommit())), //
      authors,//
      input.getName(), //
      input.getLink());
   }
  });
 }

 private Commit toCommit(GitCommit gitCommit) {
  return new Commit(//
    gitCommit.getAuthorName(), //
    gitCommit.getAuthorEmailAddress(), //
    format(gitCommit.getCommitTime()), //
    gitCommit.getMessage(), //
    gitCommit.getHash());
 }

 private String format(Date commitTime) {
  return new SimpleDateFormat(settings.getDateFormat()).format(commitTime);
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

  return transform(newArrayList(authorsWithCommits), new Function<String, Author>() {
   @Override
   public Author apply(String input) {
    List<GitCommit> gitCommitsOfSameAuthor = newArrayList(commitsPerAuthor.get(input));
    List<Commit> commitsOfSameAuthor = toCommits(gitCommitsOfSameAuthor);
    return new Author(//
      commitsOfSameAuthor.get(0).getAuthorName(), //
      commitsOfSameAuthor.get(0).getAuthorEmailAddress(), //
      commitsOfSameAuthor);
   }
  });
 }
}
