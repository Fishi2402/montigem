package umlp.generator.gui.genjava.decorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InitDependency {
  protected InitDependency dependent;
  protected List<InitDependency> dependencyList = new ArrayList<>();
  protected Optional<InitMethodTemplate> initMethod = Optional.empty();

  public void setDependent(InitDependency dependent) {
    this.dependent = dependent;
    dependent.addDependency(this);
  }

  public void addDependency(InitDependency sub) {
    this.dependencyList.add(sub);
  }

  public List<InitDependency> getDependencyList() {
    return dependencyList;
  }

  public void setInitMethod(InitMethodTemplate initMethod) {
    this.initMethod = Optional.of(initMethod);
  }

  public Optional<InitMethodTemplate> getInitMethod() {
    return initMethod;
  }
}
