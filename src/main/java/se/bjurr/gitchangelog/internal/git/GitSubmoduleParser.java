package se.bjurr.gitchangelog.internal.git;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.common.base.Optional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

  public HashMap<String, List<Changelog>> parseForSubmodules(
      final GitChangelogApi gitChangelogApi,
      final boolean useIntegrationIfConfigured,
      final GitRepo gitRepo,
      final List<GitCommit> commits) {

    HashMap<String, List<Changelog>> submoduleSections = new HashMap<>();
    Pattern submoduleNamePattern =
        Pattern.compile(
            "(?m)^\\+{3} b/([\\w/\\s-]+)$\\n@.+\\n-Subproject commit (\\w+)$\\n\\+Subproject commit (\\w+)$");

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
        String previousSubmoduleHash = submoduleMatch.group(2);
        String currentSubmoduleHash = submoduleMatch.group(3);
        GitRepo submodule = gitRepo.getSubmodule(submoduleName);
        if (submodule == null) {
          continue;
        }

        settings.setFromCommit(previousSubmoduleHash);
        settings.setToCommit(currentSubmoduleHash);
        settings.setFromRef(null);
        settings.setToRef(null);
        settings.setFromRepo(submodule.getDirectory());

        String commitHash = commit.getHash();
        if (!submoduleSections.containsKey(commitHash)) {
          submoduleSections.put(commitHash, new ArrayList<>());
        }
        List<Changelog> submoduleSectionList = submoduleSections.get(commitHash);
        try {
          submoduleSectionList.add(
              GitChangelogApi.gitChangelogApiBuilder()
                  .withSettings(settings)
                  .getChangelog(useIntegrationIfConfigured));
        } catch (GitChangelogRepositoryException e) {
          e.printStackTrace();
        }
      }
    }

    settings.setFromCommit(cachedFromCommit.orNull());
    settings.setToCommit(cachedToCommit.orNull());
    settings.setFromRef(cachedFromRef.orNull());
    settings.setToRef(cachedToRef.orNull());
    settings.setFromRepo(cachedFromRepo);

    return submoduleSections;
  }
}
