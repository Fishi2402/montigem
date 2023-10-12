package umlp.generator.domain;

public class UMLPGeneratorConfig {
  private boolean useSpring;
  private boolean useShell;

  public UMLPGeneratorConfig(boolean useSpring, boolean useShell) {
    this.useSpring = useSpring;
    this.useShell = useShell;
  }

  public boolean isUseSpring() {
    return useSpring;
  }

  public boolean isUseShell() {
    return useShell;
  }
}
