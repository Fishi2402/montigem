package umlp.generator.domain.visitor.model;

import java.util.Optional;

public final class SDObject {
  private final Optional<MatchingStrategy> matchingStrategy;
  private final String name;
  private final String qualifiedTypeName;

  public SDObject(Optional<MatchingStrategy> matchingStrategy,
      String name,
      String qualifiedTypeName) {
    this.matchingStrategy = matchingStrategy;
    this.name = name;
    this.qualifiedTypeName = qualifiedTypeName;
  }

  public Optional<MatchingStrategy> matchingStrategy() {
    return matchingStrategy;
  }

  public String name() {
    return name;
  }

  public String qualifiedTypeName() {
    return qualifiedTypeName;
  }

  @Override
  public String toString() {
    return "SDObject{" + "matchingStrategy=" + matchingStrategy + ", name='" + name + '\'' +
           ", qualifiedTypeName='" + qualifiedTypeName + '\'' + '}';
  }
}
