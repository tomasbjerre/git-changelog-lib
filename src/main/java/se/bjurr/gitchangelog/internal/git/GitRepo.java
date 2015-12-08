package se.bjurr.gitchangelog.internal.git;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static org.eclipse.jgit.lib.ObjectId.fromString;
import static org.eclipse.jgit.revwalk.RevSort.REVERSE;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

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

 public List<GitCommit> getDiff(ObjectId from, ObjectId to) {
  Git git = null;
  try {
   git = new Git(repository);
   Iterable<RevCommit> itr = git //
     .log() //
     .addRange(from, to) //
     .call(); //
   return newArrayList(transform(itr, TO_GITCOMMIT));
  } catch (Exception e) {
   throw new RuntimeException("References:\n" + on("\n").join(repository.getAllRefs().keySet()), e);
  } finally {
   git.close();
  }
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
   for (Ref foundRef : repository.getAllRefs().values()) {
    if (foundRef.getName().endsWith(fromRef)) {
     Ref ref = repository.getAllRefs().get(foundRef.getName());
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
   throw new RuntimeException(fromRef + " not found in:\n" + on("\n").join(repository.getAllRefs().values()));
  } catch (Exception e) {
   throw new RuntimeException("", e);
  }
 }

 public ObjectId getCommit(String fromCommit) {
  if (fromCommit.startsWith(ZERO_COMMIT)) {
   return firstCommit();
  }
  return fromString(fromCommit);
 }

 private ObjectId firstCommit() {
  RevWalk walk = null;
  try {
   walk = new RevWalk(repository);
   RevCommit root = walk.parseCommit(repository.resolve(Constants.HEAD));
   walk.sort(REVERSE);
   walk.markStart(root);
   return walk.next();
  } catch (Exception e) {
   throw new RuntimeException("First commit not found in " + repository.getDirectory(), e);
  }
 }
}
