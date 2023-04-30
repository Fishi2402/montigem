package umlp.generator.gui;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

public class ExpressionReplacement {
  protected ASTNode holder;
  protected ASTExpression oldValue;
  protected ASTExpression newValue;

  public ExpressionReplacement(ASTNode holder, ASTExpression oldValue, ASTExpression newValue) {
    this.holder = holder;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public ASTNode getHolder() {
    return holder;
  }

  public ASTExpression getOldValue() {
    return oldValue;
  }

  public ASTExpression getNewValue() {
    return newValue;
  }
}
