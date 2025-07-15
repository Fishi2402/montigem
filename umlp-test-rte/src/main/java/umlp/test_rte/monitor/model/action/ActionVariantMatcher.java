package umlp.test_rte.monitor.model.action;

public interface ActionVariantMatcher<T> {
  default T whenMethodCall(MethodCallAction methodCall) {
    return otherwise(methodCall);
  }

  default T whenError(ErrorAction errorAction) {
    return otherwise(errorAction);
  }

  default T whenReturn(ReturnAction returnAction) {
    return otherwise(returnAction);
  }

  default T whenActivityBarEnd(ActivityBarEndAction activityBarEndAction) {
    return otherwise(activityBarEndAction);
  }

  T otherwise(Action action);
}
