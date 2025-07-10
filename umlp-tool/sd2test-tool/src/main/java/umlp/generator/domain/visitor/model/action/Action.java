package umlp.generator.domain.visitor.model.action;

public interface Action {
  <T> T matchVariant(ActionVariantMatcher<T> matcher);
}
