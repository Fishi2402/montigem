package umlp.generator.gui.genjava.decorator;

public class Argument {
  protected final String name;
  protected final String type;

  public Argument(String type, String name) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }
}