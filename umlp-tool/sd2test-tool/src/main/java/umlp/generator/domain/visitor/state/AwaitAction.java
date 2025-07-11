package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.InteractionEntity;

public final class AwaitAction implements VisitorState {

  private final VisitorState previousState;
  private final InteractionEntity source;
  private final InteractionEntity target;

  public AwaitAction(VisitorState previousState, InteractionEntity source, InteractionEntity target) {
    this.previousState = previousState;
    this.source = source;
    this.target = target;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenAwaitAction(this);
  }

  public VisitorState previousState() {
    return previousState;
  }

  public InteractionEntity source() {
    return source;
  }

  public InteractionEntity target() {
    return target;
  }
}
