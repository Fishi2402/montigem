package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.Collection;

public class Name2BuilderConverter extends Name2AccessorConverter {

  protected SharedBoolean lastFieldAccess;

  public Name2BuilderConverter(
      IndentPrinter printer, boolean printComments,
      Collection<ASTExpression> ignore, Collection<ASTExpression> nonClassMembers,
      SharedBoolean lastFieldAccess
  ) {
    super(printer, printComments, ignore, nonClassMembers);
    this.lastFieldAccess = lastFieldAccess;
  }

  @Override
  public void handle(ASTNameExpression node) {
    if (lastFieldAccess.getValue()) {
      errorIfExpressionIsFirst(node);
    }
    else {
      super.handle(node);
    }
  }

  @Override
  public void handle(ASTLiteralExpression node) {
    errorIfExpressionIsFirst(node);
  }

  protected void errorIfExpressionIsFirst(ASTExpression node) {
    if (lastFieldAccess.getValue()) {
      Log.error("0x0000D Expected FieldAccessExpression but got '"
          + GUIGeneratorUtil.getInstance().print(node)
          + "'. " + this.getClass().getSimpleName() + " cannot produce setter.");
    }
  }

}
