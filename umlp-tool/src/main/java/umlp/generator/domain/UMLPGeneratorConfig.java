package umlp.generator.domain;

public class UMLPGeneratorConfig {
  private boolean useSpring;

  public UMLPGeneratorConfig(boolean useSpring) {
    this.useSpring = useSpring;
  }

  public boolean isUseSpring() {
    return useSpring;
  }
}
