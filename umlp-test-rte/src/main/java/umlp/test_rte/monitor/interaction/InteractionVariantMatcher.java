package umlp.test_rte.monitor.interaction;

public interface InteractionVariantMatcher<T, E extends Throwable> {

  default T whenMethodCall(MethodCallInteraction methodCall) throws E {
    return otherwise(methodCall);
  }

  default T whenConstructorCall(ConstructorCallInteraction constructorCall) throws E {
    return otherwise(constructorCall);
  }

  default T whenError(ThrowInteraction throwInteraction) throws E {
    return otherwise(throwInteraction);
  }

  default T whenReturn(ReturnInteraction returnInteraction) throws E {
    return otherwise(returnInteraction);
  }

  T otherwise(Interaction interaction) throws E;
}
