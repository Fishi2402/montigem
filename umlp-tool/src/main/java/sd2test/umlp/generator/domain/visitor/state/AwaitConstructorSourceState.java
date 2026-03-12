package sd2test.umlp.generator.domain.visitor.state;

import sd2test.umlp.generator.domain.visitor.model.interaction.Actor;
import sd2test.umlp.generator.domain.visitor.model.interaction.ConstructorCallInteraction;

import java.util.function.Function;

public final class AwaitConstructorSourceState implements VisitorState {

  private final VisitorState previousState;
  private final Function<Actor, ConstructorCallInteraction> finisher;

  public AwaitConstructorSourceState(VisitorState previousState,
      Function<Actor, ConstructorCallInteraction> finisher) {
    this.previousState = previousState;
    this.finisher = finisher;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenAwaitConstructorSource(this);
  }

  public VisitorState previousState() {
    return previousState;
  }

  public ConstructorCallInteraction finish(Actor source) {
    return finisher.apply(source);
  }

  @Override
  public String toString() {
    return "AwaitConstructorSourceState{" + "previousState=" + previousState + ", finisher=" +
           finisher + '}';
  }
}
