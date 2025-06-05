package umlp.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents an object defined in a sequence diagram
 */
public final class SDObject {

  private final Optional<MatchingStrategy> matchingStrategy;
  private final Optional<String> name;
  private final String qualifiedTypeName;

  public SDObject(Optional<MatchingStrategy> matchingStrategy,
                  Optional<String> name,
                  String qualifiedTypeName) {
    this.matchingStrategy = matchingStrategy;
    this.name = name;
    this.qualifiedTypeName = qualifiedTypeName;
  }

  public Optional<MatchingStrategy> matchingStrategy() {
    return matchingStrategy;
  }

  public Optional<String> name() {
    return name;
  }

  public String qualifiedTypeName() {
    return qualifiedTypeName;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass())
      return false;
    SDObject sdObject = (SDObject) o;
    return Objects.equals(matchingStrategy, sdObject.matchingStrategy) && Objects.equals(name,
        sdObject.name) && Objects.equals(qualifiedTypeName, sdObject.qualifiedTypeName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(matchingStrategy, name, qualifiedTypeName);
  }

  @Override
  public String toString() {
    return "SDObject{" + "matchingStrategy=" + matchingStrategy + ", name=" + name + ", qualifiedTypeName='" + qualifiedTypeName + '\'' + '}';
  }
}
