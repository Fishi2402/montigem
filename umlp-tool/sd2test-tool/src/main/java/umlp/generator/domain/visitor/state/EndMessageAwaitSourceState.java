package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.InteractionEntity;

public final class EndMessageAwaitSourceState implements VisitorState {

  private final EndMessageAwaitTargetState previousState;
  private final InteractionEntity target;

  public EndMessageAwaitSourceState(EndMessageAwaitTargetState previousState, InteractionEntity target) {
    this.previousState = previousState;
    this.target = target;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenEndMessageAwaitSource(this);
  }

  public EndMessageAwaitTargetState previousState() {
    return previousState;
  }

  public InteractionEntity target() {
    return target;
  }
}
