package se.bjurr.gitchangelog.api;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.reverse;
import static com.google.common.collect.Maps.uniqueIndex;
import static org.eclipse.jgit.lib.ObjectId.fromString;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.ObjectId;

import se.bjurr.gitchangelog.internal.git.GitRepo;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

import com.google.common.base.Function;

public class FakeGitRepo extends GitRepo {

 public static long DAY_ZERO = 0L;
 public static long TIME_DAY = 86400000L;

 private final List<GitCommit> commits = newArrayList();
 private final List<GitTag> tags = newArrayList();

 public FakeGitRepo() {
  commits.add(new GitCommit("authorName", "authorEmailAddress", new Date(DAY_ZERO), "First commit!", ZERO_COMMIT));
 }

 public FakeGitRepo withCommit(GitCommit gitCommit) {
  commits.add(0, gitCommit);
  return this;
 }

 public FakeGitRepo withTag(GitTag gitTag) {
  tags.add(gitTag);
  return this;
 }

 @Override
 public ObjectId getCommit(String fromCommit) {
  Map<String, GitCommit> commitsMap = uniqueIndex(commits, new Function<GitCommit, String>() {
   @Override
   public String apply(GitCommit input) {
    return input.getHash();
   }
  });
  return fromString(checkNotNull(commitsMap.get(fromCommit),
    "Not found: " + fromCommit + " in:\n" + on("\n").join(commitsMap.keySet())).getHash());
 }

 @Override
 public ObjectId getRef(String ref) {
  if (ref.endsWith("master")) {
   return fromString(commits.get(0).getHash());
  }
  Map<String, GitTag> tagsMap = uniqueIndex(tags, new Function<GitTag, String>() {
   @Override
   public String apply(GitTag input) {
    return input.getGitCommit().getHash();
   }
  });
  return fromString(checkNotNull(tagsMap.get(ref), "Not found: " + ref + " in:\n" + on("\n").join(tagsMap.keySet()))
    .getGitCommit().getHash());
 }

 @Override
 public List<GitTag> getTags() {
  return tags;
 }

 @Override
 public List<GitCommit> getDiff(ObjectId from, ObjectId to) {
  List<GitCommit> toReturn = newArrayList();
  boolean included = false;
  for (GitCommit gitCommit : reverse(commits)) {
   if (gitCommit.getHash().equals(from.getName())) {
    included = true;
   }
   if (included) {
    toReturn.add(0, gitCommit);
   }
   if (gitCommit.getHash().equals(to.getName())) {
    break;
   }
  }
  return toReturn;
 }

}
