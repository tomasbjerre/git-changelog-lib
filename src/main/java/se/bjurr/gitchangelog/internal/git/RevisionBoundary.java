package se.bjurr.gitchangelog.internal.git;

import static java.util.Objects.requireNonNull;

import se.bjurr.gitchangelog.api.InclusivenessStrategy;

/**
 * @author Réda Housni Alaoui
 */
public class RevisionBoundary<T> {
  private final T revision;
  private final InclusivenessStrategy inclusivenessStrategy;

  public RevisionBoundary(final T revision, final InclusivenessStrategy inclusivenessStrategy) {
    this.revision = requireNonNull(revision);
    this.inclusivenessStrategy = requireNonNull(inclusivenessStrategy);
  }

  public T getRevision() {
    return this.revision;
  }

  public InclusivenessStrategy getInclusivenessStrategy() {
    return this.inclusivenessStrategy;
  }
}
