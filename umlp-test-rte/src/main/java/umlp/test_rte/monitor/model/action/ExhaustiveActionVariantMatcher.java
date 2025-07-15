package umlp.test_rte.monitor.model.action;

public interface ExhaustiveActionVariantMatcher<T> extends ActionVariantMatcher<T> {

  @Override
  T whenCall(MethodCallAction methodCall);

  @Override
  T whenError(ErrorAction errorAction);

  @Override
  T whenReturn(ReturnAction returnAction);

  @Override
  T whenActivityBarEnd(ActivityBarEndAction activityBarEndAction);

  @Override
  default T otherwise(Action action) {
    throw new IllegalArgumentException("unknown variant: " + action);
  }
}
