package umlp.generator.gui.gentypescript;

import de.monticore.guidsl._ast.ASTGUICompilationUnit;

import java.util.ArrayList;
import java.util.List;

public class ModuleRoutingInfo {

  protected final List<String> packageParts;
  protected final List<ASTGUICompilationUnit> components = new ArrayList<>();

  protected final List<ModuleRoutingInfo> submodules = new ArrayList<>();

  public ModuleRoutingInfo(List<String> packageParts) {
    this.packageParts = packageParts;
    this.routingPath=String.join("/",packageParts);
  }

  public void add(ASTGUICompilationUnit comp) {
    components.add(comp);
  }
  private String routingPath;

  public List<ASTGUICompilationUnit> getComponents() {
    return components;
  }

  public List<String> getPackageParts() {
    return packageParts;
  }

  public boolean isDirectSubmodule(ModuleRoutingInfo module) {
    boolean res = packageParts.size() - 1 == module.getPackageParts().size();
    if (!res) {
      return false;
    }
    for (int i = 0; i < module.getPackageParts().size(); ++i) {
      String submodulePart = module.getPackageParts().get(i);
      String modulePart = this.getPackageParts().get(i);
      if (!submodulePart.equals(modulePart)) {
        res = false;
      }
    }
    return res;
  }

  public void addSubmodule(ModuleRoutingInfo module) {
    submodules.add(module);
  }

  public List<ModuleRoutingInfo> getSubmodules() {
    return submodules;
  }


  public String getRoutingPath()
  {
    if(this.routingPath==null)
    {

    }
    return routingPath;
  }
}
