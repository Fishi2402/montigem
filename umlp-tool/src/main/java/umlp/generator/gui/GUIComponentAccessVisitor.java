/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;
import de.monticore.prettyprint.IndentPrinter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GUIComponentAccessVisitor implements GUIDSLVisitor2, CommonExpressionsVisitor2, ExpressionsBasisVisitor2, MCCommonLiteralsVisitor2 {

  protected static CD4CodeFullPrettyPrinter astPrinter;
  protected GUIDSLTraverser traverser;
  protected List<ASTFieldAccessExpression> fieldAccess = new ArrayList<>();
  protected List<ASTNameExpression> nameExprs = new ArrayList<>();
  protected List<ASTCallExpression> callExprs = new ArrayList<>();
  protected List<ASTGUINamedArg> namedArgs = new ArrayList<>();

  protected Map<ASTGUINamedArg, Set<ASTFieldAccessExpression>> namedArgsFieldAccess = new HashMap<>();

  protected ASTGUINamedArg actualNamedArg = null;

  public GUIComponentAccessVisitor(ASTGUIComponent node) {
    astPrinter = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    traverser.add4CommonExpressions(this);
    traverser.add4ExpressionsBasis(this);
    traverser.add4MCCommonLiterals(this);
    node.accept(getTraverser());
  }

  public List<ASTFieldAccessExpression> getFieldAccess() {
    return fieldAccess;
  }

  public List<ASTNameExpression> getNameExpressions() {
    return nameExprs;
  }

  public List<ASTCallExpression> getCallExpressions() {
    return callExprs;
  }

  public List<ASTGUINamedArg> getNamedArguments() {
    return namedArgs;
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  public Map<ASTGUINamedArg, Set<ASTFieldAccessExpression>> getNamedArgsFieldAccess() {
    return namedArgsFieldAccess;
  }

  @Override
  public void visit(ASTFieldAccessExpression node) {
    fieldAccess.add(node);
    if (namedArgsFieldAccess.containsKey(actualNamedArg)) {
      namedArgsFieldAccess.get(actualNamedArg).add(node);
    }
  }

  @Override
  public void visit(ASTNameExpression node) {
    nameExprs.add(node);
  }

  @Override
  public void visit(ASTCallExpression node) {
    callExprs.add(node);
  }

  @Override
  public void visit(ASTGUINamedArg node) {
    namedArgs.add(node);
    namedArgsFieldAccess.put(node, new HashSet<>());
    actualNamedArg = node;
  }

  @Override
  public void endVisit(ASTGUINamedArg node) {
    actualNamedArg = null;
  }

}
