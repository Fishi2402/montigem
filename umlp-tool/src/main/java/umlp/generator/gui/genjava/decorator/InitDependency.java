package umlp.generator.gui.genjava.decorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InitDependency {
  protected List<InitDependency> dependentList = new ArrayList<>();
  protected Optional<InitMethodTemplate> initMethod = Optional.empty();

  public void addDependent(InitDependency sub) {
    this.dependentList.add(sub);
  }

  public List<InitDependency> getDependentList() {
    return dependentList;
  }

  public void setInitMethod(InitMethodTemplate initMethod) {
    this.initMethod = Optional.of(initMethod);
  }

  public Optional<InitMethodTemplate> getInitMethod() {
    return initMethod;
  }
}
