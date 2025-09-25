package umlp.test_rte.monitor.interaction;

public interface ExhaustiveStackEventVariantMatcher<T> extends StackEventVariantMatcher<T> {

  @Override
  T whenMethodCall(MethodCallInteraction methodCall);

  @Override
  T whenConstructorCall(ConstructorCallInteraction constructorCall);

  @Override
  T whenError(ThrowInteraction throwInteraction);

  @Override
  T whenReturn(ReturnInteraction returnInteraction);

  @Override
  default T otherwise(Interaction interaction) {
    throw new IllegalArgumentException("unknown variant: " + interaction);
  }
}
