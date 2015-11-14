package se.bjurr.gitchangelog.api.model.interfaces;

import java.util.List;

import se.bjurr.gitchangelog.api.model.Commit;

public interface ICommits {
 List<Commit> getCommits();
}
