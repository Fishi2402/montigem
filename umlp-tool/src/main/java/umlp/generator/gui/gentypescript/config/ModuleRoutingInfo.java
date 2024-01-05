package umlp.generator.gui.gentypescript.config;

import com.google.common.collect.Iterables;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModuleRoutingInfo {

  protected final List<String> packageParts;
  protected final List<ASTGUICompilationUnit> components = new ArrayList<>();
  private final String generatedFileDir;
  private final String routingPath;

  public ModuleRoutingInfo(List<String> packageParts) {
    this.packageParts = packageParts;
    this.generatedFileDir = String.join("/", packageParts);
    this.routingPath = packageParts.size() > 1
        ? packageParts.get(1) + String.join("/", packageParts.subList(2, packageParts.size()))
        : "";
  }

  public void add(ASTGUICompilationUnit comp) {
    components.add(comp);
  }

  public List<ASTGUICompilationUnit> getComponents() {
    return components;
  }

  public String getRoutingFileNameBase() {
    return StringTransformations.capitalize(Iterables.getLast(packageParts));
  }

  public String getRoutingPath() {
    return routingPath;
  }

  public String getGeneratedFileDir() {
    return generatedFileDir;
  }
}
