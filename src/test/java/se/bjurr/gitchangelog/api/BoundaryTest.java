package se.bjurr.gitchangelog.api;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import org.junit.Test;
import se.bjurr.gitchangelog.api.model.Changelog;
import se.bjurr.gitchangelog.api.model.Commit;

/**
 * @author Réda Housni Alaoui
 */
public class BoundaryTest {

  @Test
  public void testThatFirstVersionAndLastVersionCanBeIncluded() throws Exception {
    final Changelog actual =
        gitChangelogApiBuilder() //
            .withFromRevision(ZERO_COMMIT, InclusivenessStrategy.INCLUSIVE) //
            .withToRevision("test", InclusivenessStrategy.INCLUSIVE) //
            .getChangelog();

    assertThat(actual.getCommits().stream().map(Commit::getHash))
        .hasSize(16)
        .containsExactly(
            "8371342ad0d887d",
            "bb2f35bb09454b6",
            "090e7f4b11223c4",
            "c5d37f5e964afd5",
            "87c0d7288896171",
            "20e333f8e108f77",
            "d50a3e332f9fcba",
            "cc0fbbd8bc63955",
            "a9bd03b34b255ff",
            "5607ea23fa8aaf7",
            "3f62b35d1317311",
            "71d845c18924504",
            "071a14f29020758",
            "326d4d01c0a9415",
            "7cd30508cecdc5b",
            "a1aa5ff5b625e63");
  }

  @Test
  public void testThatFirstVersionAndLastVersionCanBeExcluded() throws Exception {
    final Changelog actual =
        gitChangelogApiBuilder() //
            .withFromRevision(ZERO_COMMIT, InclusivenessStrategy.EXCLUSIVE) //
            .withToRevision("test", InclusivenessStrategy.EXCLUSIVE) //
            .getChangelog();

    assertThat(actual.getCommits().stream().map(Commit::getHash))
        .hasSize(14)
        .containsExactly(
            "bb2f35bb09454b6",
            "090e7f4b11223c4",
            "c5d37f5e964afd5",
            "87c0d7288896171",
            "20e333f8e108f77",
            "d50a3e332f9fcba",
            "cc0fbbd8bc63955",
            "a9bd03b34b255ff",
            "5607ea23fa8aaf7",
            "3f62b35d1317311",
            "71d845c18924504",
            "071a14f29020758",
            "326d4d01c0a9415",
            "7cd30508cecdc5b");
  }

  @Test
  public void testThatFirstVersionCanBeIncludedAndLastVersionCanBeExcluded() throws Exception {
    final Changelog actual =
        gitChangelogApiBuilder() //
            .withFromRevision(ZERO_COMMIT, InclusivenessStrategy.INCLUSIVE) //
            .withToRevision("test", InclusivenessStrategy.EXCLUSIVE) //
            .getChangelog();

    assertThat(actual.getCommits().stream().map(Commit::getHash))
        .hasSize(15)
        .containsExactly(
            "bb2f35bb09454b6",
            "090e7f4b11223c4",
            "c5d37f5e964afd5",
            "87c0d7288896171",
            "20e333f8e108f77",
            "d50a3e332f9fcba",
            "cc0fbbd8bc63955",
            "a9bd03b34b255ff",
            "5607ea23fa8aaf7",
            "3f62b35d1317311",
            "71d845c18924504",
            "071a14f29020758",
            "326d4d01c0a9415",
            "7cd30508cecdc5b",
            "a1aa5ff5b625e63");
  }

  @Test
  public void testThatFirstVersionCanBeExcludedAndLastVersionCanBeIncluded() throws Exception {
    final Changelog actual =
        gitChangelogApiBuilder() //
            .withFromRevision(ZERO_COMMIT, InclusivenessStrategy.EXCLUSIVE) //
            .withToRevision("test", InclusivenessStrategy.INCLUSIVE) //
            .getChangelog();

    assertThat(actual.getCommits().stream().map(Commit::getHash))
        .hasSize(15)
        .containsExactly(
            "8371342ad0d887d",
            "bb2f35bb09454b6",
            "090e7f4b11223c4",
            "c5d37f5e964afd5",
            "87c0d7288896171",
            "20e333f8e108f77",
            "d50a3e332f9fcba",
            "cc0fbbd8bc63955",
            "a9bd03b34b255ff",
            "5607ea23fa8aaf7",
            "3f62b35d1317311",
            "71d845c18924504",
            "071a14f29020758",
            "326d4d01c0a9415",
            "7cd30508cecdc5b");
  }
}
