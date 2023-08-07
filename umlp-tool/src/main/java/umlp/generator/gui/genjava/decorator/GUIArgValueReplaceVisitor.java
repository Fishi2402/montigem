/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIArgValue;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.types.check.SymTypeExpression;
import umlp.generator.gui.ExpressionReplaceVisitor;
import umlp.generator.gui.ExpressionReplacement;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

public class GUIArgValueReplaceVisitor extends GUINameExpressionReplaceVisitor {

  protected GUIDSLTraverser traverser;
  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  protected ASTGUIArgValue getterExpression;
  protected Optional<ASTGUIArgValue> setterExpression = Optional.empty();

  protected Optional<ExpressionReplacement> setterReplacement = Optional.empty();
  protected boolean usesBuilder = false;

  protected boolean needsGetter = false;
  protected boolean needsOwnParam = false;

  public GUIArgValueReplaceVisitor() {
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
    return getterExpression.getExpression();
  }

  public Optional<ASTExpression> getNullCheckExpression() {
    return nullCheckExpression;
  }

  public Optional<ASTExpression> getSetterExpression() {
    return setterExpression.map(ASTGUIArgValue::getExpression);
  }

  public boolean argNeedsGetter() {
    return needsGetter;
  }

  public boolean argNeedsOwnParamWithInit() {
    return needsOwnParam;
  }

  public void setIgnore(Collection<String> ignore) {
    this.ignore = ignore;
  }

  public void setUsesBuilder(boolean usesBuilder) {
    this.usesBuilder = usesBuilder;
  }

  public void transform(SymTypeExpression ste, ASTGUINamedArg arg) {
    if (!util.hasGetter(ste)) {
      return;
    }

    needsGetter = true;

    // TODO AGe: also when a constructor like call is present
    if (arg.getGUIArgValue().isPresentGUIList()) {
      needsOwnParam = true;
      return;
    }

    if (arg.getGUIArgValue().isPresentExpression()) {
      getterExpression = arg.getGUIArgValue().deepClone();
      getterExpression.accept(getTraverser());
      Collections.reverse(replacements);
      for (ExpressionReplacement nr : replacements) {
        ExpressionReplaceVisitor visitor = new ExpressionReplaceVisitor();
        visitor.transform(nr.getHolder(), nr.getOldValue(), nr.getNewValue());
      }

      replacements = new ArrayList<>();

      setterExpression = Optional.of(arg.getGUIArgValue().deepClone());
      setterExpression.get().accept(getTraverser());
      Collections.reverse(replacements);
      for (int i = 0; i < replacements.size(); ++i) {
        ExpressionReplacement nr = replacements.get(i);
        if (i == replacements.size() - 1 && setterReplacement.isPresent()) {
          nr = setterReplacement.get();
        }
        ExpressionReplaceVisitor visitor = new ExpressionReplaceVisitor();
        visitor.transform(nr.getHolder(), nr.getOldValue(), nr.getNewValue());
      }
    }
  }

  @Override
  public void visit(ASTFieldAccessExpression node) {
    if (potentialHolder != null) {
      if (potentialHolder.getValue().equals(node)) {
        if (potentialHolder.getKey() instanceof ASTGUIArgValue) {
          Supplier<ASTExpression> newValue = () -> CommonExpressionsMill.fieldAccessExpressionBuilder()
              .setExpression(node.getExpression())
              .setName(usesBuilder
                  ? util.gui2cdAttrBuilderSetterName(node.getName())
                  : util.gui2cdAttrSetterName(node.getName()))
              .build();
          setterReplacement = Optional.of(new ExpressionReplacement(
              potentialHolder.getKey(), potentialHolder.getValue(), newValue));
        }
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
