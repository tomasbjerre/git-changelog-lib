package se.bjurr.gitchangelog.internal.git;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
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

	public static List<RevCommit> listCommits(Git git, RevWalk revWalk, RevCommitBoundary from, RevCommitBoundary to, String pathFilter) throws IOException, GitAPIException {
		final LogCommand logCommand = git.log().addRange(from.revCommit, to.revCommit);
		if (pathFilter != null && !pathFilter.isEmpty()) {
			logCommand.addPath(pathFilter);
		}
		final List<RevCommit> list = new ArrayList<>();

		for (RevCommit commit : logCommand.call()) {
			list.add(commit);
		}

		if (from.inclusivenessStrategy == InclusivenessStrategy.LEGACY) {
			revWalk.parseHeaders(from.revCommit);
			if (from.revCommit.getParentCount() == 0) {
				list.add(from.revCommit);
			}
		}
		if (from.inclusivenessStrategy == InclusivenessStrategy.INCLUSIVE) {
			list.add(from.revCommit);
		}
		if (to.inclusivenessStrategy == InclusivenessStrategy.EXCLUSIVE) {
			list.remove(to.revCommit);
		}

		return list;
	}

}
