package umlp.test_rte.monitor.model.action;

public interface ActionVariantMatcher<T> {
  default T whenCall(Call call) {
    return otherwise(call);
  }

  default T whenError(Error error) {
    return otherwise(error);
  }

  default T whenReturn(Return returnAction) {
    return otherwise(returnAction);
  }

  default T whenActivityBarEnd(ActivityBarEnd activityBarEnd) {
    return otherwise(activityBarEnd);
  }

  T otherwise(Action action);
}
