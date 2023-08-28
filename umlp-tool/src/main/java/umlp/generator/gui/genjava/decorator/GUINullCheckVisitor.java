/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GUINullCheckVisitor implements ExpressionsBasisVisitor2, CommonExpressionsVisitor2, CommonExpressionsHandler {

  protected Optional<ASTExpression> nullCheckExpression = Optional.empty();

  protected GUIDSLTraverser traverser;
  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  protected boolean skipNext = true;
  List<ASTExpression> checks = new ArrayList<>();

  public GUINullCheckVisitor() {
    traverser = GUIDSLMill.traverser();
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

  public Optional<ASTExpression> getNullCheckExpression() {
    return nullCheckExpression;
  }

  public void transform(ASTExpression exp) {
    ASTExpression clone = exp.deepClone();
    clone.accept(getTraverser());

    Collections.reverse(checks);
    for (ASTExpression e : checks) {
      ASTExpression check = CommonExpressionsMill
          .equalsExpressionBuilder()
          .setOperator("==")
          .setLeft(e)
          .setRight(CommonExpressionsMill.literalExpressionBuilder().setLiteral(
              MCCommonLiteralsMill.nullLiteralBuilder().build()).build())
          .build();
      if (nullCheckExpression.isPresent()) {
        nullCheckExpression = Optional.of(
            CommonExpressionsMill.booleanOrOpExpressionBuilder()
                .setOperator("||")
                .setLeft(nullCheckExpression.get())
                .setRight(check)
                .build());
      } else {
        nullCheckExpression = Optional.of(check);
      }
    }
  }

  @Override
  public void visit(ASTFieldAccessExpression node) {
    if (skipNext) {
      skipNext = false;
      return;
    }
    checks.add(node.deepClone());
  }

  @Override
  public void visit(ASTCallExpression node) {
    if (skipNext) {
      return;
    }
    checks.add(node.deepClone());
    skipNext = true;
  }

}
