package umlp.generator.domain.visitor.model.interaction;

public interface ExhaustiveInteractionVariantMatcher<T, E extends Throwable> extends InteractionVariantMatcher<T, E> {
  @Override
  T whenMethodCall(MethodCallInteraction methodCall) throws E;

  @Override
  T whenConstructorCall(ConstructorCallInteraction constructorCall) throws E;

  @Override
  T whenReturn(ReturnInteraction returnInteraction) throws E;

  @Override
  T whenThrow(ThrowInteraction throwInteraction) throws E;

  @Override
  default T otherwise(Interaction interaction) throws E, IllegalArgumentException {
    throw new IllegalArgumentException("unknown variant: " + interaction);
  }
}
