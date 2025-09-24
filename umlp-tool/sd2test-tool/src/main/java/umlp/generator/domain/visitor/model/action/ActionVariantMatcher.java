package umlp.generator.domain.visitor.model.action;

@Deprecated
public interface ActionVariantMatcher<T> {

  default T whenCall(CallAction callAction) {
    return otherwise(callAction);
  }

  default T whenReturn(ReturnAction returnAction) {
    return otherwise(returnAction);
  }

  default T whenThrow(ThrowAction throwAction) {
    return otherwise(throwAction);
  }

  T otherwise(Action action);
}
