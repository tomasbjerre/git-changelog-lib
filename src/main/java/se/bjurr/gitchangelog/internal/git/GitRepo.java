package se.bjurr.gitchangelog.internal.git;

import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;
import static org.eclipse.jgit.lib.Constants.HEAD;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.bjurr.gitchangelog.api.GitChangelogApiConstants;
import se.bjurr.gitchangelog.api.InclusivenessStrategy;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogRepositoryException;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;
import se.bjurr.gitchangelog.internal.semantic.SemanticVersioning;

@SuppressFBWarnings({
  "CRLF_INJECTION_LOGS",
  "BC_VACUOUS_INSTANCEOF",
  "BC_UNCONFIRMED_CAST_OF_RETURN_VALUE",
  "PATH_TRAVERSAL_IN"
})
public class GitRepo implements Closeable {
  private static final Logger LOG = LoggerFactory.getLogger(GitRepo.class);
  private List<RevCommit> commitsToInclude;
  private Git git;
  private final Repository repository;
  private final RevWalk revWalk;
  private String pathFilter = "";

  public GitRepo() {
    this.repository = null;
    this.revWalk = null;
  }

  public GitRepo(final File repo) throws GitChangelogRepositoryException {
    try {
      File repoFile = new File(repo.getAbsolutePath());
      final File gitRepoFile = new File(repo.getAbsolutePath() + "/.git");
      if (gitRepoFile.exists()) {
        repoFile = gitRepoFile;
      }
      final FileRepositoryBuilder builder =
          new FileRepositoryBuilder() //
              .findGitDir(repoFile) //
              .readEnvironment();
      if (builder.getGitDir() == null) {
        throw new GitChangelogRepositoryException(
            "Did not find a GIT repo in " + repo.getAbsolutePath());
      }
      this.repository = builder.build();
      this.revWalk = new RevWalk(this.repository);
      this.git = new Git(this.repository);
    } catch (final IOException e) {
      throw new GitChangelogRepositoryException(
          "Could not use GIT repo in " + repo.getAbsolutePath(), e);
    }
  }

  @Override
  public void close() throws IOException {
    this.git.close();
    this.repository.close();
    this.revWalk.dispose();
    if (this.revWalk instanceof AutoCloseable) {
      try {
        ((AutoCloseable) this.revWalk).close();
      } catch (final Exception e) {
        LOG.error(e.getMessage(), e);
      }
    }
  }

  public ObjectId getCommit(final String fromCommit) throws GitChangelogRepositoryException {
    if (fromCommit.startsWith(ZERO_COMMIT)) {
      return this.firstCommit();
    }
    try {
      return this.repository.resolve(fromCommit);
    } catch (final Exception e) {
      throw new GitChangelogRepositoryException("", e);
    }
  }

  public Optional<RevisionBoundary<ObjectId>> findObjectId(
      final String revision, final InclusivenessStrategy inclusivenessStrategy)
      throws GitChangelogRepositoryException {
    Optional<ObjectId> objectId = this.findRef(revision);
    if (!objectId.isPresent()) {
      objectId = Optional.ofNullable(this.getCommit(revision));
    }
    return objectId.map(
        id -> {
          return new RevisionBoundary<ObjectId>(id, inclusivenessStrategy);
        });
  }

  /**
   * @param from From, but not including, this commit. Except for the {@link
   *     GitChangelogApiConstants#ZERO_COMMIT}, it is included.
   * @param to To and including this commit.
   */
  public GitRepoData getGitRepoData(
      final RevisionBoundary<ObjectId> from,
      final RevisionBoundary<ObjectId> to,
      final String untaggedName,
      final Optional<String> ignoreTagsIfNameMatches)
      throws GitChangelogRepositoryException {
    try {
      final String originUrl =
          this.git.getRepository().getConfig().getString("remote", "origin", "url");
      final List<GitTag> gitTags = this.gitTags(from, to, untaggedName, ignoreTagsIfNameMatches);
      return new GitRepoData(originUrl, gitTags);
    } catch (final Exception e) {
      throw new GitChangelogRepositoryException(this.toString(), e);
    }
  }

  public ObjectId getRef(final String ref) throws GitChangelogRepositoryException {
    try {
      final Optional<ObjectId> foundOpt = this.findRef(ref, true);
      if (foundOpt.isPresent()) {
        return foundOpt.get();
      }
      return this.findRef(ref, false).get();
    } catch (final Exception e) {
      throw new GitChangelogRepositoryException(ref + " not found in:\n" + this.toString(), e);
    }
  }

  public Optional<ObjectId> findRef(final String findRef, final boolean exact) {
    for (final Ref foundRef : this.getAllRefs().values()) {
      final boolean match = this.isMatching(findRef, exact, foundRef);
      if (match) {
        final Ref ref = this.getAllRefs().get(foundRef.getName());
        final Ref peeledRef = this.repository.peel(ref);
        if (peeledRef.getPeeledObjectId() != null) {
          return Optional.of(peeledRef.getPeeledObjectId());
        } else {
          return Optional.of(ref.getObjectId());
        }
      }
    }
    return Optional.empty();
  }

  private boolean isMatching(final String findRef, final boolean exact, final Ref candidate) {
    final String candidateName = candidate.getName();
    if (exact) {
      return candidateName.equalsIgnoreCase(findRef) //
          || candidateName.equalsIgnoreCase("refs/tags/" + findRef) //
          || candidateName.equalsIgnoreCase("refs/heads/" + findRef);
    }
    return candidateName.endsWith(findRef);
  }

  public Optional<ObjectId> findRef(final String ref) {
    try {
      return Optional.of(this.getRef(ref));
    } catch (final Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for (final Ref foundRef : this.getAllRefs().values()) {
      sb.append(foundRef.getName() + "\n");
    }
    return "Repo: " + this.repository + "\n" + sb.toString();
  }

  private boolean addCommitToCurrentTag(
      final Map<String, Set<GitCommit>> commitsPerTagName,
      final String currentTagName,
      final RevCommit thisCommit) {
    final GitCommit gitCommit = this.toGitCommit(thisCommit);
    boolean newTagFound = false;
    if (!commitsPerTagName.containsKey(currentTagName)) {
      commitsPerTagName.put(currentTagName, new TreeSet<GitCommit>());
      newTagFound = true;
    }
    final Set<GitCommit> gitCommitsInCurrentTag = commitsPerTagName.get(currentTagName);
    gitCommitsInCurrentTag.add(gitCommit);
    return newTagFound;
  }

  private void addToTags(
      final Map<String, Set<GitCommit>> commitsPerTag,
      final String tagName,
      final Date tagTime,
      final List<GitTag> addTo,
      final Map<String, RevTag> annotatedTagPerTagName) {
    if (commitsPerTag.containsKey(tagName)) {
      final Set<GitCommit> gitCommits = commitsPerTag.get(tagName);
      final boolean isAnnotated = annotatedTagPerTagName.containsKey(tagName);
      String annotation = null;
      if (isAnnotated) {
        annotation = annotatedTagPerTagName.get(tagName).getFullMessage();
      }

      final GitTag gitTag = new GitTag(tagName, annotation, new ArrayList<>(gitCommits), tagTime);
      addTo.add(gitTag);
    }
  }

  private RevCommit firstCommit() {
    Git git = null;
    try {
      git = new Git(this.repository);
      final AnyObjectId master = this.getRef(HEAD);
      final Iterator<RevCommit> itr = git.log().add(master).call().iterator();
      RevCommit last = null;
      while (itr.hasNext()) {
        last = itr.next();
      }
      return last;
    } catch (final Exception e) {
      throw new RuntimeException("First commit not found in " + this.repository.getDirectory(), e);
    } finally {
      if (git != null) {
        git.close();
      }
    }
  }

  private Map<String, Ref> getAllRefs() {
    return this.repository.getAllRefs();
  }

  private Map<String, RevTag> getAnnotatedTagPerTagName(
      final Optional<String> ignoreTagsIfNameMatches, final List<Ref> tagList) {
    final Map<String, RevTag> tagPerCommit = new TreeMap<>();
    for (final Ref tag : tagList) {
      if (ignoreTagsIfNameMatches.isPresent()) {
        if (compile(ignoreTagsIfNameMatches.get()).matcher(tag.getName()).matches()) {
          continue;
        }
      }
      final Ref peeledTag = this.repository.peel(tag);
      if (peeledTag.getPeeledObjectId() != null) {
        try {
          final RevTag revTag = RevTag.parse(this.repository.open(tag.getObjectId()).getBytes());
          tagPerCommit.put(tag.getName(), revTag);
        } catch (final IOException e) {
          LOG.error(e.getMessage(), e);
        }
      }
    }
    return tagPerCommit;
  }

  private List<RevCommit> getCommitList(
      final RevWalk revWalk,
      final RevisionBoundary<RevCommit> fromBoundary,
      final RevisionBoundary<RevCommit> toBoundary,
      final String pathFilterParam)
      throws Exception {
    final RevCommit from = fromBoundary.getRevision();
    final RevCommit to = toBoundary.getRevision();
    final LogCommand logCommand = this.git.log().addRange(from, to);
    if (pathFilterParam != null && !pathFilterParam.isEmpty()) {
      logCommand.addPath(pathFilterParam);
    }
    final List<RevCommit> list = new ArrayList<>();

    for (final RevCommit commit : logCommand.call()) {
      list.add(commit);
    }

    if (fromBoundary.getInclusivenessStrategy() == InclusivenessStrategy.DEFAULT) {
      revWalk.parseHeaders(from);
      if (from.getParentCount() == 0) {
        list.add(from);
      }
    }
    if (fromBoundary.getInclusivenessStrategy() == InclusivenessStrategy.INCLUSIVE) {
      list.add(from);
    }
    if (toBoundary.getInclusivenessStrategy() == InclusivenessStrategy.EXCLUSIVE) {
      list.remove(to);
    }

    return list;
  }

  private boolean hasPathFilter() {
    return this.pathFilter != null && !this.pathFilter.isEmpty();
  }

  private ObjectId getPeeled(final Ref tag) {
    final Ref peeledTag = this.repository.peel(tag);
    if (peeledTag.getPeeledObjectId() != null) {
      return peeledTag.getPeeledObjectId();
    } else {
      return tag.getObjectId();
    }
  }

  private List<Ref> getTagCommitHashSortedByCommitTime(final Collection<Ref> refs) {
    return refs.stream()
        .sorted(
            (final Ref o1, final Ref o2) -> {
              final RevCommit revCommit1 =
                  GitRepo.this.revWalk.lookupCommit(GitRepo.this.getPeeled(o1));
              try {
                GitRepo.this.revWalk.parseHeaders(revCommit1);
                final RevCommit revCommit2 =
                    GitRepo.this.revWalk.lookupCommit(GitRepo.this.getPeeled(o2));
                GitRepo.this.revWalk.parseHeaders(revCommit2);
                return GitRepo.this
                    .toGitCommit(revCommit1)
                    .compareTo(GitRepo.this.toGitCommit(revCommit2));
              } catch (final Exception e) {
                throw new RuntimeException(e);
              }
            })
        .collect(toList());
  }

  private String getTagName(final Map<String, Ref> tagPerCommitHash, final String thisCommitHash) {
    return tagPerCommitHash.get(thisCommitHash).getName();
  }

  private Map<String, Ref> getTagPerCommitHash(
      final Optional<String> ignoreTagsIfNameMatches, final List<Ref> tagList) {
    final Map<String, Ref> tagPerCommit = new TreeMap<>();
    for (final Ref tag : tagList) {
      if (ignoreTagsIfNameMatches.isPresent()) {
        if (compile(ignoreTagsIfNameMatches.get()).matcher(tag.getName()).matches()) {
          continue;
        }
      }
      final String commitId = this.getPeeled(tag).getName();
      if (tagPerCommit.containsKey(commitId)) {
        /**
         * Only overwrite tag, if the new tag is semantic version. Let semantic tags have priority.
         */
        final boolean newFoundTagSemantic = SemanticVersioning.isSemantic(tag.getName());
        final boolean existingTagNotSemantic =
            !SemanticVersioning.isSemantic(tagPerCommit.get(commitId).getName());
        if (newFoundTagSemantic || existingTagNotSemantic) {
          tagPerCommit.put(commitId, tag);
        }
      } else {
        tagPerCommit.put(commitId, tag);
      }
    }
    return tagPerCommit;
  }

  private List<GitTag> gitTags(
      final RevisionBoundary<ObjectId> fromObjectId,
      final RevisionBoundary<ObjectId> toObjectId,
      final String untaggedName,
      final Optional<String> ignoreTagsIfNameMatches)
      throws Exception {
    final RevisionBoundary<RevCommit> from =
        new RevisionBoundary<RevCommit>(
            this.revWalk.lookupCommit(fromObjectId.getRevision()),
            fromObjectId.getInclusivenessStrategy());
    final RevisionBoundary<RevCommit> to =
        new RevisionBoundary<RevCommit>(
            this.revWalk.lookupCommit(toObjectId.getRevision()),
            toObjectId.getInclusivenessStrategy());

    this.commitsToInclude = this.getCommitList(this.revWalk, from, to, this.pathFilter);

    final List<Ref> tagList = this.tagsBetweenFromAndTo(from, to);
    /**
     * What: Contains only the commits that are directly referred to by tags.<br>
     * Why: To know if a new tag was found when walking up through the parents.
     */
    final Map<String, Ref> tagPerCommitHash =
        this.getTagPerCommitHash(ignoreTagsIfNameMatches, tagList);

    /**
     * What: Contains only the tags that are annotated.<br>
     * Why: To populate tag message, for annotated tags.
     */
    final Map<String, RevTag> annotatedTagPerTagName =
        this.getAnnotatedTagPerTagName(ignoreTagsIfNameMatches, tagList);

    /**
     * What: Populated with all included commits, referring to there tags.<br>
     * Why: To know if a commit is already mapped to a tag, or not.
     */
    final Map<String, String> tagPerCommitsHash = new TreeMap<>();
    /**
     * What: Commits per tag.<br>
     * Why: Its what we are here for! =)
     */
    final Map<String, Set<GitCommit>> commitsPerTag = new HashMap<>();
    final Map<String, Date> datePerTag = new TreeMap<>();

    this.populateComitPerTag(
        from.getRevision(),
        to.getRevision(),
        tagPerCommitHash,
        tagPerCommitsHash,
        commitsPerTag,
        datePerTag,
        null);
    this.populateComitPerTag(
        from.getRevision(),
        to.getRevision(),
        tagPerCommitHash,
        tagPerCommitsHash,
        commitsPerTag,
        datePerTag,
        untaggedName);

    if (this.hasPathFilter()) {
      this.pruneCommitsPerTag(commitsPerTag);
    }

    final List<GitTag> tags = new ArrayList<>();
    this.addToTags(commitsPerTag, untaggedName, null, tags, annotatedTagPerTagName);
    final List<Ref> tagCommitHashSortedByCommitTime =
        this.getTagCommitHashSortedByCommitTime(tagPerCommitHash.values());
    for (final Ref tag : tagCommitHashSortedByCommitTime) {
      this.addToTags(
          commitsPerTag,
          tag.getName(),
          datePerTag.get(tag.getName()),
          tags,
          annotatedTagPerTagName);
    }
    return tags;
  }

  /**
   * prunes commits that have been added in addition to the desired ones
   *
   * @param commitsPerTag
   */
  private void pruneCommitsPerTag(final Map<String, Set<GitCommit>> commitsPerTag) {
    final Set<String> toIncludeSet =
        this.commitsToInclude.stream().map(AnyObjectId::name).collect(Collectors.toSet());
    final List<String> tagsToRemove = new ArrayList<>();

    commitsPerTag.forEach(
        (tag, commits) -> {
          final List<GitCommit> removeList =
              commits.stream()
                  .filter(c -> !toIncludeSet.contains(c.getHash()))
                  .collect(Collectors.toList());
          commits.removeAll(removeList);
          if (commits.size() == 0) {
            tagsToRemove.add(tag);
          }
        });
    tagsToRemove.forEach(commitsPerTag::remove);
  }

  private boolean isMappedToAnotherTag(
      final Map<String, String> tagPerCommitsHash, final String thisCommitHash) {
    return tagPerCommitsHash.containsKey(thisCommitHash);
  }

  private String noteThatTheCommitWasMapped(
      final Map<String, String> tagPerCommitsHash,
      final String currentTagName,
      final String thisCommitHash) {
    return tagPerCommitsHash.put(thisCommitHash, currentTagName);
  }

  private boolean notFirstIncludedCommit(final ObjectId from, final ObjectId to) {
    return !from.getName().equals(to.getName());
  }

  /** This can be done recursively but will result in {@link StackOverflowError} for large repos. */
  private void populateComitPerTag(
      final RevCommit from,
      final RevCommit to,
      final Map<String, Ref> tagPerCommitHash,
      final Map<String, String> tagPerCommitsHash,
      final Map<String, Set<GitCommit>> commitsPerTag,
      final Map<String, Date> datePerTag,
      final String startingTagName)
      throws Exception {

    final RevCommit thisCommit = this.revWalk.lookupCommit(to);
    this.revWalk.parseHeaders(thisCommit);

    final PriorityQueue<TraversalWork> moreWork = new PriorityQueue<>();
    moreWork.add(new TraversalWork(to, startingTagName));
    do {
      final TraversalWork next = moreWork.remove();
      this.populateCommitPerTag(
          from,
          next.getTo(),
          commitsPerTag,
          tagPerCommitHash,
          tagPerCommitsHash,
          datePerTag,
          next.getCurrentTagName(),
          moreWork);
      LOG.debug("Work left: " + moreWork.size());
    } while (!moreWork.isEmpty());
  }

  private void populateCommitPerTag(
      final RevCommit from,
      final RevCommit to,
      final Map<String, Set<GitCommit>> commitsPerTagName,
      final Map<String, Ref> tagPerCommitHash,
      final Map<String, String> tagPerCommitsHash,
      final Map<String, Date> datePerTag,
      String currentTagName,
      final PriorityQueue<TraversalWork> moreWork)
      throws Exception {
    final String thisCommitHash = to.getName();
    if (this.isMappedToAnotherTag(tagPerCommitsHash, thisCommitHash)) {
      return;
    }
    if (this.thisIsANewTag(tagPerCommitHash, thisCommitHash)) {
      currentTagName = this.getTagName(tagPerCommitHash, thisCommitHash);
    }
    if (currentTagName != null && this.shouldInclude(to)) {
      if (this.addCommitToCurrentTag(commitsPerTagName, currentTagName, to)) {
        datePerTag.put(currentTagName, new Date(to.getCommitTime() * 1000L));
      }
      this.noteThatTheCommitWasMapped(tagPerCommitsHash, currentTagName, thisCommitHash);
    }
    if (this.notFirstIncludedCommit(from, to)) {
      for (final RevCommit parent : to.getParents()) {
        if (this.shouldInclude(parent)) {
          this.revWalk.parseHeaders(parent);
          final TraversalWork work = new TraversalWork(parent, currentTagName);
          if (moreWork.contains(work)) {
            LOG.debug("Removing " + work.getTo().getName());
            moreWork.remove(work); // Remove work added from reference by a newer commit
          }
          LOG.debug("Adding " + work.getTo().getName() + " tag: " + work.getCurrentTagName());
          moreWork.add(work); // Add work from this older reference
        }
      }
    }
    return;
  }

  private boolean shouldInclude(final RevCommit candidate) throws Exception {
    // If we use a path filter we can't skip parent commits, as their grandparents might be included
    // again
    return this.hasPathFilter() || this.commitsToInclude.contains(candidate);
  }

  private List<Ref> tagsBetweenFromAndTo(
      final RevisionBoundary<RevCommit> from, final RevisionBoundary<RevCommit> to)
      throws Exception {
    final List<Ref> tagList = this.git.tagList().call();
    final List<RevCommit> icludedCommits = this.getCommitList(this.revWalk, from, to, null);

    final List<Ref> includedTags = new ArrayList<>();
    for (final Ref tag : tagList) {
      final ObjectId peeledTag = this.getPeeled(tag);
      if (icludedCommits.contains(peeledTag)) {
        includedTags.add(tag);
      }
    }
    return includedTags;
  }

  private boolean thisIsANewTag(
      final Map<String, Ref> tagsPerCommitHash, final String thisCommitHash) {
    return tagsPerCommitHash.containsKey(thisCommitHash);
  }

  private GitCommit toGitCommit(final RevCommit revCommit) {
    final Boolean merge = revCommit.getParentCount() > 1;
    return new GitCommit( //
        revCommit.getAuthorIdent().getName(), //
        revCommit.getAuthorIdent().getEmailAddress(), //
        new Date(revCommit.getCommitTime() * 1000L), //
        revCommit.getFullMessage(), //
        revCommit.getId().getName(), //
        merge);
  }

  /**
   * @param pathFilter use when filtering commits
   */
  public void setTreeFilter(final String pathFilter) {
    this.pathFilter = pathFilter == null ? "" : pathFilter;
  }
}
