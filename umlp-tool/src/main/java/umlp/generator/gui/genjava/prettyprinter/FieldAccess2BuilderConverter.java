package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Collection;

import static umlp.generator.gui.genjava.prettyprinter.Expression2SetterPrinter.SETTER_PARAM;

public class FieldAccess2BuilderConverter extends FieldAccess2MutatorConverter {

  public FieldAccess2BuilderConverter(
      IndentPrinter printer, boolean printComments,
      Collection<ASTExpression> ignore, SharedBoolean lastFieldAccess
  ) {
    super(printer, printComments, ignore, lastFieldAccess);
  }

  @Override
  protected String attr2Setter(ASTFieldAccessExpression node) {
    return node.getName() + "(" + SETTER_PARAM + ")";
  }

}
