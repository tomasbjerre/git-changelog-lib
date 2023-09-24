package se.bjurr.gitchangelog.semantic;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.internal.semantic.SemanticVersioning.VERSION_STEP.MAJOR;
import static se.bjurr.gitchangelog.internal.semantic.SemanticVersioning.VERSION_STEP.MINOR;
import static se.bjurr.gitchangelog.internal.semantic.SemanticVersioning.VERSION_STEP.NONE;
import static se.bjurr.gitchangelog.internal.semantic.SemanticVersioning.VERSION_STEP.PATCH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import se.bjurr.gitchangelog.api.GitChangelogApi;
import se.bjurr.gitchangelog.internal.semantic.SemanticVersion;
import se.bjurr.gitchangelog.internal.semantic.SemanticVersioning;
import se.bjurr.gitchangelog.internal.semantic.SemanticVersioning.VERSION_STEP;

public class SemanticVersioningTest {

  private List<String> tags;
  private List<String> commits;
  private String majorPattern;
  private String minorPattern;
  private String patchPattern;
  private SemanticVersioning sut;

  @Before
  public void before() {
    this.tags = new ArrayList<>();
    this.commits = new ArrayList<>();
    this.majorPattern = null;
    this.minorPattern = "[Uu]pdate:.*";
    this.patchPattern = null;
    this.sut =
        new SemanticVersioning(
            this.tags, this.commits, this.majorPattern, this.minorPattern, this.patchPattern);
  }

  @Test
  public void smokeTest1() throws Throwable {
    final GitChangelogApi builder1 =
        gitChangelogApiBuilder() //
            .withToRevision("7c1c366") //
            .withSemanticMajorVersionPattern("breaking:.*") //
            .withSemanticMinorVersionPattern("update:.*");

    assertThat(builder1.getNextSemanticVersion().toString()).isEqualTo("1.144.5");
    assertThat(builder1.getNextSemanticVersion().getVersionStep()).isEqualTo(PATCH);

    final GitChangelogApi builder2 =
        gitChangelogApiBuilder() //
            .withToRevision("7c1c366") //
            .withSemanticMajorVersionPattern("breaking:.*") //
            .withSemanticMinorVersionPattern("update:.*");
    assertThat(builder2.getHighestSemanticVersion().toString()).isEqualTo("1.144.4");
    assertThat(builder2.getHighestSemanticVersion().findTag().orElse(null)).isEqualTo("1.144.4");
    assertThat(builder2.getNextSemanticVersion().getVersionStep()).isEqualTo(VERSION_STEP.PATCH);
  }

  @Test
  public void smokeTest2() throws Throwable {
    final SemanticVersion nextSemanticVersion =
        gitChangelogApiBuilder()
            .withToRevision("1.155.0") //
            .withSemanticMajorVersionPattern("^[Bb]reaking.*")
            .withSemanticMinorVersionPattern("^[Ff]eat.*")
            .getNextSemanticVersion();
    assertThat(nextSemanticVersion.toString()).isEqualTo("1.155.1");
    assertThat(nextSemanticVersion.getVersionStep()).isEqualTo(PATCH);
  }

  @Test
  public void nextSemanticVersionShouldNotEffectHighestSemanticVersion() throws Throwable {
    final GitChangelogApi api = gitChangelogApiBuilder().withToRevision("1.155.0");
    assertThat(api.getNextSemanticVersion().toString()).isEqualTo("1.155.1");
    assertThat(api.getNextSemanticVersion().toString()).isEqualTo("1.155.1");
    assertThat(api.getHighestSemanticVersion().toString()).isEqualTo("1.155.0");
    assertThat(api.getHighestSemanticVersion().toString()).isEqualTo("1.155.0");
    assertThat(api.getNextSemanticVersion().toString()).isEqualTo("1.155.1");
    assertThat(api.getNextSemanticVersion().toString()).isEqualTo("1.155.1");
    assertThat(api.getHighestSemanticVersion().toString()).isEqualTo("1.155.0");
    assertThat(api.getHighestSemanticVersion().toString()).isEqualTo("1.155.0");
  }

  @Test
  public void testMajorStep() throws Throwable {
    this.tags.add("v1.0.0");
    this.commits.add("breaking: whatever");

    final SemanticVersion highestVersion = SemanticVersioning.getHighestVersion(this.tags);
    assertThat(highestVersion + " -> " + this.sut.getNextVersion(highestVersion)) //
        .isEqualTo("1.0.0 -> 2.0.0");
    assertThat(this.sut.getNextVersion(highestVersion).getVersionStep()).isEqualTo(MAJOR);
  }

  @Test
  public void testMajorExclamation() throws Throwable {
    this.tags.add("v1.0.0");
    this.commits.add("feat!: whatever");

    final SemanticVersion highestVersion = SemanticVersioning.getHighestVersion(this.tags);
    assertThat(highestVersion + " -> " + this.sut.getNextVersion(highestVersion)) //
        .isEqualTo("1.0.0 -> 2.0.0");
    assertThat(this.sut.getNextVersion(highestVersion).getVersionStep()).isEqualTo(MAJOR);
  }

  @Test
  public void testMajorBreaking() throws Throwable {
    this.tags.add("v1.0.0");
    this.commits.add("feat: whatever\n\nBREAKING CHANGE: this is a text");

    final SemanticVersion highestVersion = SemanticVersioning.getHighestVersion(this.tags);
    final SemanticVersion nextVersion = this.sut.getNextVersion(highestVersion);
    assertThat(highestVersion + " -> " + nextVersion) //
        .isEqualTo("1.0.0 -> 2.0.0");
    assertThat(nextVersion.getVersionStep()).isEqualTo(MAJOR);
  }

  @Test
  public void testMinorStep() throws Throwable {
    this.tags.add("v1.0.0");
    this.commits.add("update: whatever");

    final SemanticVersion highestVersion = SemanticVersioning.getHighestVersion(this.tags);
    assertThat(highestVersion + " -> " + this.sut.getNextVersion(highestVersion)) //
        .isEqualTo("1.0.0 -> 1.1.0");
    assertThat(this.sut.getNextVersion(highestVersion).getVersionStep()).isEqualTo(MINOR);
  }

  @Test
  public void testPatchStep() throws Throwable {
    this.tags.add("v1.0.0");
    this.commits.add("chore: whatever");

    final SemanticVersion highestVersion = SemanticVersioning.getHighestVersion(this.tags);
    assertThat(highestVersion + " -> " + this.sut.getNextVersion(highestVersion)) //
        .isEqualTo("1.0.0 -> 1.0.1");
    assertThat(this.sut.getNextVersion(highestVersion).getVersionStep()).isEqualTo(PATCH);
  }

  @Test
  public void testPatchStepWithNoNewCommitsAndNoPattern() throws Throwable {
    this.tags.add("v1.0.0");

    final SemanticVersion highestVersion = SemanticVersioning.getHighestVersion(this.tags);
    assertThat(highestVersion + " -> " + this.sut.getNextVersion(highestVersion)) //
        .isEqualTo("1.0.0 -> 1.0.1");
    assertThat(this.sut.getNextVersion(highestVersion).getVersionStep()).isEqualTo(PATCH);
  }

  @Test
  public void testPatchStepWithNoNewCommitsAndPattern() throws Throwable {
    this.tags.add("v1.0.0");
    this.patchPattern = "fix:.*";
    this.sut =
        new SemanticVersioning(
            this.tags, this.commits, this.majorPattern, this.minorPattern, this.patchPattern);
    final SemanticVersion highestVersion = SemanticVersioning.getHighestVersion(this.tags);
    assertThat(highestVersion + " -> " + this.sut.getNextVersion(highestVersion)) //
        .isEqualTo("1.0.0 -> 1.0.0");
    assertThat(this.sut.getNextVersion(highestVersion).getVersionStep()).isEqualTo(NONE);
  }

  @Test
  public void testPatchStepWithPatternNotMatching() throws Throwable {
    this.tags.add("v1.0.0");
    this.commits.add("chore: whatever");

    final SemanticVersion highestVersion = SemanticVersioning.getHighestVersion(this.tags);
    this.patchPattern = "fix:.*";
    this.sut =
        new SemanticVersioning(
            this.tags, this.commits, this.majorPattern, this.minorPattern, this.patchPattern);
    assertThat(highestVersion + " -> " + this.sut.getNextVersion(highestVersion)) //
        .isEqualTo("1.0.0 -> 1.0.0");
    assertThat(this.sut.getNextVersion(highestVersion).getVersionStep()).isEqualTo(NONE);
  }

  @Test
  public void testPatchStepWithPatternMatching() throws Throwable {
    this.tags.add("v1.0.0");
    this.commits.add("fix: whatever");

    final SemanticVersion highestVersion = SemanticVersioning.getHighestVersion(this.tags);
    this.patchPattern = "fix:.*";
    this.sut =
        new SemanticVersioning(
            this.tags, this.commits, this.majorPattern, this.minorPattern, this.patchPattern);
    assertThat(highestVersion + " -> " + this.sut.getNextVersion(highestVersion)) //
        .isEqualTo("1.0.0 -> 1.0.1");
    assertThat(this.sut.getNextVersion(highestVersion).getVersionStep()).isEqualTo(PATCH);
  }

  @Test
  public void testLongPatchMatches() throws Throwable {
    this.tags.add("1.2.33333");
    this.tags.add("1.2.3");

    final SemanticVersion highestVersion = SemanticVersioning.getHighestVersion(this.tags);
    assertThat(highestVersion.toString()) //
        .isEqualTo("1.2.33333");
  }

  @Test
  public void testLongPatchMatches_reverse_order() throws Throwable {
    this.tags.add("1.2.3");
    this.tags.add("1.2.33333");

    final SemanticVersion highestVersion = SemanticVersioning.getHighestVersion(this.tags);
    assertThat(highestVersion.toString()) //
        .isEqualTo("1.2.33333");
  }

  @Test
  public void testLongMinorMatches() throws Throwable {
    this.tags.add("1.22222");
    this.tags.add("1.2");

    final SemanticVersion highestVersion = SemanticVersioning.getHighestVersion(this.tags);
    assertThat(highestVersion.toString()) //
        .isEqualTo("1.22222.0");
  }

  @Test
  public void testLongMinorMatches_reverse_order() throws Throwable {
    this.tags.add("1.2");
    this.tags.add("1.22222");

    final SemanticVersion highestVersion = SemanticVersioning.getHighestVersion(this.tags);
    assertThat(highestVersion.toString()) //
        .isEqualTo("1.22222.0");
    assertThat(highestVersion.findTag().orElse("")) //
        .isEqualTo("1.22222");
  }

  @Test
  public void testTagCleanup() throws Throwable {
    assertThat(this.getTagNameFrom("whatever1.2.3.4")).isEqualTo("1.2.3");
    assertThat(this.getTagNameFrom("1.2.3.4whatever")).isEqualTo("1.2.3");
    assertThat(this.getTagNameFrom("1.2.3.4whatever-5.6.7")).isEqualTo("1.2.3");
    assertThat(this.getTagNameFrom("whatever-v1-1.2.3")).isEqualTo("1.2.3");
    assertThat(this.getTagNameFrom("whatever-v1-1.2.3.4")).isEqualTo("1.2.3");
  }

  @Test
  public void testCurrentSemanticVersion_tagged_commit() throws Throwable {
    final SemanticVersion currentSemanticVersion =
        gitChangelogApiBuilder().withToRevision("1.144.4").getCurrentSemanticVersion();
    assertThat(currentSemanticVersion.getVersion()).isEqualTo("1.144.4");
    assertThat(currentSemanticVersion.findTag().orElse(null)).isEqualTo("1.144.4");
    assertThat(this.getSnapshotVersion(currentSemanticVersion)).isEqualTo("1.144.4");
  }

  @Test
  public void testCurrentSemanticVersion_untagged_commit() throws Throwable {
    final SemanticVersion currentSemanticVersion =
        gitChangelogApiBuilder().withToRevision("be739eb").getCurrentSemanticVersion();
    assertThat(currentSemanticVersion.getVersion()).isEqualTo("1.144.5");
    assertThat(currentSemanticVersion.findTag().orElse(null)).isEqualTo(null);
    assertThat(this.getSnapshotVersion(currentSemanticVersion)).isEqualTo("1.144.5-SNAPSHOT");
  }

  private String getSnapshotVersion(final SemanticVersion currentSemanticVersion) {
    if (currentSemanticVersion.findTag().isPresent()) {
      return currentSemanticVersion.getVersion();
    }
    return currentSemanticVersion.getVersion() + "-SNAPSHOT";
  }

  private String getTagNameFrom(final String tagName) {
    return SemanticVersioning.getHighestVersion(Arrays.asList(tagName)).toString();
  }
}
