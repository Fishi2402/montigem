package umlp.generator.gui.coco;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.literals.mccommonliterals._ast.ASTNullLiteral;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor2;

import java.util.Optional;

public class ClosestNameOrFieldAccessFinder
    implements GUIDSLVisitor2, CommonExpressionsVisitor2,
    ExpressionsBasisVisitor2, MCCommonLiteralsVisitor2 {

  protected GUIDSLTraverser traverser;

  protected boolean stopChecking = false;
  protected Optional<ASTLiteralExpression> nullExp = Optional.empty();
  protected Optional<ASTNameExpression> nameExp = Optional.empty();
  protected Optional<ASTFieldAccessExpression> fieldAccessExp = Optional.empty();

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  public ClosestNameOrFieldAccessFinder() {
    traverser = GUIDSLMill.traverser();
    traverser.add4CommonExpressions(this);
    traverser.add4ExpressionsBasis(this);
  }

  public Optional<ASTFieldAccessExpression> getFieldAccessExp() {
    return fieldAccessExp;
  }

  public Optional<ASTNameExpression> getNameExp() {
    return nameExp;
  }

  public Optional<ASTLiteralExpression> getNullExp() {
    return nullExp;
  }

  @Override
  public void visit(ASTLiteralExpression node) {
    if (!stopChecking && node.getLiteral() instanceof ASTNullLiteral) {
      this.nullExp = Optional.of(node);
      stopChecking = true;
    }
  }

  @Override
  public void visit(ASTNameExpression node) {
    if (!stopChecking) {
      this.nameExp = Optional.of(node);
      stopChecking = true;
    }
  }

  @Override
  public void visit(ASTFieldAccessExpression node) {
    if (!stopChecking) {
      this.fieldAccessExp = Optional.of(node);
      stopChecking = true;
    }
  }
}
