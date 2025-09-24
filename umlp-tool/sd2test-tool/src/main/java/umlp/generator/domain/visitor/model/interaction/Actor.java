package umlp.generator.domain.visitor.model.interaction;

public final class Actor {
  private final String name;
  private final boolean isStatic;

  public Actor(String name, boolean isStatic) {
    this.name = name;
    this.isStatic = isStatic;
  }

  public String name() {
    return name;
  }

  public boolean isStatic() {
    return isStatic;
  }

  @Override
  public String toString() {
    return "InteractionEntity{" + "name='" + name + '\'' + ", isStatic=" + isStatic + '}';
  }
}
