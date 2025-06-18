package umlp.model;

/**
 * Describes the different matching strategies which an object defined in a sequence diagram may have.
 */
//TODO do I really need to duplicate this class?
public enum MatchingStrategy {
  /**
   * Limitless other interactions with the object are possible.
   */
  FREE,
  /**
   * Interactions of a different kind (different method calls) are okay, but the documented
   * interactions have to match exactly, until the last one has occurred. After that, unlimited more
   * of that interaction may occur.
   */
  INITIAL,
  /**
   * Interactions with other visible objects (visible in the diagram) must be complete,
   * interactions with other objects may be omitted.
   */
  VISIBLE,
  /**
   * Every interaction must happen exactly as defined in the sequence diagram.
   */
  COMPLETE;

  /**
   * Get the default matching strategy, {@link #FREE} as per convention.
   * @return The default matching strategy
   */
  public static MatchingStrategy getDefault() {
    return MatchingStrategy.FREE;
  }
}
