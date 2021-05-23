package se.bjurr.gitchangelog.semantic;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import se.bjurr.gitchangelog.api.GitChangelogApi;

public class SemanticVersioningTest {

  private List<String> tags;
  private List<String> commits;
  private String majorPattern;
  private String minorPattern;
  private SemanticVersioning sut;

  @Before
  public void before() {
    this.tags = new ArrayList<String>();
    this.commits = new ArrayList<String>();
    this.majorPattern = "[Bb]reaking:.*";
    this.minorPattern = "[Uu]pdate:.*";
    this.sut =
        new SemanticVersioning(this.tags, this.commits, this.majorPattern, this.minorPattern);
  }

  @Test
  public void smokeTest() throws Throwable {
    final GitChangelogApi builder =
        gitChangelogApiBuilder() //
            .withToCommit("7c1c366") //
            .withSemanticPatterns("breaking:.*", "update:.*");

    assertThat(builder.getNextSemanticVersion().toString()).isEqualTo("1.144.5");
    assertThat(builder.getHighestSemanticVersion().toString()).isEqualTo("1.144.4");
  }

  @Test
  public void testMajorStep() throws Throwable {
    this.tags.add("v1.0.0");
    this.commits.add("breaking: whatever");

    assertThat(this.sut.getHighestVersion() + " -> " + this.sut.getNextVersion()) //
        .isEqualTo("1.0.0 -> 2.0.0");
  }

  @Test
  public void testMinorStep() throws Throwable {
    this.tags.add("v1.0.0");
    this.commits.add("update: whatever");

    assertThat(this.sut.getHighestVersion() + " -> " + this.sut.getNextVersion()) //
        .isEqualTo("1.0.0 -> 1.1.0");
  }

  @Test
  public void testPatchStep() throws Throwable {
    this.tags.add("v1.0.0");
    this.commits.add("chore: whatever");

    assertThat(this.sut.getHighestVersion() + " -> " + this.sut.getNextVersion()) //
        .isEqualTo("1.0.0 -> 1.0.1");
  }
}
