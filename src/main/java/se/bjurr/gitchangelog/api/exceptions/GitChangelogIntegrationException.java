package se.bjurr.gitchangelog.api.exceptions;

public class GitChangelogIntegrationException extends Exception {

 private static final long serialVersionUID = 4249741847365803709L;

 public GitChangelogIntegrationException(String message, Throwable throwable) {
  super(message, throwable);
 }

 public GitChangelogIntegrationException(String message) {
  super(message);
 }
}
