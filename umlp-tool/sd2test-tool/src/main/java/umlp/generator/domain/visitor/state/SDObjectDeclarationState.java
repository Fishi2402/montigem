package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.MatchingStrategy;

import java.util.Optional;

/**
 * waiting for an sd-object declaration to finish
 */
public final class SDObjectDeclarationState implements VisitorState {

  private final SDObjectSectionState section;
  private final String name;
  private final String qualifiedTypeName;
  private Optional<MatchingStrategy> matchingStrategy = Optional.empty();

  public SDObjectDeclarationState(SDObjectSectionState section, String name, String qualifiedTypeName) {
    this.section = section;
    this.name = name;
    this.qualifiedTypeName = qualifiedTypeName;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenSDObjectDeclaration(this);
  }

  public SDObjectSectionState section() {
    return section;
  }

  public Optional<MatchingStrategy> matchingStrategy() {
    return matchingStrategy;
  }

  public SDObjectDeclarationState matchingStrategy(MatchingStrategy matchingStrategy) {
    this.matchingStrategy = Optional.of(matchingStrategy);
    return this;
  }

  public String name() {
    return name;
  }

  public String qualifiedTypeName() {
    return qualifiedTypeName;
  }

  @Override
  public String toString() {
    return "SDObjectDeclarationState{" + "section=" + section + ", name='" + name + '\'' +
           ", qualifiedTypeName='" + qualifiedTypeName + '\'' + ", matchingStrategy=" +
           matchingStrategy + '}';
  }
}
