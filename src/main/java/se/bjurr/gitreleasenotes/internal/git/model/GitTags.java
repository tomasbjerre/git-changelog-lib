package se.bjurr.gitreleasenotes.internal.git.model;

import static com.google.common.collect.Maps.uniqueIndex;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

public class GitTags {
 private final List<GitTag> gitReferenceList;
 private final ImmutableMap<String, GitTag> gitReferencesPerCommit;
 private final ImmutableMap<String, GitTag> gitReferencesPerName;

 public GitTags(List<GitTag> gitReferenceList) {
  this.gitReferenceList = gitReferenceList;
  gitReferencesPerCommit = uniqueIndex(gitReferenceList, new Function<GitTag, String>() {
   @Override
   public String apply(GitTag input) {
    return input.getCommit();
   }
  });
  gitReferencesPerName = uniqueIndex(gitReferenceList, new Function<GitTag, String>() {
   @Override
   public String apply(GitTag input) {
    return input.getName();
   }
  });
 }

 public ImmutableMap<String, GitTag> getPerCommit() {
  return gitReferencesPerCommit;
 }

 public ImmutableMap<String, GitTag> getPerName() {
  return gitReferencesPerName;
 }

 public List<GitTag> getGitReferenceList() {
  return gitReferenceList;
 }
}
