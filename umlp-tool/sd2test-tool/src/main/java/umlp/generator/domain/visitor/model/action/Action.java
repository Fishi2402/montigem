package umlp.generator.domain.visitor.model.action;

@Deprecated
public interface Action {
  <T> T matchVariant(ActionVariantMatcher<T> matcher);
}
