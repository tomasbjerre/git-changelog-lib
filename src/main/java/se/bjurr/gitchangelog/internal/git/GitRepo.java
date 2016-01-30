package se.bjurr.gitchangelog.internal.git;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Iterators.getLast;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
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
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import se.bjurr.gitchangelog.api.GitChangelogApiConstants;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class GitRepo {
 private static final Function<RevCommit, GitCommit> TO_GITCOMMIT = new Function<RevCommit, GitCommit>() {
  @Override
  public GitCommit apply(RevCommit input) {
   return new GitCommit( //
     input.getAuthorIdent().getName(),//
     input.getAuthorIdent().getEmailAddress(),//
     new Date(input.getCommitTime() * 1000L),//
     input.getFullMessage(),//
     input.getId().getName().substring(0, 15));
  }
 };
 private final Repository repository;

 public GitRepo() {
  this.repository = null;
 }

 public GitRepo(File repo) {
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
    throw new RuntimeException("Did not find a GIT repo in " + repo.getAbsolutePath());
   }
   this.repository = builder.build();
  } catch (IOException e) {
   throw propagate(e);
  }
 }

 /**
  *
  * @param from
  *         From, but not including, this commit. Except for the
  *         {@link GitChangelogApiConstants#ZERO_COMMIT}, it is included.
  * @param to
  *         To and including this commit.
  */
 public List<GitCommit> getDiff(ObjectId from, ObjectId to) {
  Git git = null;
  try {
   git = new Git(repository);
   final List<RevCommit> toList = newArrayList(git.log().add(to).call());
   if (from.name().equals(firstCommit().name())) {
    return newArrayList(transform(toList, TO_GITCOMMIT));
   }

   List<RevCommit> fromList = newArrayList(git.log().add(from).call()); //
   Iterable<RevCommit> diff = filter(toList, notIn(fromList));

   return newArrayList(transform(diff, TO_GITCOMMIT));
  } catch (Exception e) {
   throw new RuntimeException("References:\n" + on("\n").join(getAllRefs().keySet()), e);
  } finally {
   git.close();
  }
 }

 private Predicate<RevCommit> notIn(final List<RevCommit> fromList) {
  return new Predicate<RevCommit>() {
   @Override
   public boolean apply(RevCommit input) {
    return !fromList.contains(input);
   }
  };
 }

 public List<GitTag> getTags() {
  Git git = null;
  try {
   git = new Git(repository);
   List<GitTag> refs = newArrayList();
   for (Ref ref : git.tagList().call()) {
    LogCommand log = git.log();

    Ref peeledRef = repository.peel(ref);
    if (peeledRef.getPeeledObjectId() != null) {
     log.add(peeledRef.getPeeledObjectId());
    } else {
     log.add(ref.getObjectId());
    }

    Iterable<RevCommit> itr = log.call();

    List<GitCommit> commits = transform(newArrayList(itr), TO_GITCOMMIT);

    refs.add(new GitTag(//
      ref.getName(), //
      commits));
   }

   return refs;
  } catch (Exception e) {
   throw propagate(e);
  } finally {
   git.close();
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
  String s = ""//
    + "First commit at: " + firstCommit().name() + "\n"//
    + "Refs: \n";
  Map<String, Ref> allRefs = getAllRefs();
  for (String k : allRefs.keySet()) {
   ObjectId pealed = firstNonNull(allRefs.get(k).getPeeledObjectId(), pealed = allRefs.get(k).getObjectId());
   s += "Ref: " + k + " -> " + pealed.name() + "\n";
  }
  return s;
 }
}
