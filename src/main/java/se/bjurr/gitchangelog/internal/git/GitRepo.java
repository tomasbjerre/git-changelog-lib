package se.bjurr.gitchangelog.internal.git;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterators.getLast;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newTreeSet;
import static java.util.regex.Pattern.compile;
import static org.eclipse.jgit.lib.Constants.HEAD;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import com.google.common.base.Optional;
import com.google.common.collect.Ordering;
import java.io.*;
import java.util.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.submodule.SubmoduleWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.bjurr.gitchangelog.api.GitChangelogApiConstants;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogRepositoryException;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;

public class GitRepo implements Closeable {
  private static final Logger LOG = LoggerFactory.getLogger(GitRepo.class);
  private List<RevCommit> commitsToInclude;
  private Git git;
  private final Repository repository;
  private final RevWalk revWalk;
  private final HashMap<String, GitRepo> submodules;

  public GitRepo() {
    this.repository = null;
    this.revWalk = null;
    this.submodules = null;
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

      if (SubmoduleWalk.containsGitModulesFile(repository)) {
        this.submodules = new HashMap<>();
        final SubmoduleWalk submoduleWalk = SubmoduleWalk.forIndex(repository);
        while (submoduleWalk.next()) {
          final Repository submoduleRepository = submoduleWalk.getRepository();
          if (submoduleRepository != null) {
            try {
              String submodulePath = submoduleWalk.getModulesPath();
              this.submodules.put(submodulePath, new GitRepo(submoduleRepository.getDirectory()));
            } catch (ConfigInvalidException e) {
              LOG.warn("invalid submodule configuration; skipping submodule\n" + e);
            }
            submoduleRepository.close();
          }
        }
      } else {
        this.submodules = null;
      }

    } catch (final IOException e) {
      throw new GitChangelogRepositoryException(
          "Could not use GIT repo in " + repo.getAbsolutePath(), e);
    }
  }

  @Override
  public void close() throws IOException {
    this.git.close();
    this.repository.close();
    revWalk.dispose();
    if (revWalk instanceof AutoCloseable) {
      try {
        ((AutoCloseable) revWalk).close();
      } catch (final Exception e) {
        LOG.error(e.getMessage(), e);
      }
    }
    if (submodules != null) {
      for (Map.Entry<String, GitRepo> submodule : submodules.entrySet()) {
        submodule.getValue().close();
      }
    }
  }

  public ObjectId getCommit(final String fromCommit) throws GitChangelogRepositoryException {
    if (fromCommit.startsWith(ZERO_COMMIT)) {
      return firstCommit();
    }
    try {
      return this.repository.resolve(fromCommit);
    } catch (final Exception e) {
      throw new GitChangelogRepositoryException("", e);
    }
  }

  /**
   * @param from From, but not including, this commit. Except for the {@link
   *     GitChangelogApiConstants#ZERO_COMMIT}, it is included.
   * @param to To and including this commit.
   */
  public GitRepoData getGitRepoData(
      final ObjectId from,
      final ObjectId to,
      final String untaggedName,
      final Optional<String> ignoreTagsIfNameMatches)
      throws GitChangelogRepositoryException {
    try {
      final String originUrl =
          this.git.getRepository().getConfig().getString("remote", "origin", "url");
      final List<GitTag> gitTags = gitTags(from, to, untaggedName, ignoreTagsIfNameMatches);
      return new GitRepoData(originUrl, gitTags);
    } catch (final Exception e) {
      throw new GitChangelogRepositoryException(toString(), e);
    }
  }

  public ObjectId getRef(final String fromRef) throws GitChangelogRepositoryException {
    try {
      for (final Ref foundRef : getAllRefs().values()) {
        if (foundRef.getName().endsWith(fromRef)) {
          final Ref ref = getAllRefs().get(foundRef.getName());
          final Ref peeledRef = this.repository.peel(ref);
          if (peeledRef.getPeeledObjectId() != null) {
            return peeledRef.getPeeledObjectId();
          } else {
            return ref.getObjectId();
          }
        }
      }
    } catch (final Exception e) {
      throw new GitChangelogRepositoryException(fromRef + " not found in:\n" + toString(), e);
    }
    throw new GitChangelogRepositoryException(fromRef + " not found in:\n" + toString());
  }

  public boolean hasSubmodules() {
    return submodules != null && submodules.size() > 0;
  }

  public GitRepo getSubmodule(String submodulePath) {
    return submodules.getOrDefault(submodulePath, null);
  }

  public String getDiff(String commitHash) {
    RevCommit commit = null;
    RevCommit prevCommit = null;

    try {
      commit = this.revWalk.parseCommit(getCommit(commitHash));
      prevCommit = commit.getParentCount() > 0 ? commit.getParent(0) : null;
    } catch (GitChangelogRepositoryException | IOException e) {
      e.printStackTrace();
    }

    OutputStream outputStream = new ByteArrayOutputStream();
    DiffFormatter diffFormatter = new DiffFormatter(outputStream);
    diffFormatter.setRepository(this.repository);
    diffFormatter.setAbbreviationLength(10);

    try {
      for (DiffEntry entry : diffFormatter.scan(prevCommit, commit)) {
        diffFormatter.format(diffFormatter.toFileHeader(entry));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return outputStream.toString();
  }

  public String getDirectory() {
    return this.repository.getDirectory().getAbsolutePath();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for (final Ref foundRef : getAllRefs().values()) {
      sb.append(foundRef.getName() + "\n");
    }
    return "Repo: " + this.repository + "\n" + sb.toString();
  }

  private CanonicalTreeParser getTreeParser(RevCommit commit) {
    LOG.info("getTreeParser for " + commit.toString());
    RevTree revTree = commit.getTree();
    if (revTree == null) {
      LOG.info("revTree is null");
      return null;
    }
    ObjectId treeId = commit.getTree().getId();
    ObjectReader reader = this.repository.newObjectReader();
    try {
      return new CanonicalTreeParser(null, reader, treeId);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private boolean addCommitToCurrentTag(
      final Map<String, Set<GitCommit>> commitsPerTagName,
      final String currentTagName,
      final RevCommit thisCommit) {
    final GitCommit gitCommit = toGitCommit(thisCommit);
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

      final GitTag gitTag = new GitTag(tagName, annotation, newArrayList(gitCommits), tagTime);
      addTo.add(gitTag);
    }
  }

  private RevCommit firstCommit() {
    Git git = null;
    try {
      git = new Git(this.repository);
      final AnyObjectId master = getRef(HEAD);
      final Iterator<RevCommit> itr = git.log().add(master).call().iterator();
      return getLast(itr);
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
    final Map<String, RevTag> tagPerCommit = newHashMap();
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

  private List<RevCommit> getDiffingCommits(final RevCommit from, final RevCommit to)
      throws Exception {
    final RevCommit firstCommit = firstCommit();
    final List<RevCommit> allInFrom =
        newArrayList(this.git.log().addRange(firstCommit, from).call());
    final List<RevCommit> allInTo = newArrayList(this.git.log().addRange(firstCommit, to).call());
    return newArrayList(filter(allInTo, not(in(allInFrom))));
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
    return Ordering.from(
            new Comparator<Ref>() {
              @Override
              public int compare(final Ref o1, final Ref o2) {
                final RevCommit revCommit1 = GitRepo.this.revWalk.lookupCommit(getPeeled(o1));
                try {
                  GitRepo.this.revWalk.parseHeaders(revCommit1);
                  final RevCommit revCommit2 = GitRepo.this.revWalk.lookupCommit(getPeeled(o2));
                  GitRepo.this.revWalk.parseHeaders(revCommit2);
                  return toGitCommit(revCommit1).compareTo(toGitCommit(revCommit2));
                } catch (final Exception e) {
                  throw propagate(e);
                }
              }
            }) //
        .sortedCopy(refs);
  }

  private String getTagName(final Map<String, Ref> tagPerCommitHash, final String thisCommitHash) {
    return tagPerCommitHash.get(thisCommitHash).getName();
  }

  private Map<String, Ref> getTagPerCommitHash(
      final Optional<String> ignoreTagsIfNameMatches, final List<Ref> tagList) {
    final Map<String, Ref> tagPerCommit = newHashMap();
    for (final Ref tag : tagList) {
      if (ignoreTagsIfNameMatches.isPresent()) {
        if (compile(ignoreTagsIfNameMatches.get()).matcher(tag.getName()).matches()) {
          continue;
        }
      }
      tagPerCommit.put(getPeeled(tag).getName(), tag);
    }
    return tagPerCommit;
  }

  private List<GitTag> gitTags(
      final ObjectId fromObjectId,
      final ObjectId toObjectId,
      final String untaggedName,
      final Optional<String> ignoreTagsIfNameMatches)
      throws Exception {
    final RevCommit from = this.revWalk.lookupCommit(fromObjectId);
    final RevCommit to = this.revWalk.lookupCommit(toObjectId);

    if (from == to) {
      return newArrayList();
    }

    this.commitsToInclude = getDiffingCommits(from, to);

    final List<Ref> tagList = tagsBetweenFromAndTo(from, to);
    /**
     * What: Contains only the commits that are directly referred to by tags.<br>
     * Why: To know if a new tag was found when walking up through the parents.
     */
    final Map<String, Ref> tagPerCommitHash = getTagPerCommitHash(ignoreTagsIfNameMatches, tagList);

    /**
     * What: Contains only the tags that are annotated.<br>
     * Why: To populate tag message, for annotated tags.
     */
    final Map<String, RevTag> annotatedTagPerTagName =
        getAnnotatedTagPerTagName(ignoreTagsIfNameMatches, tagList);

    /**
     * What: Populated with all included commits, referring to there tags.<br>
     * Why: To know if a commit is already mapped to a tag, or not.
     */
    final Map<String, String> tagPerCommitsHash = newHashMap();
    /**
     * What: Commits per tag.<br>
     * Why: Its what we are here for! =)
     */
    final Map<String, Set<GitCommit>> commitsPerTag = newHashMap();
    final Map<String, Date> datePerTag = newHashMap();

    populateComitPerTag(
        from, to, tagPerCommitHash, tagPerCommitsHash, commitsPerTag, datePerTag, null);
    populateComitPerTag(
        from, to, tagPerCommitHash, tagPerCommitsHash, commitsPerTag, datePerTag, untaggedName);

    final List<GitTag> tags = newArrayList();
    addToTags(commitsPerTag, untaggedName, null, tags, annotatedTagPerTagName);
    final List<Ref> tagCommitHashSortedByCommitTime =
        getTagCommitHashSortedByCommitTime(tagPerCommitHash.values());
    for (final Ref tag : tagCommitHashSortedByCommitTime) {
      addToTags(
          commitsPerTag,
          tag.getName(),
          datePerTag.get(tag.getName()),
          tags,
          annotatedTagPerTagName);
    }
    return tags;
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
      final ObjectId to,
      final Map<String, Ref> tagPerCommitHash,
      final Map<String, String> tagPerCommitsHash,
      final Map<String, Set<GitCommit>> commitsPerTag,
      final Map<String, Date> datePerTag,
      final String startingTagName)
      throws Exception {
    final Set<TraversalWork> moreWork =
        populateCommitPerTag(
            from,
            to,
            commitsPerTag,
            tagPerCommitHash,
            tagPerCommitsHash,
            datePerTag,
            startingTagName);
    do {
      final Set<TraversalWork> evenMoreWork = newTreeSet();
      for (final TraversalWork tw : newArrayList(moreWork)) {
        moreWork.remove(tw);
        final Set<TraversalWork> newWork =
            populateCommitPerTag(
                from,
                tw.getTo(),
                commitsPerTag,
                tagPerCommitHash,
                tagPerCommitsHash,
                datePerTag,
                tw.getCurrentTagName());
        evenMoreWork.addAll(newWork);
      }
      moreWork.addAll(evenMoreWork);
      LOG.debug("Work left: " + moreWork.size());
    } while (!moreWork.isEmpty());
  }

  private Set<TraversalWork> populateCommitPerTag(
      final RevCommit from,
      final ObjectId to,
      final Map<String, Set<GitCommit>> commitsPerTagName,
      final Map<String, Ref> tagPerCommitHash,
      final Map<String, String> tagPerCommitsHash,
      final Map<String, Date> datePerTag,
      String currentTagName)
      throws Exception {
    final String thisCommitHash = to.getName();
    if (isMappedToAnotherTag(tagPerCommitsHash, thisCommitHash)) {
      return newTreeSet();
    }
    final RevCommit thisCommit = this.revWalk.lookupCommit(to);
    this.revWalk.parseHeaders(thisCommit);
    if (thisIsANewTag(tagPerCommitHash, thisCommitHash)) {
      currentTagName = getTagName(tagPerCommitHash, thisCommitHash);
    }
    if (currentTagName != null) {
      if (addCommitToCurrentTag(commitsPerTagName, currentTagName, thisCommit)) {
        datePerTag.put(currentTagName, new Date(thisCommit.getCommitTime() * 1000L));
      }
      noteThatTheCommitWasMapped(tagPerCommitsHash, currentTagName, thisCommitHash);
    }
    if (notFirstIncludedCommit(from, to)) {
      final Set<TraversalWork> work = newTreeSet();
      for (final RevCommit parent : thisCommit.getParents()) {
        if (shouldInclude(parent)) {
          work.add(new TraversalWork(parent, currentTagName));
        }
      }
      return work;
    }
    return newTreeSet();
  }

  private boolean shouldInclude(final RevCommit candidate) throws Exception {
    return this.commitsToInclude.contains(candidate);
  }

  private List<Ref> tagsBetweenFromAndTo(final ObjectId from, final ObjectId to) throws Exception {
    final List<Ref> tagList = this.git.tagList().call();
    final List<RevCommit> icludedCommits = newArrayList(this.git.log().addRange(from, to).call());
    final List<Ref> includedTags = newArrayList();
    for (final Ref tag : tagList) {
      final ObjectId peeledTag = getPeeled(tag);
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
}
