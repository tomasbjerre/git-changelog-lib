package se.bjurr.gitchangelog.internal.common;

import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;

import com.google.common.base.Predicate;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;
import se.bjurr.gitchangelog.internal.settings.Settings;

public class GitPredicates {

  public static Predicate<GitCommit> ignoreCommits(final Settings settings) {
    return gitCommit -> {
      final boolean messageMatches =
          compile(settings.getIgnoreCommitsIfMessageMatches(), DOTALL)
              .matcher(gitCommit.getMessage())
              .matches();
      if (messageMatches) {
        return false;
      }

      if (settings.getIgnoreCommitsIfOlderThan().isPresent()) {
        final boolean olderThan =
            gitCommit.getCommitTime().before(settings.getIgnoreCommitsIfOlderThan().get());
        if (olderThan) {
          return false;
        }
      }
      return true;
    };
  }
}
