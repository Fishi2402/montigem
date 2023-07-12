/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import umlp.generator.gui.ExpressionReplaceSearchVisitor;
import umlp.generator.gui.ExpressionReplaceVisitor;
import umlp.generator.gui.ExpressionReplacement;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class GUIGuardConditionReplaceVisitor extends ExpressionReplaceSearchVisitor {

  protected GUIDSLTraverser traverser;
  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  protected ASTGUIGuard getterExpression;
  protected Optional<ASTExpression> nullCheckExpression = Optional.empty();
  protected List<ExpressionReplacement> replacements = new ArrayList<>();

  protected Collection<String> ignore = new HashSet<>();

  public GUIGuardConditionReplaceVisitor() {
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

  public ASTExpression getGetterExpression() {
    return getterExpression.getCondition();
  }

  public Optional<ASTExpression> getNullCheckExpression() {
    return nullCheckExpression;
  }

  public void setIgnore(Collection<String> ignore) {
    this.ignore = ignore;
  }

  public void transform(ASTGUIGuard guard) {
    getterExpression = guard.deepClone();

    potentialHolder = Map.entry(getterExpression, getterExpression.getCondition());
    getterExpression.getCondition().accept(getTraverser());
    Collections.reverse(replacements);
    for (ExpressionReplacement nr : replacements) {
      ExpressionReplaceVisitor visitor = new ExpressionReplaceVisitor();
      visitor.transform(nr.getHolder(), nr.getOldValue(), nr.getNewValue());
    }

    replacements = new ArrayList<>();

    Collections.reverse(replacements);
    for (int i = 0; i < replacements.size(); ++i) {
      ExpressionReplacement nr = replacements.get(i);
      ExpressionReplaceVisitor visitor = new ExpressionReplaceVisitor();
      visitor.transform(nr.getHolder(), nr.getOldValue(), nr.getNewValue());
    }
  }

  @Override
  public void visit(ASTNameExpression node) {
    if (potentialHolder != null) {
      if (potentialHolder.getValue().equals(node) && !ignore.contains(node.getName())) {
        Supplier<ASTExpression> newValue = () -> CommonExpressionsMill.callExpressionBuilder()
            .setExpression(CommonExpressionsMill.fieldAccessExpressionBuilder()
                .setName(util.gui2cdAttrGetterName(node.getName()))
                .setExpression(
                    ExpressionsBasisMill.nameExpressionBuilder()
                        // TODO AGe: technically java "this" expression
                        .setName("this")
                        .build())
                .build()
            )
            .setArguments(
                ExpressionsBasisMill.argumentsBuilder()
                    .setExpressionsList(Collections.emptyList())
                    .build()
            )
            .build();
        nullCheckExpression = Optional.of(newValue.get());
        replacements.add(new ExpressionReplacement(
            potentialHolder.getKey(), potentialHolder.getValue(), newValue));
      }
    }
  }

  @Override
  public void visit(ASTFieldAccessExpression node) {
    if (potentialHolder != null) {
      if (potentialHolder.getValue().equals(node)) {
        Supplier<ASTExpression> newValue = () -> CommonExpressionsMill.callExpressionBuilder()
            .setExpression(
                CommonExpressionsMill.fieldAccessExpressionBuilder()
                    .setExpression(node.getExpression())
                    .setName(util.gui2cdAttrGetterName(node.getName()))
                    .build()
            )
            .setArguments(
                ExpressionsBasisMill.argumentsBuilder()
                    .setExpressionsList(Collections.emptyList())
                    .build()
            )
            .build();
        replacements.add(new ExpressionReplacement(
            potentialHolder.getKey(), potentialHolder.getValue(), newValue));
      }
    }
    super.visit(node);
  }

}
