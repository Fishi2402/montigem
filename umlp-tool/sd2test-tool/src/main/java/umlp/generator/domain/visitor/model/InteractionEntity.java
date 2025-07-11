package umlp.generator.domain.visitor.model;

public final class InteractionEntity {
  private final String name;
  private final boolean isStatic;

  public InteractionEntity(String name, boolean isStatic) {
    this.name = name;
    this.isStatic = isStatic;
  }

  public String name() {
    return name;
  }

  public boolean isStatic() {
    return isStatic;
  }
}
