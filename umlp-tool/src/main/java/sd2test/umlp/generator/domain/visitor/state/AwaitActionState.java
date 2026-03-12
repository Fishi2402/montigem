package sd2test.umlp.generator.domain.visitor.state;

import sd2test.umlp.generator.domain.visitor.model.interaction.Actor;

/**
 * Waiting for action after reading source and target of an interaction
 */
public final class AwaitActionState implements VisitorState {

  private final VisitorState previousState;
  private final Actor source;
  private final Actor target;

  public AwaitActionState(VisitorState previousState, Actor source, Actor target) {
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

  public Actor source() {
    return source;
  }

  public Actor target() {
    return target;
  }

  @Override
  public String toString() {
    return "AwaitActionState{" + "previousState=" + previousState + ", source=" + source +
           ", target=" + target + '}';
  }
}
