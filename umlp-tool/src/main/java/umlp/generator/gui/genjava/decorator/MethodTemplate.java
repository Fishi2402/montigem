package umlp.generator.gui.genjava.decorator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodTemplate {
  protected final String template;

  protected final List<Object> arguments = new ArrayList<>();

  public MethodTemplate(String template, Object... arguments) {
    this.template = template;
    this.arguments.addAll(Arrays.asList(arguments));
  }

  public String getTemplate() {
    return template;
  }

  public List<Object> getArguments() {
    return arguments;
  }

  public void addArgument(Object arg) {
    this.arguments.add(arg);
  }
}
