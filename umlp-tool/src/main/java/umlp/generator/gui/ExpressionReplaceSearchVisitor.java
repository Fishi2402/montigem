/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanAndOpExpression;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanNotExpression;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanOrOpExpression;
import de.monticore.expressions.commonexpressions._ast.ASTBracketExpression;
import de.monticore.expressions.commonexpressions._ast.ASTConditionalExpression;
import de.monticore.expressions.commonexpressions._ast.ASTDivideExpression;
import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTGreaterEqualExpression;
import de.monticore.expressions.commonexpressions._ast.ASTGreaterThanExpression;
import de.monticore.expressions.commonexpressions._ast.ASTLessEqualExpression;
import de.monticore.expressions.commonexpressions._ast.ASTLessThanExpression;
import de.monticore.expressions.commonexpressions._ast.ASTLogicalNotExpression;
import de.monticore.expressions.commonexpressions._ast.ASTMinusExpression;
import de.monticore.expressions.commonexpressions._ast.ASTMinusPrefixExpression;
import de.monticore.expressions.commonexpressions._ast.ASTModuloExpression;
import de.monticore.expressions.commonexpressions._ast.ASTMultExpression;
import de.monticore.expressions.commonexpressions._ast.ASTNotEqualsExpression;
import de.monticore.expressions.commonexpressions._ast.ASTPlusExpression;
import de.monticore.expressions.commonexpressions._ast.ASTPlusPrefixExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIArgValue;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._symboltable.BuiltInTypes;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.types.check.SymTypeExpression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class ExpressionReplaceSearchVisitor implements GUIDSLVisitor2, ExpressionsBasisVisitor2, CommonExpressionsVisitor2, CommonExpressionsHandler {

  protected GUIDSLTraverser traverser;

  protected Map.Entry<ASTNode, ASTExpression> potentialHolder;

  public ExpressionReplaceSearchVisitor() {
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    traverser.add4ExpressionsBasis(this);
    traverser.add4CommonExpressions(this);
    traverser.setCommonExpressionsHandler(this);
  }

  @Override
  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(GUIDSLTraverser traverser) {
    this.traverser = traverser;
  }

  public void setTraverser(CommonExpressionsTraverser traverser) {
    // do not throw error
  }

  @Override
  public void visit(ASTGUIArgValue node) {
    potentialHolder = Map.entry(node, node.getExpression());
  }

  @Override
  public void visit(ASTFieldAccessExpression node) {
    potentialHolder = Map.entry(node, node.getExpression());
  }

  @Override
  public void visit(ASTPlusPrefixExpression node) {
    potentialHolder = Map.entry(node, node.getExpression());
  }

  @Override
  public void visit(ASTMinusPrefixExpression node) {
    potentialHolder = Map.entry(node, node.getExpression());
  }

  @Override
  public void visit(ASTBooleanNotExpression node) {
    potentialHolder = Map.entry(node, node.getExpression());
  }

  @Override
  public void visit(ASTLogicalNotExpression node) {
    potentialHolder = Map.entry(node, node.getExpression());
  }

  @Override
  public void traverse(ASTMinusExpression node) {
    if (null != node.getLeft()) {
      potentialHolder = Map.entry(node, node.getLeft());
      node.getLeft().accept(getTraverser());
    }
    if (null != node.getRight()) {
      potentialHolder = Map.entry(node, node.getRight());
      node.getRight().accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTMultExpression node) {
    if (null != node.getLeft()) {
      potentialHolder = Map.entry(node, node.getLeft());
      node.getLeft().accept(getTraverser());
    }
    if (null != node.getRight()) {
      potentialHolder = Map.entry(node, node.getRight());
      node.getRight().accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTDivideExpression node) {
    if (null != node.getLeft()) {
      potentialHolder = Map.entry(node, node.getLeft());
      node.getLeft().accept(getTraverser());
    }
    if (null != node.getRight()) {
      potentialHolder = Map.entry(node, node.getRight());
      node.getRight().accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTModuloExpression node) {
    if (null != node.getLeft()) {
      potentialHolder = Map.entry(node, node.getLeft());
      node.getLeft().accept(getTraverser());
    }
    if (null != node.getRight()) {
      potentialHolder = Map.entry(node, node.getRight());
      node.getRight().accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTPlusExpression node) {
    if (null != node.getLeft()) {
      potentialHolder = Map.entry(node, node.getLeft());
      node.getLeft().accept(getTraverser());
    }
    if (null != node.getRight()) {
      potentialHolder = Map.entry(node, node.getRight());
      node.getRight().accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTLessEqualExpression node) {
    if (null != node.getLeft()) {
      potentialHolder = Map.entry(node, node.getLeft());
      node.getLeft().accept(getTraverser());
    }
    if (null != node.getRight()) {
      potentialHolder = Map.entry(node, node.getRight());
      node.getRight().accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTGreaterEqualExpression node) {
    if (null != node.getLeft()) {
      potentialHolder = Map.entry(node, node.getLeft());
      node.getLeft().accept(getTraverser());
    }
    if (null != node.getRight()) {
      potentialHolder = Map.entry(node, node.getRight());
      node.getRight().accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTLessThanExpression node) {
    if (null != node.getLeft()) {
      potentialHolder = Map.entry(node, node.getLeft());
      node.getLeft().accept(getTraverser());
    }
    if (null != node.getRight()) {
      potentialHolder = Map.entry(node, node.getRight());
      node.getRight().accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTGreaterThanExpression node) {
    if (null != node.getLeft()) {
      potentialHolder = Map.entry(node, node.getLeft());
      node.getLeft().accept(getTraverser());
    }
    if (null != node.getRight()) {
      potentialHolder = Map.entry(node, node.getRight());
      node.getRight().accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTEqualsExpression node) {
    if (null != node.getLeft()) {
      potentialHolder = Map.entry(node, node.getLeft());
      node.getLeft().accept(getTraverser());
    }
    if (null != node.getRight()) {
      potentialHolder = Map.entry(node, node.getRight());
      node.getRight().accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTNotEqualsExpression node) {
    if (null != node.getLeft()) {
      potentialHolder = Map.entry(node, node.getLeft());
      node.getLeft().accept(getTraverser());
    }
    if (null != node.getRight()) {
      potentialHolder = Map.entry(node, node.getRight());
      node.getRight().accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTBooleanAndOpExpression node) {
    if (null != node.getLeft()) {
      potentialHolder = Map.entry(node, node.getLeft());
      node.getLeft().accept(getTraverser());
    }
    if (null != node.getRight()) {
      potentialHolder = Map.entry(node, node.getRight());
      node.getRight().accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTBooleanOrOpExpression node) {
    if (null != node.getLeft()) {
      potentialHolder = Map.entry(node, node.getLeft());
      node.getLeft().accept(getTraverser());
    }
    if (null != node.getRight()) {
      potentialHolder = Map.entry(node, node.getRight());
      node.getRight().accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTConditionalExpression node) {
    if (null != node.getCondition()) {
      potentialHolder = Map.entry(node, node.getCondition());
      node.getCondition().accept(getTraverser());
    }
    if (null != node.getTrueExpression()) {
      potentialHolder = Map.entry(node, node.getTrueExpression());
      node.getTrueExpression().accept(getTraverser());
    }
    if (null != node.getFalseExpression()) {
      potentialHolder = Map.entry(node, node.getFalseExpression());
      node.getFalseExpression().accept(getTraverser());
    }
  }

  @Override
  public void visit(ASTBracketExpression node) {
    potentialHolder = Map.entry(node, node.getExpression());
  }

}
