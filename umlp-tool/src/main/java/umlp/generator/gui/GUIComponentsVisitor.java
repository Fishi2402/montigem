/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import com.google.common.collect.Lists;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIArgValue;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class GUIComponentsVisitor implements GUIDSLVisitor2, CommonExpressionsVisitor2, ExpressionsBasisVisitor2, MCCommonLiteralsVisitor2 {

  protected GUIDSLTraverser traverser;
  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  protected Map<ASTGUIComponent, String> namedComponents = new HashMap<>();
  protected Map<ASTGUIIterate, String> namedIterates = new HashMap<>();
  protected Map<ASTGUIArgValue, String> namedTemplates = new HashMap<>();
  protected Map<String, String> modules = new HashMap<>();
  protected Map<String, String> modulesQualified = new HashMap<>();
  protected List<ASTGUIComponent> notNestedComponents = new ArrayList<>();
  protected Map<ASTGUIComponent, ASTGUIIterate> componentIterate = new HashMap<>();

  protected List<ASTGUIGuard> guards = new ArrayList<>();

  public static final String COMPONENT_NAME = "Comp";
  protected int counterComponents = 1;
  public static final String TEMPLATE_NAME = "Tpl";
  protected int counterTemplates = 0;

  protected ASTGUIArgValue currentArg = null;
  protected ASTGUIIterate currentIterate = null;

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
    // Build components names
    namedComponents.put(node, buildComponentName(node));
    if (currentArg == null) {
      notNestedComponents.add(node);
    }

    if (currentIterate != null) {
      componentIterate.put(node, currentIterate);
    }

    // Build modules
    String qualifiedModuleName = node.getNameSymbol().getFullName();
    String moduleName = util.getModuleName(qualifiedModuleName);
    modules.put(node.getName(), moduleName);
    if (!modulesQualified.containsKey(moduleName)) {
      modulesQualified.put(moduleName, qualifiedModuleName);
    }
  }

  @Override
  public void visit(ASTGUIGuard node) {
    guards.add(node);
  }

  @Override
  public void visit(ASTGUIIterate node) {
    currentIterate = node;
    // Build components names
    namedIterates.put(node, buildComponentName(node));
  }

  @Override
  public void endVisit(ASTGUIIterate node) {
    currentIterate = null;
  }
  /*
  @Override
  public void visit(ASTGUINamedArgs node) {
    counterTemplates = 1;
  }

  @Override
  public void endVisit(ASTGUINamedArgs node) {
    counterTemplates = 0;
  }

   */

  @Override
  public void visit(ASTGUIArgValue node) {
    namedTemplates.put(node, buildTemplateName(node));
    currentArg = node;
  }

  @Override
  public void endVisit(ASTGUIArgValue node) {
    currentArg = null;
  }

  protected String buildComponentName(ASTGUIComponent component) {
    return _buildComponentName(component) + addIterateSuffix();
  }

  protected String buildComponentName(ASTGUIIterate component) {
    return _buildComponentName(component);
  }

  protected boolean isCurrnetIterateComponent(ASTGUIComponent component) {
    return currentIterate == null ||
        !componentIterate.values().stream()
            .filter(e -> e.equals(component)).findAny().isPresent();
  }

  protected String addIterateSuffix() {
    return currentIterate != null ? "Of" + getComponentName(currentIterate) : "";
  }

  protected String _buildComponentName(ASTGUIComponent component) {
    if (component.isPresentId()) {
      return component.getId().getName();
    }
    return COMPONENT_NAME + counterComponents++;
  }

  protected String _buildComponentName(ASTGUIIterate component) {
    if (component.isPresentId()) {
      return component.getId().getName();
    }
    return COMPONENT_NAME + counterComponents++;
  }

  protected String buildTemplateName(ASTGUIArgValue namedArg) {
    return TEMPLATE_NAME + counterTemplates++;
  }

  public List<ASTGUIComponent> getComponents() {
    return Lists.newArrayList(getNamedComponents().keySet());
  }

  public String getComponentName(ASTGUIComponent component) {
    return getNamedComponents().get(component);
  }

  public String getComponentName(ASTGUIIterate component) {
    return getNamedIterates().get(component);
  }

  public Map<ASTGUIComponent, String> getNamedComponents() {
    return namedComponents;
  }

  public Map<ASTGUIIterate, String> getNamedIterates() {
    return namedIterates;
  }

  public List<ASTGUIIterate> getIterates() {
    return Lists.newArrayList(getNamedIterates().keySet());
  }

  public String getTemplateName(ASTGUIArgValue namedArg) {
    return namedTemplates.get(namedArg);
  }

  public List<ASTGUIComponent> getNotNestedComponents() {
    return notNestedComponents;
  }

  public ASTGUIIterate getCurrentIterate() {
    return currentIterate;
  }

  public Optional<ASTGUIIterate> getIterateForComponent(ASTGUIComponent node) {
    return Optional.ofNullable(componentIterate.get(node));
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

  public List<ASTGUIGuard> getGuards() {
    return guards;
  }

}
