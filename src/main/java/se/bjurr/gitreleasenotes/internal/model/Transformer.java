package se.bjurr.gitreleasenotes.internal.model;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Multimaps.index;

import java.util.List;

import se.bjurr.gitreleasenotes.api.model.Author;
import se.bjurr.gitreleasenotes.api.model.Commit;
import se.bjurr.gitreleasenotes.api.model.Issue;
import se.bjurr.gitreleasenotes.api.model.Tag;
import se.bjurr.gitreleasenotes.internal.git.model.GitCommit;
import se.bjurr.gitreleasenotes.internal.git.model.GitTag;
import se.bjurr.gitreleasenotes.internal.git.model.GitTags;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;

public class Transformer {

 public static List<Tag> toTags(List<GitCommit> from, GitTags references, List<ParsedIssue> parsedIssues) {
  Iterable<GitTag> tags = filter(references.getGitReferenceList(), new Predicate<GitTag>() {
   @Override
   public boolean apply(GitTag input) {
    return false;
   }
  });

  return transform(newArrayList(tags), new Function<GitTag, Tag>() {
   @Override
   public Tag apply(GitTag input) {
    List<Commit> commits = null;
    List<Author> authors = null;
    List<Issue> issues = null;
    return new Tag(input.getName(), commits, authors, issues);
   }
  });
 }

 public static List<Commit> toCommits(List<GitCommit> from) {
  return transform(from, (c) -> {
   return toCommit(c);
  });
 }

 public static List<Issue> toIssues(List<GitCommit> diff, List<ParsedIssue> issues) {
  return transform(issues, new Function<ParsedIssue, Issue>() {
   @Override
   public Issue apply(ParsedIssue input) {
    List<Author> authors = null;
    return new Issue(//
      newArrayList(toCommit(input.getGitCommit())), //
      authors,//
      input.getName(), //
      input.getDescription());
   }
  });
 }

 private static Commit toCommit(GitCommit gitCommit) {
  return new Commit(//
    gitCommit.getAuthorName(), //
    gitCommit.getAuthorEmailAddress(), //
    gitCommit.getCommitTime(), //
    gitCommit.getMessage(), //
    gitCommit.getHash());
 }

 public static List<Author> toAuthors(List<GitCommit> gitCommits) {
  Multimap<String, GitCommit> commitsPerAuthor = index(gitCommits, new Function<GitCommit, String>() {
   @Override
   public String apply(GitCommit input) {
    return input.getAuthorEmailAddress() + "-" + input.getAuthorName();
   }
  });

  return transform(newArrayList(commitsPerAuthor.keys()), new Function<String, Author>() {
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
