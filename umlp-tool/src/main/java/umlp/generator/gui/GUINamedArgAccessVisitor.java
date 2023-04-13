/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;
import de.monticore.prettyprint.IndentPrinter;

import java.util.ArrayList;
import java.util.List;


public class GUINamedArgAccessVisitor implements GUIDSLVisitor2, CommonExpressionsVisitor2, ExpressionsBasisVisitor2, MCCommonLiteralsVisitor2 {

  protected static CD4CodeFullPrettyPrinter astPrinter;
  protected GUIDSLTraverser traverser;
  protected List<ASTCallExpression> callExprs = new ArrayList<>();

  public GUINamedArgAccessVisitor(ASTGUINamedArg node) {
    astPrinter = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    traverser.add4CommonExpressions(this);
    traverser.add4ExpressionsBasis(this);
    traverser.add4MCCommonLiterals(this);
    node.accept(getTraverser());
  }

  public List<ASTCallExpression> getCallExpressions() {
    return callExprs;
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void visit(ASTCallExpression node) {
    callExprs.add(node);
  }

}
