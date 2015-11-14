package se.bjurr.gitchangelog.internal.model.interfaces;

import se.bjurr.gitchangelog.internal.git.model.GitCommit;

public interface IGitCommitReferer {
 GitCommit getGitCommit();

 String getName();
}
