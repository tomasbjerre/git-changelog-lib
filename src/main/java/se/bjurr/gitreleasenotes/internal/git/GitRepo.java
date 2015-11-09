package se.bjurr.gitreleasenotes.internal.git;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import se.bjurr.gitreleasenotes.internal.git.model.GitCommit;
import se.bjurr.gitreleasenotes.internal.git.model.GitTag;
import se.bjurr.gitreleasenotes.internal.git.model.GitTags;

import com.google.common.base.Function;
import com.google.common.base.Optional;

public class GitRepo {

 private final Repository repository;

 public GitRepo(File repoFolder) {
  FileRepositoryBuilder builder = new FileRepositoryBuilder();
  Optional<File> gitDir = findClosestGitRepo(repoFolder);

  try {
   this.repository = builder//
     .setGitDir(gitDir.get())//
     .readEnvironment()//
     .findGitDir()//
     .build();
  } catch (IOException e) {
   throw propagate(e);
  }
 }

 public List<GitCommit> getDiff(String fromBranch, String toBranch) {
  Ref fromRef = getRef(fromBranch);
  Ref toRef = getRef(toBranch);
  try (Git git = new Git(repository)) {
   Iterable<RevCommit> itr = git //
     .log() //
     .addRange(fromRef.getObjectId(), toRef.getObjectId()) //
     .call(); //
   return newArrayList(transform(itr, new Function<RevCommit, GitCommit>() {
    @Override
    public GitCommit apply(RevCommit input) {
     return new GitCommit( //
       input.getAuthorIdent().getName(),//
       input.getAuthorIdent().getEmailAddress(),//
       new Date(input.getCommitTime() * 1000L),//
       input.getFullMessage(),//
       input.getId().getName());
    }
   }));
  } catch (Exception e) {
   throw propagate(e);
  }
 }

 public GitTags getTags() {
  return new GitTags( //
    from(repository.getTags().values()) //
      .transform((r) -> new GitTag(r.getName(), r.getObjectId().getName())) //
      .toList());
 }

 private Ref getRef(String fromBranch) {
  return checkNotNull(repository.getAllRefs().get(fromBranch),
    "Reference \"" + fromBranch + "\" not found in:\n" + on("\n").join(repository.getAllRefs().keySet()));
 }

 private static Optional<File> findClosestGitRepo(File file) {
  File candidate = new File(file.getAbsolutePath() + "/.git");
  if (candidate.exists()) {
   return of(candidate);
  }
  if (file.getParent() == null) {
   return absent();
  }
  return findClosestGitRepo(file.getParentFile());
 }
}
