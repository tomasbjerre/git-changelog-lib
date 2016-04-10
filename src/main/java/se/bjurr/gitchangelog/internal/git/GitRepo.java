package se.bjurr.gitchangelog.internal.git;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterators.getLast;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newTreeSet;
import static java.util.regex.Pattern.compile;
import static org.eclipse.jgit.lib.ObjectId.fromString;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.REF_MASTER;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.bjurr.gitchangelog.api.GitChangelogApiConstants;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogRepositoryException;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

import com.google.common.base.Optional;

public class GitRepo implements Closeable {
 private static final Logger LOG = LoggerFactory.getLogger(GitRepo.class);
 private final Repository repository;
 private final RevWalk revWalk;
 private Git git;

 public GitRepo() {
  this.repository = null;
  this.revWalk = null;
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
   this.revWalk = new RevWalk(repository);
   this.git = new Git(repository);
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
  */
 public GitRepoData getGitRepoData(ObjectId from, ObjectId to, String untaggedName,
   Optional<String> ignoreTagsIfNameMatches) throws GitChangelogRepositoryException {
  try {
   return new GitRepoData(gitTags(from, to, untaggedName, ignoreTagsIfNameMatches));
  } catch (Exception e) {
   throw new GitChangelogRepositoryException(toString(), e);
  }
 }

 public ObjectId getRef(String fromRef) {
  try {
   for (Ref foundRef : getAllRefs().values()) {
    if (foundRef.getName().endsWith(fromRef)) {
     Ref ref = getAllRefs().get(foundRef.getName());
     Ref peeledRef = repository.peel(ref);
     if (peeledRef.getPeeledObjectId() != null) {
      return peeledRef.getPeeledObjectId();
     } else {
      return ref.getObjectId();
     }
    }
   }
   throw new RuntimeException(fromRef + " not found in:\n" + toString());
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

 private List<GitTag> gitTags(ObjectId from, ObjectId to, String untaggedName, Optional<String> ignoreTagsIfNameMatches)
   throws Exception {
  List<Ref> tagList = tagsBetweenFromAndTo(from, to);
  /**
   * What: Contains only the commits that are directly referred to by tags.<br>
   * Why: To know if a new tag was found when walking up through the parents.
   */
  Map<String, Ref> tagPerCommitHash = getTagPerCommitHash(ignoreTagsIfNameMatches, tagList);
  /**
   * What: Populated with all included commits, referring to there tags.<br>
   * Why: To know if a commit is already mapped to a tag, or not.
   */
  Map<String, String> tagPerCommitsHash = newHashMap();
  /**
   * What: Commits per tag.<br>
   * Why: Its what we are here for! =)
   */
  Map<String, Set<GitCommit>> commitsPerTag = newHashMap();

  populateComitPerTag(from, to, tagPerCommitHash, tagPerCommitsHash, commitsPerTag, null);
  populateComitPerTag(from, to, tagPerCommitHash, tagPerCommitsHash, commitsPerTag, untaggedName);

  List<GitTag> tags = newArrayList();
  addToTags(commitsPerTag, tags, untaggedName);
  List<String> tagCommitHashSortedByCommitTime = tagCommitHashSortedByCommitTime(tagPerCommitsHash.keySet());
  for (String tagCommitHash : tagCommitHashSortedByCommitTime) {
   if (tagPerCommitHash.containsKey(tagCommitHash)) {
    Ref tag = tagPerCommitHash.get(tagCommitHash);
    addToTags(commitsPerTag, tags, tag.getName());
   }
  }
  return tags;
 }

 /**
  * This can be done recursively but will result in {@link StackOverflowError}
  * for large repos.
  */
 private void populateComitPerTag(ObjectId from, ObjectId to, Map<String, Ref> tagPerCommitHash,
   Map<String, String> tagPerCommitsHash, Map<String, Set<GitCommit>> commitsPerTag, String startingTagName)
   throws Exception {
  Set<TraversalWork> moreWork = populateCommitPerTag(from, to, commitsPerTag, tagPerCommitHash, tagPerCommitsHash,
    startingTagName);
  do {
   Set<TraversalWork> evenMoreWork = newTreeSet();
   for (TraversalWork tw : newArrayList(moreWork)) {
    moreWork.remove(tw);
    evenMoreWork.addAll(populateCommitPerTag(from, tw.getTo(), commitsPerTag, tagPerCommitHash, tagPerCommitsHash,
      tw.getCurrentTagName()));
   }
   moreWork.addAll(evenMoreWork);
   LOG.debug("Work left: " + moreWork.size());
  } while (!moreWork.isEmpty());
 }

 private List<Ref> tagsBetweenFromAndTo(ObjectId from, ObjectId to) throws Exception {
  List<Ref> tagList = git.tagList().call();
  List<RevCommit> icludedCommits = newArrayList(git.log().addRange(from, to).call());
  List<Ref> includedTags = newArrayList();
  for (Ref tag : tagList) {
   ObjectId peeledTag = getPeeled(tag);
   if (icludedCommits.contains(peeledTag)) {
    includedTags.add(tag);
   }
  }
  return includedTags;
 }

 private List<String> tagCommitHashSortedByCommitTime(Set<String> commitHashes) {
  List<String> sorted = newArrayList(commitHashes);
  Collections.sort(sorted, new Comparator<String>() {
   @Override
   public int compare(String hash1, String hash2) {
    try {
     RevCommit revCommit1 = revWalk.lookupCommit(getCommit(hash1));
     revWalk.parseHeaders(revCommit1);
     RevCommit revCommit2 = revWalk.lookupCommit(getCommit(hash2));
     revWalk.parseHeaders(revCommit2);
     return toGitCommit(revCommit1).compareTo(toGitCommit(revCommit2));
    } catch (IOException e) {
     throw propagate(e);
    }
   }
  });
  return sorted;
 }

 private void addToTags(Map<String, Set<GitCommit>> commitsPerTag, List<GitTag> addTo, String tagName) {
  if (commitsPerTag.containsKey(tagName)) {
   Set<GitCommit> gitCommits = commitsPerTag.get(tagName);
   GitTag gitTag = new GitTag(tagName, newArrayList(gitCommits));
   addTo.add(gitTag);
  }
 }

 private Set<TraversalWork> populateCommitPerTag(ObjectId from, ObjectId to,
   Map<String, Set<GitCommit>> commitsPerTagName, Map<String, Ref> tagPerCommitHash,
   Map<String, String> tagPerCommitsHash, String currentTagName) throws Exception {
  String thisCommitHash = to.getName();
  if (isNotMappedToAnotherTag(tagPerCommitsHash, thisCommitHash)) {
   RevCommit thisCommit = revWalk.lookupCommit(to);
   revWalk.parseHeaders(thisCommit);
   if (thisIsANewTag(tagPerCommitHash, thisCommitHash)) {
    currentTagName = getTagName(tagPerCommitHash, thisCommitHash);
   }
   if (currentTagName != null) {
    addCommitToCurrentTag(commitsPerTagName, currentTagName, thisCommit);
    noteThatTheCommitWasMapped(tagPerCommitsHash, currentTagName, thisCommitHash);
   }
   if (notFirstIncludedCommit(from, to)) {
    Set<TraversalWork> work = newHashSet();
    for (RevCommit parent : thisCommit.getParents()) {
     work.add(new TraversalWork(parent, currentTagName));
    }
    return work;
   }
  }
  return newHashSet();
 }

 private boolean thisIsANewTag(Map<String, Ref> tagsPerCommitHash, String thisCommitHash) {
  return tagsPerCommitHash.containsKey(thisCommitHash);
 }

 private String getTagName(Map<String, Ref> tagPerCommitHash, String thisCommitHash) {
  return tagPerCommitHash.get(thisCommitHash).getName();
 }

 private boolean notFirstIncludedCommit(ObjectId from, ObjectId to) {
  return !from.getName().equals(to.getName());
 }

 private void addCommitToCurrentTag(Map<String, Set<GitCommit>> commitsPerTagName, String currentTagName,
   RevCommit thisCommit) {
  GitCommit gitCommit = toGitCommit(thisCommit);
  if (!commitsPerTagName.containsKey(currentTagName)) {
   commitsPerTagName.put(currentTagName, new TreeSet<GitCommit>());
  }
  Set<GitCommit> gitCommitsInCurrentTag = commitsPerTagName.get(currentTagName);
  gitCommitsInCurrentTag.add(gitCommit);
 }

 private String noteThatTheCommitWasMapped(Map<String, String> tagPerCommitsHash, String currentTagName,
   String thisCommitHash) {
  return tagPerCommitsHash.put(thisCommitHash, currentTagName);
 }

 private boolean isNotMappedToAnotherTag(Map<String, String> tagPerCommitsHash, String thisCommitHash) {
  return !tagPerCommitsHash.containsKey(thisCommitHash);
 }

 private Map<String, Ref> getTagPerCommitHash(Optional<String> ignoreTagsIfNameMatches, List<Ref> tagList) {
  Map<String, Ref> tagPerCommit = newHashMap();
  for (Ref tag : tagList) {
   if (ignoreTagsIfNameMatches.isPresent()) {
    if (compile(ignoreTagsIfNameMatches.get()).matcher(tag.getName()).matches()) {
     continue;
    }
   }
   tagPerCommit.put(getPeeled(tag).getName(), tag);
  }
  return tagPerCommit;
 }

 private ObjectId getPeeled(Ref tag) {
  Ref peeledTag = repository.peel(tag);
  if (peeledTag.getPeeledObjectId() != null) {
   return peeledTag.getPeeledObjectId();
  } else {
   return tag.getObjectId();
  }
 }

 private Map<String, Ref> getAllRefs() {
  return repository.getAllRefs();
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

 private GitCommit toGitCommit(RevCommit revCommit) {
  return new GitCommit( //
    revCommit.getAuthorIdent().getName(),//
    revCommit.getAuthorIdent().getEmailAddress(),//
    new Date(revCommit.getCommitTime() * 1000L),//
    revCommit.getFullMessage(),//
    revCommit.getId().getName());
 }

 @Override
 public String toString() {
  return "Repo: " + repository;
 }

 @Override
 public void close() throws IOException {
  git.close();
  repository.close();
 }
}
