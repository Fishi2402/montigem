package umlp.test_rte.monitor.event;

public interface StackEvent {
  /**
   * The object on which the method has been called
   */
  Object object();

  /**
   * A string representation of the method- or constructor signature
   */
  String signature();

  <T> T matchVariant(StackEventVariantMatcher<T> matcher);
}
