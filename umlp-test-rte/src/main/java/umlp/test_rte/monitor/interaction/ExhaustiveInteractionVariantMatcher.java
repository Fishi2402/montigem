package umlp.test_rte.monitor.interaction;

public interface ExhaustiveInteractionVariantMatcher<T, E extends Throwable> extends InteractionVariantMatcher<T, E> {

  @Override
  T whenMethodCall(MethodCallInteraction methodCall) throws E;

  @Override
  T whenConstructorCall(ConstructorCallInteraction constructorCall) throws E;

  @Override
  T whenError(ThrowInteraction throwInteraction) throws E;

  @Override
  T whenReturn(ReturnInteraction returnInteraction) throws E;

  @Override
  default T otherwise(Interaction interaction) throws E, IllegalArgumentException {
    throw new IllegalArgumentException("unknown variant: " + interaction);
  }
}
