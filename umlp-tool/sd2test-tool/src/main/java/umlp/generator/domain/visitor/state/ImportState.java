package umlp.generator.domain.visitor.state;

/**
 * We are waiting for an import statement to finish
 */
public final class ImportState implements VisitorState {

  private final InitialState initialState;

  public ImportState(InitialState initialState) {
    this.initialState = initialState;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenImport(this);
  }

  public InitialState initialState() {
    return initialState;
  }

  @Override
  public String toString() {
    return "ImportState{" + "initialState=" + initialState + '}';
  }
}
