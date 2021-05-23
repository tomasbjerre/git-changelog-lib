package se.bjurr.gitchangelog.semantic;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemanticVersioning {

  public enum VERSION_STEP {
    MAJOR,
    MINOR,
    PATCH
  }

  private final List<String> tags;
  private final List<String> commits;
  private final Pattern majorPattern;
  private final Pattern minorPattern;

  public SemanticVersioning(
      final List<String> tags,
      final List<String> commits,
      final String majorPattern,
      final String minorPattern) {
    this.tags = tags;
    this.commits = commits;
    this.majorPattern = Pattern.compile(this.notNull(majorPattern, "majorPattern"));
    this.minorPattern = Pattern.compile(this.notNull(minorPattern, "minorPattern"));
  }

  private String notNull(final String value, final String msg) {
    if (value == null) {
      throw new RuntimeException(msg + " is not defined");
    }
    return value;
  }

  public SemanticVersion getNextVersion() {
    final SemanticVersion highestVersion = this.getHighestVersion();
    final VERSION_STEP versionStep = this.getVersionStep();
    if (versionStep == VERSION_STEP.MAJOR) {
      return new SemanticVersion(highestVersion.getMajor() + 1, 0, 0);
    } else if (versionStep == VERSION_STEP.MINOR) {
      return new SemanticVersion(highestVersion.getMajor(), highestVersion.getMinor() + 1, 0);
    }
    return new SemanticVersion(
        highestVersion.getMajor(), highestVersion.getMinor(), highestVersion.getPatch() + 1);
  }

  public SemanticVersion getHighestVersion() {
    SemanticVersion highest = new SemanticVersion(0, 0, 0);
    for (final String tag : this.tags) {
      final Matcher semanticVersionMatcher =
          Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+").matcher(tag.replaceAll("[^0-9\\.]", ""));
      if (semanticVersionMatcher.find()) {
        final String[] dotParts = semanticVersionMatcher.group().split("\\.");
        final SemanticVersion candidate =
            new SemanticVersion(
                new Integer(dotParts[0]), //
                new Integer(dotParts[1]), //
                new Integer(dotParts[2]));
        candidate.setTag(tag);
        if (candidate.getMajor() > highest.getMajor()) {
          highest = candidate;
          continue;
        }
        if (candidate.getMajor() == highest.getMajor()
            && candidate.getMinor() > highest.getMinor()) {
          highest = candidate;
          continue;
        }
        if (candidate.getMajor() == highest.getMajor()
            && candidate.getMinor() == highest.getMinor()
            && candidate.getPatch() > highest.getPatch()) {
          highest = candidate;
          continue;
        }
      }
    }
    return highest;
  }

  private VERSION_STEP getVersionStep() {
    VERSION_STEP versionStep = VERSION_STEP.PATCH;
    for (final String commit : this.commits) {
      if (this.majorPattern.matcher(commit).find()) {
        return VERSION_STEP.MAJOR;
      } else if (this.minorPattern.matcher(commit).find()) {
        versionStep = VERSION_STEP.MINOR;
      }
    }
    return versionStep;
  }
}
