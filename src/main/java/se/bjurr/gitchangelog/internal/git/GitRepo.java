package se.bjurr.gitchangelog.internal.git;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterators.getLast;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newTreeSet;
import static java.util.regex.Pattern.compile;
import static org.eclipse.jgit.lib.Constants.HEAD;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
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
import com.google.common.collect.Ordering;

public class GitRepo implements Closeable {
 private static final Logger LOG = LoggerFactory.getLogger(GitRepo.class);
 private Git git;
 private List<RevCommit> includedCommits;
 private final Repository repository;
 private final RevWalk revWalk;

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
   this.revWalk = new RevWalk(this.repository);
   this.git = new Git(this.repository);
  } catch (IOException e) {
   throw new GitChangelogRepositoryException("Could not use GIT repo in " + repo.getAbsolutePath(), e);
  }
 }

 @Override
 public void close() throws IOException {
  this.git.close();
  this.repository.close();
 }

 public ObjectId getCommit(String fromCommit) throws GitChangelogRepositoryException {
  if (fromCommit.startsWith(ZERO_COMMIT)) {
   return firstCommit();
  }
  try {
   return this.repository.resolve(fromCommit);
  } catch (Exception e) {
   throw new GitChangelogRepositoryException("", e);
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

 public ObjectId getRef(String fromRef) throws GitChangelogRepositoryException {
  try {
   for (Ref foundRef : getAllRefs().values()) {
    if (foundRef.getName().endsWith(fromRef)) {
     Ref ref = getAllRefs().get(foundRef.getName());
     Ref peeledRef = this.repository.peel(ref);
     if (peeledRef.getPeeledObjectId() != null) {
      return peeledRef.getPeeledObjectId();
     } else {
      return ref.getObjectId();
     }
    }
   }
   throw new GitChangelogRepositoryException(fromRef + " not found in:\n" + toString());
  } catch (Exception e) {
   throw new GitChangelogRepositoryException(e.getMessage(), e);
  }
 }

 @Override
 public String toString() {
  return "Repo: " + this.repository;
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

 private void addToTags(Map<String, Set<GitCommit>> commitsPerTag, String tagName, List<GitTag> addTo) {
  if (commitsPerTag.containsKey(tagName)) {
   Set<GitCommit> gitCommits = commitsPerTag.get(tagName);
   GitTag gitTag = new GitTag(tagName, newArrayList(gitCommits));
   addTo.add(gitTag);
  }
 }

 private RevCommit firstCommit() {
  Git git = null;
  try {
   git = new Git(this.repository);
   AnyObjectId master = getRef(HEAD);
   Iterator<RevCommit> itr = git.log().add(master).call().iterator();
   return getLast(itr);
  } catch (Exception e) {
   throw new RuntimeException("First commit not found in " + this.repository.getDirectory(), e);
  } finally {
   git.close();
  }
 }

 private Map<String, Ref> getAllRefs() {
  return this.repository.getAllRefs();
 }

 private ObjectId getPeeled(Ref tag) {
  Ref peeledTag = this.repository.peel(tag);
  if (peeledTag.getPeeledObjectId() != null) {
   return peeledTag.getPeeledObjectId();
  } else {
   return tag.getObjectId();
  }
 }

 private List<Ref> getTagCommitHashSortedByCommitTime(Collection<Ref> refs) {
  return Ordering.from(new Comparator<Ref>() {
   @Override
   public int compare(Ref o1, Ref o2) {
    RevCommit revCommit1 = GitRepo.this.revWalk.lookupCommit(getPeeled(o1));
    try {
     GitRepo.this.revWalk.parseHeaders(revCommit1);
     RevCommit revCommit2 = GitRepo.this.revWalk.lookupCommit(getPeeled(o2));
     GitRepo.this.revWalk.parseHeaders(revCommit2);
     return toGitCommit(revCommit1).compareTo(toGitCommit(revCommit2));
    } catch (Exception e) {
     throw propagate(e);
    }
   }
  })//
    .sortedCopy(refs);
 }

 private String getTagName(Map<String, Ref> tagPerCommitHash, String thisCommitHash) {
  return tagPerCommitHash.get(thisCommitHash).getName();
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

 private List<GitTag> gitTags(ObjectId fromObjectId, ObjectId toObjectId, String untaggedName,
   Optional<String> ignoreTagsIfNameMatches) throws Exception {
  final RevCommit from = this.revWalk.lookupCommit(fromObjectId);
  RevCommit to = this.revWalk.lookupCommit(toObjectId);

  this.includedCommits = newArrayList(this.git.log().addRange(from, to).call());
  if (LOG.isDebugEnabled()) {
   LOG.debug("Included commits " + from.getName() + " -> " + to.getName());
   for (RevCommit inc : this.includedCommits) {
    LOG.debug(inc.getName());
   }
  }

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
  addToTags(commitsPerTag, untaggedName, tags);
  List<Ref> tagCommitHashSortedByCommitTime = getTagCommitHashSortedByCommitTime(tagPerCommitHash.values());
  for (Ref tag : tagCommitHashSortedByCommitTime) {
   addToTags(commitsPerTag, tag.getName(), tags);
  }
  return tags;
 }

 private boolean isMappedToAnotherTag(Map<String, String> tagPerCommitsHash, String thisCommitHash) {
  return tagPerCommitsHash.containsKey(thisCommitHash);
 }

 private String noteThatTheCommitWasMapped(Map<String, String> tagPerCommitsHash, String currentTagName,
   String thisCommitHash) {
  return tagPerCommitsHash.put(thisCommitHash, currentTagName);
 }

 private boolean notFirstIncludedCommit(ObjectId from, ObjectId to) {
  return !from.getName().equals(to.getName());
 }

 /**
  * This can be done recursively but will result in {@link StackOverflowError}
  * for large repos.
  */
 private void populateComitPerTag(RevCommit from, ObjectId to, Map<String, Ref> tagPerCommitHash,
   Map<String, String> tagPerCommitsHash, Map<String, Set<GitCommit>> commitsPerTag, String startingTagName)
   throws Exception {
  Set<TraversalWork> moreWork = populateCommitPerTag(from, to, commitsPerTag, tagPerCommitHash, tagPerCommitsHash,
    startingTagName);
  do {
   Set<TraversalWork> evenMoreWork = newTreeSet();
   for (TraversalWork tw : newArrayList(moreWork)) {
    moreWork.remove(tw);
    Set<TraversalWork> newWork = populateCommitPerTag(from, tw.getTo(), commitsPerTag, tagPerCommitHash,
      tagPerCommitsHash, tw.getCurrentTagName());
    evenMoreWork.addAll(newWork);
   }
   moreWork.addAll(evenMoreWork);
   LOG.debug("Work left: " + moreWork.size());
  } while (!moreWork.isEmpty());
 }

 private Set<TraversalWork> populateCommitPerTag(RevCommit from, ObjectId to,
   Map<String, Set<GitCommit>> commitsPerTagName, Map<String, Ref> tagPerCommitHash,
   Map<String, String> tagPerCommitsHash, String currentTagName) throws Exception {
  String thisCommitHash = to.getName();
  if (isMappedToAnotherTag(tagPerCommitsHash, thisCommitHash)) {
   return newTreeSet();
  }
  RevCommit thisCommit = this.revWalk.lookupCommit(to);
  this.revWalk.parseHeaders(thisCommit);
  if (thisIsANewTag(tagPerCommitHash, thisCommitHash)) {
   currentTagName = getTagName(tagPerCommitHash, thisCommitHash);
  }
  if (currentTagName != null) {
   addCommitToCurrentTag(commitsPerTagName, currentTagName, thisCommit);
   noteThatTheCommitWasMapped(tagPerCommitsHash, currentTagName, thisCommitHash);
  }
  if (notFirstIncludedCommit(from, to)) {
   Set<TraversalWork> work = newTreeSet();
   for (RevCommit parent : thisCommit.getParents()) {
    if (shouldInclude(thisCommit)) {
     work.add(new TraversalWork(parent, currentTagName));
    }
   }
   return work;
  }
  return newTreeSet();
 }

 private boolean shouldInclude(RevCommit thisCommit) {
  return this.includedCommits.contains(thisCommit);
 }

 private List<Ref> tagsBetweenFromAndTo(ObjectId from, ObjectId to) throws Exception {
  List<Ref> tagList = this.git.tagList().call();
  List<Ref> includedTags = newArrayList();
  for (Ref tag : tagList) {
   ObjectId peeledTag = getPeeled(tag);
   if (this.includedCommits.contains(peeledTag)) {
    includedTags.add(tag);
   }
  }
  return includedTags;
 }

 private boolean thisIsANewTag(Map<String, Ref> tagsPerCommitHash, String thisCommitHash) {
  return tagsPerCommitHash.containsKey(thisCommitHash);
 }

 private GitCommit toGitCommit(RevCommit revCommit) {
  return new GitCommit( //
    revCommit.getAuthorIdent().getName(),//
    revCommit.getAuthorIdent().getEmailAddress(),//
    new Date(revCommit.getCommitTime() * 1000L),//
    revCommit.getFullMessage(),//
    revCommit.getId().getName());
 }
}
