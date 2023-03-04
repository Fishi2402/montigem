/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import com.google.common.collect.Lists;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
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


public class GUIComponentsAccessVisitor implements GUIDSLVisitor2, CommonExpressionsVisitor2, ExpressionsBasisVisitor2, MCCommonLiteralsVisitor2 {

  protected static CD4CodeFullPrettyPrinter astPrinter;
  protected GUIDSLTraverser traverser;
  protected List<ASTGUIComponent> components = new ArrayList<>();
  protected List<ASTFieldAccessExpression> fieldAccess = new ArrayList<>();
  protected List<ASTNameExpression> nameExprs = new ArrayList<>();
  protected Map<ASTGUIComponent, String> namedComponents = new HashMap();
  public static final String COMPONENT_NAME = "Comp";
  protected int counter = 1;

  public GUIComponentsAccessVisitor(ASTGUICompilationUnit node) {
    astPrinter = new CD4CodeFullPrettyPrinter();
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    traverser.add4CommonExpressions(this);
    traverser.add4ExpressionsBasis(this);
    traverser.add4MCCommonLiterals(this);
    node.accept(getTraverser());
  }

  public List<ASTGUIComponent> getComponents() {
    return Lists.newArrayList(getNamedComponents().keySet());
  }

  public List<ASTFieldAccessExpression> getFieldAccess() {
    return fieldAccess;
  }

  public List<ASTNameExpression> getNameExpressions() {
    return nameExprs;
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void visit(ASTGUIComponent node) {
    namedComponents.put(node, buildComponentName(node));
  }

  protected String buildComponentName(ASTGUIComponent component) {
    if (component.isPresentId()) {
      return component.getId().getName();
    }
    return COMPONENT_NAME + counter++;
  }

  public String getComponentName(ASTGUIComponent component) {
    return getNamedComponents().get(component);
  }

  public Map<ASTGUIComponent, String> getNamedComponents() {
    return namedComponents;
  }

  @Override
  public void visit(ASTFieldAccessExpression node) {
    fieldAccess.add(node);
  }

  @Override
  public void visit(ASTNameExpression node) {
    nameExprs.add(node);
  }

  @Override
  public void endVisit(ASTGUICompilationUnit node) {
  }

  public String print(List<? extends ASTExpression> nodes) {
    StringBuilder sb = new StringBuilder();
    for (ASTExpression node : nodes) {
      sb.append(astPrinter.prettyprint(node)).append(" ");
    }
    return sb.toString();
  }

}
