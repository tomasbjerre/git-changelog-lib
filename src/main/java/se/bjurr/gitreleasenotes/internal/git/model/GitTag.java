package se.bjurr.gitreleasenotes.internal.git.model;

public class GitTag {

 private final String name;
 private final String commit;

 public GitTag(String name, String commit) {
  this.name = name;
  this.commit = commit;
 }

 public String getCommit() {
  return commit;
 }

 public String getName() {
  return name;
 }

}
