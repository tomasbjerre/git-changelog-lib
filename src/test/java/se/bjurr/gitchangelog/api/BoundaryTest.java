package se.bjurr.gitchangelog.api;

import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.ZERO_COMMIT;

import org.junit.Test;
import se.bjurr.gitchangelog.internal.settings.RevisionBoundary;
import se.bjurr.gitchangelog.test.ApprovalsWrapper;

/**
 * @author Réda Housni Alaoui
 */
public class BoundaryTest {

	@Test
	public void testThatFirstVersionAndLastVersionCanBeIncluded() throws Exception {
		final GitChangelogApi given =
				gitChangelogApiBuilder() //
						.withFromRevision(new RevisionBoundary(ZERO_COMMIT, InclusivenessStrategy.INCLUSIVE)) //
						.withToRevision(new RevisionBoundary("test", InclusivenessStrategy.INCLUSIVE));

		ApprovalsWrapper.verify(given);
	}

	@Test
	public void testThatFirstVersionAndLastVersionCanBeExcluded() throws Exception {
		final GitChangelogApi given =
				gitChangelogApiBuilder() //
						.withFromRevision(new RevisionBoundary(ZERO_COMMIT, InclusivenessStrategy.EXCLUSIVE)) //
						.withToRevision(new RevisionBoundary("test", InclusivenessStrategy.EXCLUSIVE));

		ApprovalsWrapper.verify(given);
	}

	@Test
	public void testThatFirstVersionCanBeIncludedAndLastVersionCanBeExcluded() throws Exception {
		final GitChangelogApi given =
				gitChangelogApiBuilder() //
						.withFromRevision(new RevisionBoundary(ZERO_COMMIT, InclusivenessStrategy.INCLUSIVE)) //
						.withToRevision(new RevisionBoundary("test", InclusivenessStrategy.EXCLUSIVE));

		ApprovalsWrapper.verify(given);
	}

	@Test
	public void testThatFirstVersionCanBeExcludedAndLastVersionCanBeIncluded() throws Exception {
		final GitChangelogApi given =
				gitChangelogApiBuilder() //
						.withFromRevision(new RevisionBoundary(ZERO_COMMIT, InclusivenessStrategy.EXCLUSIVE)) //
						.withToRevision(new RevisionBoundary("test", InclusivenessStrategy.INCLUSIVE));

		ApprovalsWrapper.verify(given);
	}
}
