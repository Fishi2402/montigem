package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.MatchingStrategy;

import java.util.Optional;

/**
 * Waiting for a diagram matching strategy
 */
public final class DiagramMatchingStrategyState implements VisitorState {

  private final ImportSectionState importSection;
  private Optional<MatchingStrategy> matchingStrategy = Optional.empty();

  public DiagramMatchingStrategyState(ImportSectionState importSection) {
    this.importSection = importSection;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenDiagramMatchingStrategy(this);
  }

  public ImportSectionState importSection() {
    return importSection;
  }

  public Optional<MatchingStrategy> matchingStrategy() {
    return matchingStrategy;
  }

  public DiagramMatchingStrategyState matchingStrategy(MatchingStrategy matchingStrategy) {
    this.matchingStrategy = Optional.of(matchingStrategy);
    return this;
  }

  @Override
  public String toString() {
    return "DiagramMatchingStrategyState{" + "importSection=" + importSection +
           ", matchingStrategy=" + matchingStrategy + '}';
  }
}
