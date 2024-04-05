package umlp.generator.gui.genjava.decorator;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIArgValue;
import de.monticore.guidsl._ast.ASTGUIBlockContent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIList;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.guidsl._symboltable.BuiltInTypes;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import groovyjarjarantlr.collections.AST;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.Optional;

public class CallTypeDeriver implements GUIDSLVisitor2 {

  protected GUIDSLTraverser traverser;

  protected final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();
  protected static CallTypeDeriver instance;

  protected ASTCallExpression call;
  protected Optional<SymTypeExpression> result = Optional.empty();

  public static CallTypeDeriver getInstance() {
    if (instance == null) {
      instance = new CallTypeDeriver();
    }
    return instance;
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  private CallTypeDeriver() {
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
  }

  public void init(ASTGUIComponentDeclaration node, ASTCallExpression call) {
    if (node.isPresentGUIBlock()) {
      this.call = call;
      node.getGUIBlock().accept(getTraverser());
    }
  }

  public boolean isPresentResult() {
    return result.isPresent();
  }

  public SymTypeExpression getResult() {
    return result.get();
  }

  @Override
  public void visit(ASTGUIParam node) {
    if (node.isPresentDefault() && node.getDefault().equals(call)) {
      if (!node.isPresentSymbol()) {
        Log.error("0x0000E Unable to find symbol for '" + node.getName()
            + "' at " + node.get_SourcePositionStart().toString()
            + " - " + node.get_SourcePositionEnd().toString()
        );
      }
      result = Optional.of(node.getSymbol().getType());
    }
  }

  @Override
  public void visit(ASTGUIBlockContent node) {
    if (node.isPresentExpression() && node.getExpression().equals(call)) {
      result = Optional.of(SymTypeExpressionFactory.createTypeObject(
          BuiltInTypes.GUI_VIEW_ELEMENT.getQName(), GUIDSLMill.globalScope()
      ));
    }
  }

  @Override
  public void visit(ASTGUINamedArg node) {
    if (node.getGUIArgValue().isPresentExpression()
        && node.getGUIArgValue().getExpression().equals(call)) {
      if (!node.isPresentNameSymbol()) {
        Log.error("0x0000E Unable to find symbol for '" + node.getName()
            + "' at " + node.get_SourcePositionStart().toString()
            + " - " + node.get_SourcePositionEnd().toString()
        );
      }
      result = Optional.of(node.getNameSymbol().getType());
    }
  }

  @Override
  public void visit(ASTGUIGuard node) {
    if (node.getCondition().equals(call)) {
      result = Optional.of(SymTypeExpressionFactory.createTypeObject(
          BuiltInTypes.BOOLEAN_BOXED.getQName(), GUIDSLMill.globalScope()
      ));
    }
  }

  @Override
  public void visit(ASTGUIShortIterateControl node) {
    if (node.getIterable().equals(call)) {
      if (node.isPresentMCType()) {
        if (!node.getItem().isPresentSymbol()) {
          Log.error("0x0000E Unable to find symbol for '" + node.getItem().getName()
              + "' at " + node.getItem().get_SourcePositionStart().toString()
              + " - " + node.getItem().get_SourcePositionEnd().toString()
          );
        }
        result = Optional.of(SymTypeExpressionFactory.createGenerics(
            BuiltInTypes.LIST.getQName(),
            GUIDSLMill.globalScope(),
            node.getItem().getSymbol().getType()
        ));
      }
      else {
        result = Optional.of(SymTypeExpressionFactory.createTypeObject(
            BuiltInTypes.LIST.getQName(), GUIDSLMill.globalScope()
        ));
      }
    }
  }

}
