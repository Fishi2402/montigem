package umlp.test_rte.monitor.interaction;

/**
 * An observed interaction in the generated system
 */
public interface Interaction {
  /**
   * The object which reported the interaction
   */
  Object object();

  /**
   * A string representation of the method- or constructor signature, which is associated with
   * this interaction. Useful for debugging purposes.
   *
   * <p>Should NOT be used to identify which interactions belong together!
   */
  String signature();

  <T> T matchVariant(StackEventVariantMatcher<T> matcher);
}
