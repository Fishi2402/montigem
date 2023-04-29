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

public class GUIShortIterateItemReplaceVisitor implements GUIDSLVisitor2, CommonExpressionsVisitor2 {

  protected ASTNode holder;
  protected ASTNode oldValue;
  protected ASTExpression newValue;
  protected GUIDSLTraverser traverser;

  public GUIShortIterateItemReplaceVisitor() {
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    traverser.add4CommonExpressions(this);
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  public void transform(ASTNode holder, ASTExpression newValue, ASTNode oldValue) {
    holder.accept(getTraverser());
    this.holder = holder;
    this.newValue = newValue;
    this.oldValue = oldValue;
  }

  @Override
  public void visit(ASTGUIArgValue node) {
    if (node.equals(holder)) {
      node.setExpression(newValue);
    }
  }

  @Override
  public void visit(ASTGUIBlockContent node) {
    if (node.equals(holder)) {
      node.setExpression(newValue);
    }
  }

  @Override
  public void visit(ASTGUIGuard node) {
    if (node.equals(holder)) {
      node.setCondition(newValue);
    }
  }

  @Override
  public void visit(ASTGUIShortIterateControl node) {
    if (node.equals(holder)) {
      node.setIterable(newValue);
    }
  }

  @Override
  public void visit(ASTGUIRangeIterateControl node) {
    if (node.equals(holder)) {
      if (node.getStart().equals(oldValue)) {
        node.setStart(newValue);
      }
      if (node.getEnd().equals(oldValue)) {
        node.setEnd(newValue);
      }
    }
  }

  @Override
  public void visit(ASTFieldAccessExpression node) {
    if (node.equals(holder)) {
      node.setExpression(newValue);
    }
  }

  @Override
  public void visit(ASTPlusPrefixExpression node) {
    if (node.equals(holder)) {
      node.setExpression(newValue);
    }
  }

  @Override
  public void visit(ASTMinusPrefixExpression node) {
    if (node.equals(holder)) {
      node.setExpression(newValue);
    }
  }

  @Override
  public void visit(ASTBooleanNotExpression node) {
    if (node.equals(holder)) {
      node.setExpression(newValue);
    }
  }

  @Override
  public void visit(ASTLogicalNotExpression node) {
    if (node.equals(holder)) {
      node.setExpression(newValue);
    }
  }

  @Override
  public void visit(ASTMinusExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue);
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue);
      }
    }
  }

  @Override
  public void visit(ASTMultExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue);
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue);
      }
    }
  }

  @Override
  public void visit(ASTDivideExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue);
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue);
      }
    }
  }

  @Override
  public void visit(ASTModuloExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue);
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue);
      }
    }
  }

  @Override
  public void visit(ASTPlusExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue);
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue);
      }
    }
  }

  @Override
  public void visit(ASTLessEqualExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue);
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue);
      }
    }
  }

  @Override
  public void visit(ASTGreaterEqualExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue);
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue);
      }
    }
  }

  @Override
  public void visit(ASTLessThanExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue);
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue);
      }
    }
  }

  @Override
  public void visit(ASTGreaterThanExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue);
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue);
      }
    }
  }

  @Override
  public void visit(ASTEqualsExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue);
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue);
      }
    }
  }

  @Override
  public void visit(ASTNotEqualsExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue);
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue);
      }
    }
  }

  @Override
  public void visit(ASTBooleanAndOpExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue);
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue);
      }
    }
  }

  @Override
  public void visit(ASTBooleanOrOpExpression node) {
    if (node.equals(holder)) {
      if (node.getLeft().equals(oldValue)) {
        node.setLeft(newValue);
      }
      if (node.getRight().equals(oldValue)) {
        node.setRight(newValue);
      }
    }
  }

  @Override
  public void visit(ASTConditionalExpression node) {
    if (node.equals(holder)) {
      if (node.getTrueExpression().equals(oldValue)) {
        node.setTrueExpression(newValue);
      }
      if (node.getFalseExpression().equals(oldValue)) {
        node.setFalseExpression(newValue);
      }
    }
  }

  @Override
  public void visit(ASTBracketExpression node) {
    if (node.equals(holder)) {
      node.setExpression(newValue);
    }
  }
}
