/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.ast.ASTNode;
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
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIArgValue;
import de.monticore.guidsl._ast.ASTGUIBlockContent;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIRangeIterateControl;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;

import java.util.function.Supplier;

public class ExpressionReplaceVisitor implements GUIDSLVisitor2, CommonExpressionsVisitor2 {

  protected ASTNode holder;
  protected ASTNode oldValue;
  protected Supplier<ASTExpression> newValue;
  protected GUIDSLTraverser traverser;

  public ExpressionReplaceVisitor() {
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    traverser.add4CommonExpressions(this);
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  public void transform(ASTNode holder, ASTNode oldValue, Supplier<ASTExpression> newValue) {
    holder.accept(getTraverser());
    this.holder = holder;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  @Override
  public void endVisit(ASTGUIArgValue node) {
    if (node.equals(holder)) {
      node.setExpression(newValue.get());
    }
  }

  @Override
  public void endVisit(ASTGUIBlockContent node) {
    if (node.equals(holder)) {
      node.setExpression(newValue.get());
    }
  }

  @Override
  public void endVisit(ASTGUIGuard node) {
    if (node.equals(holder)) {
      node.setCondition(newValue.get());
    }
  }

  @Override
  public void endVisit(ASTGUIShortIterateControl node) {
    if (node.equals(holder)) {
      node.setIterable(newValue.get());
    }
  }

  @Override
  public void endVisit(ASTGUIRangeIterateControl node) {
    if (node.equals(holder)) {
      if (node.getStart().equals(oldValue)) {
        node.setStart(newValue.get());
      }
      if (node.getEnd().equals(oldValue)) {
        node.setEnd(newValue.get());
      }
    }
  }

  @Override
  public void endVisit(ASTFieldAccessExpression node) {
    if (node.equals(holder)) {
      node.setExpression(newValue.get());
    }
  }

  @Override
  public void endVisit(ASTPlusPrefixExpression node) {
    if (node.equals(holder)) {
      node.setExpression(newValue.get());
    }
  }

  @Override
  public void endVisit(ASTMinusPrefixExpression node) {
    if (node.equals(holder)) {
      node.setExpression(newValue.get());
    }
  }

  @Override
  public void endVisit(ASTBooleanNotExpression node) {
    if (node.equals(holder)) {
      node.setExpression(newValue.get());
    }
  }

  @Override
  public void endVisit(ASTLogicalNotExpression node) {
    if (node.equals(holder)) {
      node.setExpression(newValue.get());
    }
  }

  @Override
  public void endVisit(ASTMinusExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue.get());
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue.get());
      }
    }
  }

  @Override
  public void endVisit(ASTMultExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue.get());
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue.get());
      }
    }
  }

  @Override
  public void endVisit(ASTDivideExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue.get());
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue.get());
      }
    }
  }

  @Override
  public void endVisit(ASTModuloExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue.get());
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue.get());
      }
    }
  }

  @Override
  public void endVisit(ASTPlusExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue.get());
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue.get());
      }
    }
  }

  @Override
  public void endVisit(ASTLessEqualExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue.get());
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue.get());
      }
    }
  }

  @Override
  public void endVisit(ASTGreaterEqualExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue.get());
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue.get());
      }
    }
  }

  @Override
  public void endVisit(ASTLessThanExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue.get());
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue.get());
      }
    }
  }

  @Override
  public void endVisit(ASTGreaterThanExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue.get());
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue.get());
      }
    }
  }

  @Override
  public void endVisit(ASTEqualsExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue.get());
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue.get());
      }
    }
  }

  @Override
  public void endVisit(ASTNotEqualsExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue.get());
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue.get());
      }
    }
  }

  @Override
  public void endVisit(ASTBooleanAndOpExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue.get());
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue.get());
      }
    }
  }

  @Override
  public void endVisit(ASTBooleanOrOpExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue.get());
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue.get());
      }
    }
  }

  @Override
  public void endVisit(ASTConditionalExpression node) {
    if (node.equals(holder)) {
      if (node.getCondition().equals(oldValue)) {
        node.setCondition(newValue.get());
      }
      if (node.getTrueExpression().equals(oldValue)) {
        node.setTrueExpression(newValue.get());
      }
      if (node.getFalseExpression().equals(oldValue)) {
        node.setFalseExpression(newValue.get());
      }
    }
  }

  @Override
  public void endVisit(ASTBracketExpression node) {
    if (node.equals(holder)) {
      node.setExpression(newValue.get());
    }
  }
}
