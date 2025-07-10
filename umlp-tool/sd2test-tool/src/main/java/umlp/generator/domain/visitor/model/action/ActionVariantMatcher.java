package umlp.generator.domain.visitor.model.action;

public interface ActionVariantMatcher<T> {
  default T whenCall(Call call) {
    return otherwise(call);
  }
  T otherwise(Action action);
}
