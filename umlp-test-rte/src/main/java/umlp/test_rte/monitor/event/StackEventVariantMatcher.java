package umlp.test_rte.monitor.event;

public interface StackEventVariantMatcher<T> {

  default T whenCallEvent(CallEvent event) {
    return otherwise(event);
  }

  default T whenErrorEvent(ErrorEvent event) {
    return otherwise(event);
  }

  default T whenReturnEvent(ReturnEvent event) {
    return otherwise(event);
  }

  default T whenReturnVoidEvent(ReturnVoidEvent event) {
    return otherwise(event);
  }

  T otherwise(StackEvent event);
}
