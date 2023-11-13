/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

import java.util.function.Supplier;

public class ExpressionReplacement {
  protected ASTNode holder;
  protected ASTExpression oldValue;
  protected Supplier<ASTExpression> newValue;

  public ExpressionReplacement(ASTNode holder, ASTExpression oldValue, Supplier<ASTExpression> newValue) {
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

  public Supplier<ASTExpression> getNewValue() {
    return newValue;
  }
}
