package umlp.test_rte.monitor.event;

public interface StackEvent {
  /**
   * The object on which the method has been called
   */
  Object object();

  /**
   * The name of the method involved
   * @deprecated One should always use the signature
   */
  @Deprecated
  default String methodName() {
    return signature().substring(0, signature().indexOf("("));
  }

  /**
   * A string representation of the method- or constructor signature
   */
  String signature();

  <T> T matchVariant(StackEventVariantMatcher<T> matcher);
}
