package umlp.generator.gui.genjava.decorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InitDependency {
  protected List<InitDependency> dependentList = new ArrayList<>();
  protected Optional<MethodTemplate> initMethod = Optional.empty();
  protected Optional<MethodTemplate> removeMethod = Optional.empty();

  public void addDependent(InitDependency sub) {
    this.dependentList.add(sub);
  }

  public List<InitDependency> getDependentList() {
    return dependentList;
  }

  public void setInitMethod(MethodTemplate initMethod) {
    this.initMethod = Optional.of(initMethod);
  }

  public Optional<MethodTemplate> getInitMethod() {
    return initMethod;
  }

  public void setRemoveMethod(MethodTemplate removeMethod) {
    this.removeMethod = Optional.of(removeMethod);
  }

  public Optional<MethodTemplate> getRemoveMethod() {
    return removeMethod;
  }
}
