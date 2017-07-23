package se.bjurr.gitchangelog.internal.git;

import static com.google.common.base.Predicates.in;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newTreeSet;

import java.util.List;
import java.util.Set;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.git.model.GitTag;
import se.bjurr.gitchangelog.internal.model.ParsedIssue;

public class GitRepoDataHelper {
  public static GitRepoData removeCommitsWithoutIssue(
      List<ParsedIssue> allParsedIssues, GitRepoData gitRepoData) {
    Set<GitCommit> commitsWithIssues = newTreeSet();
    for (ParsedIssue parsedIssue : allParsedIssues) {
      for (GitCommit gitCommit : parsedIssue.getGitCommits()) {
        commitsWithIssues.add(gitCommit);
      }
    }
    List<GitCommit> reducedGitCommits = newArrayList(commitsWithIssues);

    List<GitTag> reducedGitTags = newArrayList();
    for (GitTag gitTag : gitRepoData.getGitTags()) {
      Iterable<GitCommit> reducedCommitsInTag =
          filter(gitTag.getGitCommits(), in(reducedGitCommits));
      if (!isEmpty(reducedCommitsInTag)) {
        reducedGitTags.add(
            new GitTag(
                gitTag.getName(),
                gitTag.findAnnotation().orNull(),
                newArrayList(reducedCommitsInTag),
                gitTag.getTagTime()));
      }
    }

    String originCloneUrl = gitRepoData.getOriginCloneUrl();
    return new GitRepoData(originCloneUrl, reducedGitTags);
  }

  private GitRepoDataHelper() {}
}
