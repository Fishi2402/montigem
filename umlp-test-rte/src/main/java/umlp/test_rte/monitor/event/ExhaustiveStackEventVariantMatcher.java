package umlp.test_rte.monitor.event;

public interface ExhaustiveStackEventVariantMatcher<T> extends StackEventVariantMatcher<T> {

  @Override
  T whenMethodCall(MethodCallEvent event);

  @Override
  T whenConstructorCall(ConstructorCallEvent event);

  @Override
  T whenError(ErrorEvent event);

  @Override
  T whenReturnWithValue(ReturnWithValueEvent event);

  @Override
  T whenReturn(ReturnEvent event);

  @Override
  default T otherwise(StackEvent event) {
    throw new IllegalArgumentException("unknown variant: " + event);
  }
}
