package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.MatchingStrategy;

import java.util.Optional;

/**
 * Waiting for a diagram matching strategy
 */
public final class DiagramState implements VisitorState {

  private final InitialState initialState;
  private Optional<MatchingStrategy> matchingStrategy = Optional.empty();

  public DiagramState(InitialState initialState) {
    this.initialState = initialState;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenDiagram(this);
  }

  public InitialState initialState() {
    return initialState;
  }

  public Optional<MatchingStrategy> matchingStrategy() {
    return matchingStrategy;
  }

  public DiagramState matchingStrategy(MatchingStrategy matchingStrategy) {
    this.matchingStrategy = Optional.of(matchingStrategy);
    return this;
  }

  @Override
  public String toString() {
    return "DiagramMatchingStrategyState{" + "importSection=" + initialState +
           ", matchingStrategy=" + matchingStrategy + '}';
  }
}
