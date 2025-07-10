package umlp.generator.domain.visitor.state;

import java.util.Optional;

public final class InteractionSection implements VisitorState {

  private final VisitorState previousState;
  private Optional<String> sourceName = Optional.empty();
  private Optional<String> targetName = Optional.empty();
  //TODO interaction builder

  public InteractionSection(VisitorState previousState) {
    this.previousState = previousState;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenInteractionSection(this);
  }
}
