package umlp.generator.gui;

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
import java.util.List;


public class GUIComponentsAccessVisitor implements GUIDSLVisitor2, CommonExpressionsVisitor2, ExpressionsBasisVisitor2, MCCommonLiteralsVisitor2 {

  protected GUIDSLTraverser traverser;
  protected static CD4CodeFullPrettyPrinter astPrinter;

  public List<ASTGUIComponent> getComponents() {
    return components;
  }

  public List<ASTFieldAccessExpression> getFieldAccess() {
    return fieldAccess;
  }

  public List<ASTNameExpression> getNameExpressions() {
    return nameExprs;
  }

  protected List<ASTGUIComponent> components = new ArrayList<>();
  protected List<ASTFieldAccessExpression> fieldAccess = new ArrayList<>();
  protected List<ASTNameExpression> nameExprs = new ArrayList<>();

  public GUIComponentsAccessVisitor(ASTGUICompilationUnit node) {
    astPrinter = new CD4CodeFullPrettyPrinter();
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
    components.add(node);
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
System.err.println("GUIComponentAccessVisitor\n " +
    " fieldAccess: \n"+ print(fieldAccess) + "\nnameExprs: \n" + print(nameExprs));
  }

  public String print(List<? extends ASTExpression> nodes) {
    StringBuilder sb = new StringBuilder();
    for (ASTExpression node: nodes) {
      sb.append(astPrinter.prettyprint(node)).append(" ");
    }
    return sb.toString();
  }

}
