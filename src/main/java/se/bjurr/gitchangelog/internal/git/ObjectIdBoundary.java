package se.bjurr.gitchangelog.internal.git;

import static java.util.Objects.requireNonNull;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevWalk;
import se.bjurr.gitchangelog.api.InclusivenessStrategy;

/**
 * @author Réda Housni Alaoui
 */
public class ObjectIdBoundary {
	private final ObjectId objectId;
	private final InclusivenessStrategy inclusivenessStrategy;

	public ObjectIdBoundary(ObjectId objectId, InclusivenessStrategy inclusivenessStrategy) {
		this.objectId = requireNonNull(objectId);
		this.inclusivenessStrategy = requireNonNull(inclusivenessStrategy);
	}

	public RevCommitBoundary lookupCommit(RevWalk revWalk) {
		return new RevCommitBoundary(revWalk.lookupCommit(objectId), inclusivenessStrategy);
	}

	public ObjectId getObjectId() {
		return objectId;
	}

	public InclusivenessStrategy getInclusivenessStrategy() {
		return inclusivenessStrategy;
	}
}
