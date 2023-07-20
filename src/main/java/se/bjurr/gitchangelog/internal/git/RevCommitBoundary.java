package se.bjurr.gitchangelog.internal.git;

import static java.util.Objects.requireNonNull;

import org.eclipse.jgit.revwalk.RevCommit;
import se.bjurr.gitchangelog.api.InclusivenessStrategy;

/**
 * @author Réda Housni Alaoui
 */
class RevCommitBoundary {

	private final RevCommit revCommit;
	private final InclusivenessStrategy inclusivenessStrategy;

	public RevCommitBoundary(RevCommit revCommit, InclusivenessStrategy inclusivenessStrategy) {
		this.revCommit = requireNonNull(revCommit);
		this.inclusivenessStrategy = requireNonNull(inclusivenessStrategy);
	}

	public RevCommit getRevCommit() {
		return revCommit;
	}

	public InclusivenessStrategy getInclusivenessStrategy() {
		return inclusivenessStrategy;
	}

}
