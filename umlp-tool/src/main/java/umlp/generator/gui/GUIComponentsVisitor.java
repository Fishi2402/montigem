/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import com.google.common.collect.Lists;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GUIComponentsVisitor implements GUIDSLVisitor2, CommonExpressionsVisitor2, ExpressionsBasisVisitor2, MCCommonLiteralsVisitor2 {

  protected GUIDSLTraverser traverser;

  protected Map<ASTGUIComponent, String> namedComponents = new HashMap<>();
  protected Map<String, String> modules = new HashMap<>();
  protected Map<String, String> modulesQualified = new HashMap<>();
  public static final String COMPONENT_NAME = "Comp";
  protected int counter = 1;

  protected GlobalExtensionManagement glex;

  public GUIComponentsVisitor(ASTGUICompilationUnit node, GlobalExtensionManagement glex) {
    this.glex = glex;
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    traverser.add4CommonExpressions(this);
    traverser.add4ExpressionsBasis(this);
    traverser.add4MCCommonLiterals(this);
    node.accept(getTraverser());
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void visit(ASTGUIComponent node) {
    namedComponents.put(node, buildComponentName(node));
    String qualifiedModuleName = GUIGeneratorUtil.getQualifiedNameForModule(node);
    String moduleName = GUIGeneratorUtil.getModuleName(qualifiedModuleName);
    modules.put(node.getName(), moduleName);
    if (!modulesQualified.containsKey(moduleName)) {
      modulesQualified.put(moduleName, qualifiedModuleName);
    }
  }

  protected String buildComponentName(ASTGUIComponent component) {
    if (component.isPresentId()) {
      return component.getId().getName();
    }
    return COMPONENT_NAME + counter++;
  }

  public List<ASTGUIComponent> getComponents() {
    return Lists.newArrayList(getNamedComponents().keySet());
  }

  public String getComponentName(ASTGUIComponent component) {
    return getNamedComponents().get(component);
  }

  public Map<ASTGUIComponent, String> getNamedComponents() {
    return namedComponents;
  }

  public List<String> getAllModules() {
    return Lists.newArrayList(modulesQualified.keySet());
  }

  public List<String> getModules() {
    List<String> modules = new ArrayList<>();
    modulesQualified.entrySet().forEach(e -> {
      if (modulesQualified.values().stream().filter(q -> e.getValue().startsWith(q) && !q.equals(e.getValue())).findAny().isEmpty()) {
        modules.add(e.getKey());
      }
    });
    return modules;
  }

  public String getQualifiedNameForModule(String module) {
    return modulesQualified.get(module);
  }

  public String getPackageNameForModule(String module) {
    return modulesQualified.get(module).replaceAll("\\.", "/");
  }
}
