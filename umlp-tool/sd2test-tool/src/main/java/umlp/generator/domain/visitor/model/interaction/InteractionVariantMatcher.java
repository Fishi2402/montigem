package umlp.generator.domain.visitor.model.interaction;

public interface InteractionVariantMatcher<T> {

  default T whenMethodCall(MethodCallInteraction methodCall) {
    return otherwise(methodCall);
  }

  default T whenConstructorCall(ConstructorCallInteraction constructorCall) {
    return otherwise(constructorCall);
  }

  default T whenReturn(ReturnInteraction returnInteraction) {
    return otherwise(returnInteraction);
  }

  default T whenThrow(ThrowInteraction throwInteraction) {
    return otherwise(throwInteraction);
  }

  T otherwise(Interaction interaction);
}
