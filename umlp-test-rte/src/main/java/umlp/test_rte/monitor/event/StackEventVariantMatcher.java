package umlp.test_rte.monitor.event;

public interface StackEventVariantMatcher<T> {

  default T whenMethodCall(MethodCallEvent event) {
    return otherwise(event);
  }

  default T whenConstructorCall(ConstructorCallEvent event) {
    return otherwise(event);
  }

  default T whenError(ErrorEvent event) {
    return otherwise(event);
  }

  default T whenReturnWithValue(ReturnWithValueEvent event) {
    return otherwise(event);
  }

  default T whenReturn(ReturnEvent event) {
    return otherwise(event);
  }

  T otherwise(StackEvent event);
}
