package se.bjurr.gitchangelog.api.exceptions;

public class GitChangelogRepositoryException extends Exception {

  private static final long serialVersionUID = -1634908400301897570L;

  public GitChangelogRepositoryException(String message) {
    super(message);
  }

  public GitChangelogRepositoryException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
