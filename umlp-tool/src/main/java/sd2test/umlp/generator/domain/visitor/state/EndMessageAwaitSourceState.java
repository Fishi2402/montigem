package sd2test.umlp.generator.domain.visitor.state;

import sd2test.umlp.generator.domain.visitor.model.interaction.Actor;

/**
 * We are waiting for the source of an end message interaction
 */
public final class EndMessageAwaitSourceState implements VisitorState {

  private final EndMessageAwaitTargetState previousState;
  private final Actor target;

  public EndMessageAwaitSourceState(EndMessageAwaitTargetState previousState, Actor target) {
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

  public Actor target() {
    return target;
  }

  @Override
  public String toString() {
    return "EndMessageAwaitSourceState{" + "previousState=" + previousState + ", target=" + target +
           '}';
  }
}
