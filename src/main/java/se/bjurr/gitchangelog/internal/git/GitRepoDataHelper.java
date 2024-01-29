package se.bjurr.gitchangelog.internal.git;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;
import se.bjurr.gitchangelog.internal.model.ParsedIssue;

public final class GitRepoDataHelper {
  public static GitRepoData removeCommitsWithoutIssue(
      final List<ParsedIssue> allParsedIssues, final GitRepoData gitRepoData) {
    final Set<GitCommit> commitsWithIssues = new TreeSet<>();
    for (final ParsedIssue parsedIssue : allParsedIssues) {
      for (final GitCommit gitCommit : parsedIssue.getGitCommits()) {
        commitsWithIssues.add(gitCommit);
      }
    }
    final List<GitCommit> reducedGitCommits = new ArrayList<>(commitsWithIssues);

    final List<GitTag> reducedGitTags = new ArrayList<>();
    for (final GitTag gitTag : gitRepoData.getGitTags()) {
      final List<GitCommit> reducedCommitsInTag =
          gitTag.getGitCommits().stream()
              .filter(it -> reducedGitCommits.contains(it))
              .collect(Collectors.toList());
      if (reducedCommitsInTag.iterator().hasNext()) {
        final GitTag item =
            new GitTag(
                gitTag.getName(),
                gitTag.findAnnotation().orElse(null),
                reducedCommitsInTag,
                gitTag.getTagTime());
        reducedGitTags.add(item);
      }
    }

    final String originCloneUrl = gitRepoData.getOriginCloneUrl();
    return new GitRepoData(originCloneUrl, reducedGitTags);
  }

  private GitRepoDataHelper() {}
}
