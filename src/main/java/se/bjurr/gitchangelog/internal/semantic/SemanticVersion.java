package se.bjurr.gitchangelog.internal.semantic;

import java.io.Serializable;
import java.util.Optional;

public class SemanticVersion implements Serializable {
  private static final long serialVersionUID = -7875655375353325189L;
  private final int patch;
  private final int minor;
  private final int major;
  private String tag;

  public SemanticVersion(final int major, final int minor, final int patch) {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
  }

  public int getMajor() {
    return this.major;
  }

  public int getMinor() {
    return this.minor;
  }

  public int getPatch() {
    return this.patch;
  }

  @Override
  public String toString() {
    return this.major + "." + this.minor + "." + this.patch;
  }

  public void setTag(final String tag) {
    this.tag = tag;
  }

  /** Present if this is an existing tag in the repo. */
  public Optional<String> findTag() {
    return Optional.ofNullable(this.tag);
  }
}
