package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.InteractionEntity;

/**
 * Waiting for action after reading source and target of an interaction
 */
public final class AwaitActionState implements VisitorState {

  private final VisitorState previousState;
  private final InteractionEntity source;
  private final InteractionEntity target;

  public AwaitActionState(VisitorState previousState, InteractionEntity source, InteractionEntity target) {
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
