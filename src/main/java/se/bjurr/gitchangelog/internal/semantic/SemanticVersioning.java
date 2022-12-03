package se.bjurr.gitchangelog.internal.semantic;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemanticVersioning {

  public enum VERSION_STEP {
    MAJOR,
    MINOR,
    PATCH,
    /** Was not stepped. Given patch pattern did not match. */
    NONE
  }

  private final List<String> commits;
  private final Pattern majorPattern;
  private final Pattern minorPattern;
  private final Pattern patchPattern;

  public SemanticVersioning(
      final List<String> tags,
      final List<String> commits,
      final String majorPattern,
      final String minorPattern,
      final String patchPattern) {
    this.commits = commits;
    if (majorPattern != null) {
      this.majorPattern = Pattern.compile(majorPattern);
    } else {
      this.majorPattern = null;
    }
    this.minorPattern = Pattern.compile(minorPattern);
    if (patchPattern != null) {
      this.patchPattern = Pattern.compile(patchPattern);
    } else {
      this.patchPattern = null;
    }
  }

  public SemanticVersion getNextVersion(final SemanticVersion highestVersion) {
    final VERSION_STEP versionStep = this.getVersionStep();
    if (versionStep == VERSION_STEP.MAJOR) {
      return new SemanticVersion(highestVersion.getMajor() + 1, 0, 0, versionStep);
    } else if (versionStep == VERSION_STEP.MINOR) {
      return new SemanticVersion(
          highestVersion.getMajor(), highestVersion.getMinor() + 1, 0, versionStep);
    } else if (versionStep == VERSION_STEP.PATCH) {
      return new SemanticVersion(
          highestVersion.getMajor(),
          highestVersion.getMinor(),
          highestVersion.getPatch() + 1,
          versionStep);
    }
    return new SemanticVersion(
        highestVersion.getMajor(),
        highestVersion.getMinor(),
        highestVersion.getPatch(),
        versionStep);
  }

  public static SemanticVersion getHighestVersion(final List<String> tags) {
    SemanticVersion highest = new SemanticVersion(0, 0, 0);
    for (final String tag : tags) {
      final Optional<SemanticVersion> candidateOpt = findSemanticVersion(tag);
      if (candidateOpt.isPresent()) {
        final SemanticVersion candidate = candidateOpt.get();
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

  public static Optional<SemanticVersion> findSemanticVersion(final String tag) {
    final Matcher semanticVersionMatcher =
        Pattern.compile("[0-9]+\\.[0-9]+\\.?[0-9]+?").matcher(tag);
    if (!semanticVersionMatcher.find()) {
      return Optional.empty();
    }
    final String[] dotParts = semanticVersionMatcher.group().split("\\.");
    Integer patch = 0;
    if (dotParts.length > 2) {
      patch = new Integer(dotParts[2]);
    }
    final SemanticVersion candidate =
        new SemanticVersion(
            new Integer(dotParts[0]), //
            new Integer(dotParts[1]), //
            patch);
    candidate.setTag(tag);
    return Optional.of(candidate);
  }

  public static boolean isSemantic(final String tag) {
    final Optional<SemanticVersion> foundSemanticOpt = SemanticVersioning.findSemanticVersion(tag);
    return foundSemanticOpt.isPresent();
  }

  private VERSION_STEP getVersionStep() {
    final boolean patchVersionPatternGiven = this.patchPattern != null;
    VERSION_STEP versionStep = patchVersionPatternGiven ? VERSION_STEP.NONE : VERSION_STEP.PATCH;
    for (final String commit : this.commits) {
      final boolean majorPatternMatches =
          this.majorPattern != null && this.majorPattern.matcher(commit).find();
      if (majorPatternMatches || ConventionalCommitParser.commitBreaking(commit)) {
        return VERSION_STEP.MAJOR;
      } else if (this.minorPattern.matcher(commit).find()) {
        versionStep = VERSION_STEP.MINOR;
      } else {
        if (versionStep == VERSION_STEP.NONE
            && patchVersionPatternGiven
            && this.patchPattern.matcher(commit).find()) {
          versionStep = VERSION_STEP.PATCH;
        }
      }
    }
    return versionStep;
  }
}
