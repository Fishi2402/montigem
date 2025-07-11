package umlp.generator.domain.visitor.state;

//TODO implement toString for all states
public interface VisitorState {
  <T> T matchVariant(VisitorStateVariantMatcher<T> matcher);
}
