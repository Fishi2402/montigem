package umlp.generator.gui.gentypescript;

import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModuleRoutingInfo {

  protected final List<String> packageParts;
  protected final List<ASTGUICompilationUnit> components = new ArrayList<>();

  //protected final List<ModuleRoutingInfo> submodules = new ArrayList<>();

  protected final Set<String> guiImport = new HashSet<>();
  private final String generatedFileDir;
  private final String routingPath;

  public ModuleRoutingInfo(List<String> packageParts) {
    this.packageParts = packageParts;
    this.generatedFileDir = String.join("/", packageParts);
    this.routingPath=packageParts.size()>1?(packageParts.get(1)+String.join("/", packageParts.subList(2,packageParts.size()))):"";
  }

  public void add(ASTGUICompilationUnit comp) {
    components.add(comp);
  }

  public List<ASTGUICompilationUnit> getComponents() {
    return components;
  }

  public List<String> getPackageParts() {
    return packageParts;
  }
/*
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
        break;
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

 */

  public String getRoutingPath() {
    return routingPath;
  }

  public String getGeneratedFileDir() {
    return generatedFileDir;
  }

  public Set<String> getGuiImport() {
    return guiImport;
  }

  public Map<String, String> getImports(GUIGeneratorUtil util, String compDecl) {
    Map<String, String> imports = new HashMap<>();
    for (ASTGUICompilationUnit ast : components) {
      for (ASTMCImportStatement imp : ast.getMCImportStatementList()) {
        String moduleClassName = util.getModuleClassName(imp.getMCQualifiedName().getParts(imp.getMCQualifiedName().sizeParts() - 1));
        if (guiImport.contains(imp.getQName()) && !util.isCreatedTSComponent(moduleClassName)) {
          imports.put(moduleClassName, util.java2tsModule(imp.getQName()));
        }
        else if (guiImport.contains(imp.getQName()) && util.isCreatedTSComponent(moduleClassName) && !(compDecl + "GemModule").equals(util.getPackageModuleName(imp))) {
          imports.put(util.getPackageModuleName(imp), util.getPackageModulePath(imp));
        }
      }
    }
    return imports;
  }
}
