package se.bjurr.gitchangelog.internal.git;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.common.base.Optional;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import se.bjurr.gitchangelog.api.GitChangelogApi;
import se.bjurr.gitchangelog.api.exceptions.GitChangelogRepositoryException;
import se.bjurr.gitchangelog.api.model.Changelog;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.settings.Settings;

public class GitSubmoduleParser {
  private static final Logger LOG = getLogger(GitSubmoduleParser.class);

  public GitSubmoduleParser() {}

  public List<Changelog> parseForSubmodules(
      final GitChangelogApi gitChangelogApi,
      final boolean useIntegrationIfConfigured,
      final GitRepo gitRepo,
      final List<GitCommit> commits) throws GitChangelogRepositoryException {

    List<Changelog> submodules = new ArrayList<>();
    Map<String, SubmoduleEntry> submoduleEntries = new TreeMap<>();
    Pattern submoduleNamePattern =
        Pattern.compile(
            "(?m)^\\+{3} b/([\\w/\\s-]+)($\\n@.+)?\\n-Subproject commit (\\w+)$\\n\\+Subproject commit (\\w+)$");

    Settings settings = gitChangelogApi.getSettings();

    Optional<String> cachedFromCommit = settings.getFromCommit();
    Optional<String> cachedToCommit = settings.getToCommit();
    Optional<String> cachedFromRef = settings.getFromRef();
    Optional<String> cachedToRef = settings.getToRef();
    String cachedFromRepo = settings.getFromRepo();

    for (GitCommit commit : commits) {
      String diff = gitRepo.getDiff(commit.getHash());
      Matcher submoduleMatch = submoduleNamePattern.matcher(diff);
      while (submoduleMatch.find()) {
        String submoduleName = submoduleMatch.group(1);
        String previousSubmoduleHash = submoduleMatch.group(3);
        String currentSubmoduleHash = submoduleMatch.group(4);
        GitRepo submodule = gitRepo.getSubmodule(submoduleName);

        if (submodule == null) {
          continue;
        }

        SubmoduleEntry submoduleEntry =
            new SubmoduleEntry(
                submoduleName, previousSubmoduleHash, currentSubmoduleHash, submodule);

        if (!submoduleEntries.containsKey(submoduleName)) {
          submoduleEntries.put(submoduleName, submoduleEntry);
        }

        SubmoduleEntry existingEntry = submoduleEntries.getOrDefault(submoduleName, submoduleEntry);
        existingEntry.previousSubmoduleHash = submoduleEntry.previousSubmoduleHash;
      }
    }

    for (Map.Entry<String, SubmoduleEntry> submoduleEntry : submoduleEntries.entrySet()) {
      settings.setFromCommit(submoduleEntry.getValue().previousSubmoduleHash);
      settings.setToCommit(submoduleEntry.getValue().currentSubmoduleHash);
      settings.setFromRef(null);
      settings.setToRef(null);
      settings.setFromRepo(submoduleEntry.getValue().gitRepo.getDirectory());

      try {
        submodules.add(
            GitChangelogApi.gitChangelogApiBuilder()
                .withSettings(settings)
                .getChangelog(useIntegrationIfConfigured));
      } catch (GitChangelogRepositoryException e) {
        throw new GitChangelogRepositoryException("", e);
      }
    }

    settings.setFromCommit(cachedFromCommit.orNull());
    settings.setToCommit(cachedToCommit.orNull());
    settings.setFromRef(cachedFromRef.orNull());
    settings.setToRef(cachedToRef.orNull());
    settings.setFromRepo(cachedFromRepo);

    return submodules;
  }

  private class SubmoduleEntry {
    public final String name;
    public String previousSubmoduleHash;
    public final String currentSubmoduleHash;
    public final GitRepo gitRepo;

    public SubmoduleEntry(
        final String name,
        final String previousSubmoduleHash,
        final String currentSubmoduleHash,
        final GitRepo gitRepo) {
      this.name = name;
      this.previousSubmoduleHash = previousSubmoduleHash;
      this.currentSubmoduleHash = currentSubmoduleHash;
      this.gitRepo = gitRepo;
    }
  }
}
