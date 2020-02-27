package se.bjurr.gitchangelog.api.model;

import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.bjurr.gitchangelog.api.model.interfaces.IIssues;

public class SubmoduleSection implements Serializable, IIssues {
  private static final Logger LOG = LoggerFactory.getLogger(SubmoduleSection.class);
  private static final long serialVersionUID = 2140208294219785889L;

  private final String ownerName;
  private final String repoName;
  private final String tagName;
  private final List<Issue> issues;

  public SubmoduleSection(String ownerName, String repoName, String tagName, List<Issue> issues) {
    this.ownerName = ownerName;
    this.repoName = repoName;
    this.tagName = tagName;
    this.issues = issues;
    LOG.info("new SubmoduleSection " + ownerName + " " + repoName + " " + tagName);
  }

  public String getTagName() {
    return this.tagName;
  }

  @Override
  public List<Issue> getIssues() {
    return this.issues;
  }

  @Override
  public String toString() {
    return "name: " + this.repoName;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public String getRepoName() {
    return repoName;
  }
}
