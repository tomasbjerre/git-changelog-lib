package se.bjurr.gitchangelog.api;

/**
 * @author Réda Housni Alaoui
 */
public enum InclusivenessStrategy {
	INCLUSIVE,
	EXCLUSIVE,
	/**
	 * This strategy will:
	 * <ul>
	 * <li>include the lower bound zero commit revision</li>
	 * <li>exclude lower bound non-zero commit revision</li>
	 * <li>include the upper bound revision</li>
	 * </ul>
	 *
	 * @deprecated Use {@link #INCLUSIVE} or {@link #EXCLUSIVE} instead
	 */
	@Deprecated
	LEGACY
}
