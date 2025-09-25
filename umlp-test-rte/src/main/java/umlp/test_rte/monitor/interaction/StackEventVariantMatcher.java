package umlp.test_rte.monitor.interaction;

public interface StackEventVariantMatcher<T> {

  default T whenMethodCall(MethodCallInteraction methodCall) {
    return otherwise(methodCall);
  }

  default T whenConstructorCall(ConstructorCallInteraction constructorCall) {
    return otherwise(constructorCall);
  }

  default T whenError(ThrowInteraction throwInteraction) {
    return otherwise(throwInteraction);
  }

  default T whenReturn(ReturnInteraction returnInteraction) {
    return otherwise(returnInteraction);
  }

  T otherwise(Interaction interaction);
}
