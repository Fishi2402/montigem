package umlp.generator.gui.genjava.decorator;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import umlp.generator.gui.ExpressionReplaceSearchVisitor;
import umlp.generator.gui.ExpressionReplacement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class GUINameExpressionReplaceVisitor extends ExpressionReplaceSearchVisitor {
  protected Optional<ASTExpression> nullCheckExpression = Optional.empty();
  protected List<ExpressionReplacement> replacements = new ArrayList<>();
  protected Collection<String> ignore = new HashSet<>();

  @Override
  public void visit(ASTNameExpression node) {
    if (potentialHolder != null) {
      if (potentialHolder.getValue().equals(node) && !ignore.contains(node.getName())) {
        // Create nullcheck expression: this.get${field}() == null
        Supplier<ASTExpression> newValue = () -> CommonExpressionsMill
            .notEqualsExpressionBuilder()
            .setOperator("!=")
            .setLeft(CommonExpressionsMill.callExpressionBuilder()
                .setExpression(CommonExpressionsMill.fieldAccessExpressionBuilder()
                    .setName(GUIArgValueReplaceVisitor.util.gui2cdAttrGetterName(node.getName()))
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
                .build())
            .setRight(CommonExpressionsMill.literalExpressionBuilder().setLiteral(MCCommonLiteralsMill.nullLiteralBuilder().build()).build())
            .build();
        if (nullCheckExpression.isEmpty()) {
          nullCheckExpression = Optional.of(newValue.get());
        } else {
          ASTExpression oldNullCheck = nullCheckExpression.get();
          ASTExpression newNullCheck = newValue.get();
          nullCheckExpression = Optional.of(
              CommonExpressionsMill.booleanAndOpExpressionBuilder()
                  .setOperator("&&")
                  .setLeft(oldNullCheck)
                  .setRight(newNullCheck)
                  .build()
          );
        }

        // replace ${field} by this.${field}
        Supplier<ASTExpression> accessorValue = () -> CommonExpressionsMill.callExpressionBuilder()
            .setExpression(CommonExpressionsMill.fieldAccessExpressionBuilder()
                .setName(GUIArgValueReplaceVisitor.util.gui2cdAttrGetterName(node.getName()))
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

        replacements.add(new ExpressionReplacement(
            potentialHolder.getKey(), potentialHolder.getValue(), accessorValue));
      }
    }
  }
}
