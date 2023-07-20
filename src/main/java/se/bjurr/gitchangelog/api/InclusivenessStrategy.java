package se.bjurr.gitchangelog.api;

/**
 * @author Réda Housni Alaoui
 */
public enum InclusivenessStrategy {
  INCLUSIVE,
  EXCLUSIVE,
  /**
   * This strategy will:
   *
   * <ul>
   *   <li>include the lower bound zero commit revision
   *   <li>exclude lower bound non-zero commit revision
   *   <li>include the upper bound revision
   * </ul>
   */
  DEFAULT
}
