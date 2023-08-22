package umlp.generator.gui.genjava.decorator;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import umlp.generator.gui.ExpressionReplaceSearchVisitor;
import umlp.generator.gui.ExpressionReplacement;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

public abstract class GUINameExpressionReplaceVisitor extends ExpressionReplaceSearchVisitor {
  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();
  protected List<ExpressionReplacement> replacements = new ArrayList<>();
  protected Collection<String> ignore = new HashSet<>();

  @Override
  public void visit(ASTNameExpression node) {
    if (potentialHolder != null) {
      if (potentialHolder.getValue().equals(node) && !ignore.contains(node.getName())) {
        String getterName = util.gui2cdAttrGetterName(node.getName());

        // replace ${field} by this.${field}
        Supplier<ASTExpression> accessorValue = createLocalGetterSupplier(getterName);

        replacements.add(new ExpressionReplacement(
            potentialHolder.getKey(), potentialHolder.getValue(), accessorValue));
      }
    }
  }

  protected Supplier<ASTExpression> createLocalGetterSupplier(String methodName) {
    return () -> CommonExpressionsMill.callExpressionBuilder()
        .setExpression(CommonExpressionsMill.fieldAccessExpressionBuilder()
            .setName(methodName)
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
  }
}
