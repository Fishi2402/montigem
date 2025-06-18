package umlp.model;


import java.util.Objects;
import java.util.Optional;

/**
 * Represents an object defined in a sequence diagram
 */
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

  public static final class Builder {

    private Optional<MatchingStrategy> matchingStrategy;
    private String name;
    private String qualifiedTypeName;

    public Builder(String qualifiedTypeName, String name) {
      this.qualifiedTypeName = qualifiedTypeName;
      this.name = name;
    }

    public Optional<MatchingStrategy> getMatchingStrategy() {
      return matchingStrategy;
    }

    public Builder setMatchingStrategy(MatchingStrategy matchingStrategy) {
      return setMatchingStrategy(Optional.of(matchingStrategy));
    }

    public Builder setMatchingStrategy(Optional<MatchingStrategy> matchingStrategy) {
      this.matchingStrategy = matchingStrategy;
      return this;
    }

    public String getName() {
      return name;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public String getQualifiedTypeName() {
      return qualifiedTypeName;
    }

    public Builder setQualifiedTypeName(String qualifiedTypeName) {
      this.qualifiedTypeName = qualifiedTypeName;
      return this;
    }

    public SDObject build() {
      return new SDObject(matchingStrategy, name, qualifiedTypeName);
    }
  }
}
