package umlp.generator.gui.genjava.decorator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotifyMethodTemplate {
  protected final String template;

  protected final List<Object> arguments = new ArrayList<>();
  protected final List<String> calledMethods = new ArrayList<>();

  public NotifyMethodTemplate(String template, Object... arguments) {
    this.template = template;
    this.arguments.addAll(Arrays.asList(arguments));
  }

  public String getTemplate() {
    return template;
  }

  public List<Object> getArguments() {
    List<Object> res = new ArrayList<>(arguments);
    res.add(calledMethods);
    return res;
  }

  public void addCalledMethod(String name) {
    this.calledMethods.add(name);
  }

}
