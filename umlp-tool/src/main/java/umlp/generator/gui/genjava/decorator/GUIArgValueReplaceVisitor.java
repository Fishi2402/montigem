/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanAndOpExpression;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanNotExpression;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanOrOpExpression;
import de.monticore.expressions.commonexpressions._ast.ASTBracketExpression;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
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
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIArgValue;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._symboltable.BuiltInTypes;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.types.check.SymTypeExpression;
import umlp.generator.gui.ExpressionReplaceSearchVisitor;
import umlp.generator.gui.ExpressionReplaceVisitor;
import umlp.generator.gui.ExpressionReplacement;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class GUIArgValueReplaceVisitor extends ExpressionReplaceSearchVisitor {

  protected GUIDSLTraverser traverser;
  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  protected ASTGUIArgValue result;

  protected List<ExpressionReplacement> replacements = new ArrayList<>();

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

  public ASTExpression getResult() {
    return result.getExpression();
  }

  public boolean argNeedsGetter() {
    return needsGetter;
  }

  public boolean argNeedsOwnParamWithInit() {
    return needsOwnParam;
  }

  public void transform(SymTypeExpression ste, ASTGUINamedArg arg) {
    String type = ste.getTypeInfo().getName();

    if (util.isViewElement(ste)
        || util.isViewElementsCollection(ste)
        || type.equals(BuiltInTypes.EVENT.getName())) {
      return;
    }

    needsGetter = true;

    // TODO AGe: also when a constructor like call is present
    if (arg.getGUIArgValue().isPresentGUIList()) {
      needsOwnParam = true;
      return;
    }

    if (arg.getGUIArgValue().isPresentExpression()) {
      result = arg.getGUIArgValue().deepClone();
      arg.accept(getTraverser());
      for (ExpressionReplacement nr : replacements) {
        ExpressionReplaceVisitor visitor = new ExpressionReplaceVisitor();
        visitor.transform(nr.getHolder(), nr.getOldValue(), nr.getNewValue());
      }
    }
  }

  @Override
  public void visit(ASTNameExpression node) {
    if (potentialHolder != null) {
      if (potentialHolder.getValue().equals(node)) {
        Supplier<ASTExpression> newValue = () -> CommonExpressionsMill.callExpressionBuilder()
            .setExpression(
                ExpressionsBasisMill.nameExpressionBuilder()
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
  }

  @Override
  public void visit(ASTFieldAccessExpression node) {
    if (potentialHolder != null) {
      if (potentialHolder.getValue().equals(node)) {
        Supplier<ASTExpression> newValue = () -> CommonExpressionsMill.callExpressionBuilder()
            .setExpression(
                ExpressionsBasisMill.nameExpressionBuilder()
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
