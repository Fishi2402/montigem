package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.InteractionEntity;

public final class EndMessageAwaitSource implements VisitorState {

  private final EndMessageAwaitTarget previousState;
  private final InteractionEntity target;

  public EndMessageAwaitSource(EndMessageAwaitTarget previousState, InteractionEntity target) {
    this.previousState = previousState;
    this.target = target;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenEndMessageAwaitSource(this);
  }

  public EndMessageAwaitTarget previousState() {
    return previousState;
  }

  public InteractionEntity target() {
    return target;
  }
}
