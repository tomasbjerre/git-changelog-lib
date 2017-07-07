package se.bjurr.gitchangelog.internal.common;

import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;

import com.google.common.base.Predicate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;

public class GitPredicates {

  public static Predicate<GitCommit> ignoreCommits(
      final String ignoreCommitsIfMessageMatches,
      final String ignoreCommitsIfOlderThan,
      final String dateFormat) {
    return new Predicate<GitCommit>() {
      private SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);

      @Override
      public boolean apply(GitCommit gitCommit) {
        boolean messageMatches =
            compile(ignoreCommitsIfMessageMatches, DOTALL)
                .matcher(gitCommit.getMessage())
                .matches();

        boolean olderThan;
        try {
          olderThan =
              gitCommit.getCommitTime().before(dateFormatter.parse(ignoreCommitsIfOlderThan));
        } catch (ParseException e) {
          olderThan = false;
        }

        return !messageMatches && !olderThan;
      }
    };
  }
}
