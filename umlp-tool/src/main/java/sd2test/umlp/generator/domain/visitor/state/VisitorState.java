package sd2test.umlp.generator.domain.visitor.state;

public interface VisitorState {
  <T> T matchVariant(VisitorStateVariantMatcher<T> matcher);
}
