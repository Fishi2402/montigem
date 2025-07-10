package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.MatchingStrategy;

import java.util.Optional;

public final class SDObjectDeclaration implements VisitorState {

  private final SDObjectSection section;
  private final String name;
  private final String qualifiedTypeName;
  private Optional<MatchingStrategy> matchingStrategy = Optional.empty();

  public SDObjectDeclaration(SDObjectSection section, String name, String qualifiedTypeName) {
    this.section = section;
    this.name = name;
    this.qualifiedTypeName = qualifiedTypeName;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenSDObjectDeclaration(this);
  }

  public SDObjectSection section() {
    return section;
  }

  public Optional<MatchingStrategy> matchingStrategy() {
    return matchingStrategy;
  }

  public SDObjectDeclaration matchingStrategy(MatchingStrategy matchingStrategy) {
    this.matchingStrategy = Optional.of(matchingStrategy);
    return this;
  }

  public String name() {
    return name;
  }

  public String qualifiedTypeName() {
    return qualifiedTypeName;
  }
}
