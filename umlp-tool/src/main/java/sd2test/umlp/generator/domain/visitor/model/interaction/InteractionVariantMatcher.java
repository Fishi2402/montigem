package sd2test.umlp.generator.domain.visitor.model.interaction;

public interface InteractionVariantMatcher<T, E extends Throwable> {

  default T whenMethodCall(MethodCallInteraction methodCall) throws E {
    return otherwise(methodCall);
  }

  default T whenConstructorCall(ConstructorCallInteraction constructorCall) throws E {
    return otherwise(constructorCall);
  }

  default T whenReturn(ReturnInteraction returnInteraction) throws E {
    return otherwise(returnInteraction);
  }

  default T whenThrow(ThrowInteraction throwInteraction) throws E {
    return otherwise(throwInteraction);
  }

  T otherwise(Interaction interaction) throws E;
}
