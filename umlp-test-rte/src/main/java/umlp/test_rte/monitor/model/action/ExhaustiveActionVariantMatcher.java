package umlp.test_rte.monitor.model.action;

public interface ExhaustiveActionVariantMatcher<T> extends ActionVariantMatcher<T> {

  @Override
  T whenCall(Call call);

  @Override
  T whenError(Error error);

  @Override
  T whenReturn(Return returnAction);

  @Override
  default T otherwise(Action action) {
    throw new IllegalArgumentException("unknown variant: " + action);
  }
}
