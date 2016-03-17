package se.bjurr.gitchangelog.internal.git;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Iterators.getLast;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.newHashMap;
import static org.eclipse.jgit.lib.ObjectId.fromString;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_MASTER;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import se.bjurr.gitchangelog.api.GitChangelogApiConstants;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogRepositoryException;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

import com.google.common.base.Function;

public class GitRepo {
 private static final Function<RevCommit, GitCommit> TO_GITCOMMIT = new Function<RevCommit, GitCommit>() {
  @Override
  public GitCommit apply(RevCommit input) {
   return new GitCommit( //
     input.getAuthorIdent().getName(),//
     input.getAuthorIdent().getEmailAddress(),//
     new Date(input.getCommitTime() * 1000L),//
     input.getFullMessage(),//
     toHash(input.getId().getName()));
  }
 };

 private static String toHash(String input) {
  return input.substring(0, 15);
 }

 private final Repository repository;

 public GitRepo() {
  this.repository = null;
 }

 public GitRepo(File repo) throws GitChangelogRepositoryException {
  try {
   File repoFile = new File(repo.getAbsolutePath());
   File gitRepoFile = new File(repo.getAbsolutePath() + "/.git");
   if (gitRepoFile.exists()) {
    repoFile = gitRepoFile;
   }
   FileRepositoryBuilder builder = new FileRepositoryBuilder()//
     .findGitDir(repoFile)//
     .readEnvironment();
   if (builder.getGitDir() == null) {
    throw new GitChangelogRepositoryException("Did not find a GIT repo in " + repo.getAbsolutePath());
   }
   this.repository = builder.build();
  } catch (IOException e) {
   throw new GitChangelogRepositoryException("Could not use GIT repo in " + repo.getAbsolutePath(), e);
  }
 }

 /**
  *
  * @param from
  *         From, but not including, this commit. Except for the
  *         {@link GitChangelogApiConstants#ZERO_COMMIT}, it is included.
  * @param to
  *         To and including this commit.
  * @param untaggedName
  * @throws GitChangelogRepositoryException
  */
 public GitRepoData getGitRepoData(ObjectId from, ObjectId to, String untaggedName)
   throws GitChangelogRepositoryException {
  Git git = null;
  try {
   git = new Git(repository);
   List<GitCommit> gitCommits = getGitCommits(git, from, to);
   return new GitRepoData(gitCommits, gitTags(git, gitCommits, untaggedName));
  } catch (Exception e) {
   throw new GitChangelogRepositoryException(toString(), e);
  } finally {
   git.close();
  }
 }

 private List<GitTag> gitTags(Git git, List<GitCommit> gitCommits, String untaggedName) throws GitAPIException {
  List<GitTag> refs = newArrayList();
  List<Ref> refList = git.tagList().call();
  Map<String, Ref> refsPerCommit = newHashMap();
  for (Ref ref : refList) {
   refsPerCommit.put(toHash(getPeeled(ref).getName()), ref);
  }

  String currentTagName = untaggedName;
  List<GitCommit> gitCommitsInCurrentTag = newArrayList();
  for (GitCommit gitCommit : gitCommits) {
   if (refsPerCommit.containsKey(gitCommit.getHash())) {
    if (!gitCommitsInCurrentTag.isEmpty()) {
     GitTag newTag = new GitTag(currentTagName, gitCommitsInCurrentTag);
     refs.add(newTag);
     gitCommitsInCurrentTag = newArrayList();
    }
    if (refsPerCommit.containsKey(gitCommit.getHash())) {
     currentTagName = refsPerCommit.get(gitCommit.getHash()).getName();
    } else {
     currentTagName = untaggedName;
    }
   }
   gitCommitsInCurrentTag.add(gitCommit);
  }
  if (!gitCommitsInCurrentTag.isEmpty()) {
   GitTag newTag = new GitTag(currentTagName, gitCommitsInCurrentTag);
   refs.add(newTag);
  }

  return refs;
 }

 private List<GitCommit> getGitCommits(Git git, ObjectId from, ObjectId to) throws GitChangelogRepositoryException {
  try {
   final List<RevCommit> toList = newArrayList(git.log().add(to).call());
   if (from.name().equals(firstCommit().name())) {
    return newArrayList(transform(toList, TO_GITCOMMIT));
   }

   Iterable<RevCommit> itr = git //
     .log() //
     .addRange(from, to) //
     .call();

   return newArrayList(transform(itr, TO_GITCOMMIT));
  } catch (Exception e) {
   throw new GitChangelogRepositoryException(from.name() + " -> " + to.name(), e);
  }
 }

 private ObjectId getPeeled(Ref ref) {
  Ref peeledRef = repository.peel(ref);
  if (peeledRef.getPeeledObjectId() != null) {
   return peeledRef.getPeeledObjectId();
  } else {
   return ref.getObjectId();
  }
 }

 public ObjectId getRef(String fromRef) {
  try {
   for (Ref foundRef : getAllRefs().values()) {
    if (foundRef.getName().endsWith(fromRef)) {
     Ref ref = getAllRefs().get(foundRef.getName());
     Git git = null;
     try {
      git = new Git(repository);
      Ref peeledRef = repository.peel(ref);
      if (peeledRef.getPeeledObjectId() != null) {
       return peeledRef.getPeeledObjectId();
      } else {
       return ref.getObjectId();
      }
     } finally {
      git.close();
     }
    }
   }
   throw new RuntimeException(fromRef + " not found in:\n" + toString());
  } catch (Exception e) {
   throw new RuntimeException("", e);
  }
 }

 private Map<String, Ref> getAllRefs() {
  return repository.getAllRefs();
 }

 public ObjectId getCommit(String fromCommit) {
  if (fromCommit.startsWith(ZERO_COMMIT)) {
   return firstCommit();
  }
  return fromString(fromCommit);
 }

 private RevCommit firstCommit() {
  Git git = null;
  try {
   git = new Git(repository);
   AnyObjectId master = getRef(REF_MASTER);
   Iterator<RevCommit> itr = git.log().add(master).call().iterator();
   return getLast(itr);
  } catch (Exception e) {
   throw new RuntimeException("First commit not found in " + repository.getDirectory(), e);
  } finally {
   git.close();
  }
 }

 @Override
 public String toString() {
  return "Repo: " + repository;
 }
}
