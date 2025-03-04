package umlp.test_rte.monitor.event;

public interface StackEvent {
  /**
   * The object on which the method has been called
   */
  Object object();
  String methodName();
}
