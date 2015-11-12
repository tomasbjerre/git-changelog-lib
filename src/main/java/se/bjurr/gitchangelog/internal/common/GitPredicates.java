package se.bjurr.gitchangelog.internal.common;

import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;
import se.bjurr.gitchangelog.internal.git.model.GitCommit;

import com.google.common.base.Predicate;

public class GitPredicates {

 public static Predicate<GitCommit> ignoreCommits(final String ignoreCommitsIfMessageMatches) {
  return new Predicate<GitCommit>() {
   @Override
   public boolean apply(GitCommit gitCommit) {
    return !compile(ignoreCommitsIfMessageMatches, DOTALL).matcher(gitCommit.getMessage()).matches();
   }
  };
 }

}
