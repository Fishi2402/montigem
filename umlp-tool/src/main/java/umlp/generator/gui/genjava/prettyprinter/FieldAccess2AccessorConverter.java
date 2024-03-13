package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.Collection;

public class FieldAccess2AccessorConverter extends CommonExpressionsPrettyPrinter {

  protected final Collection<ASTExpression> ignore;

  public FieldAccess2AccessorConverter(
      IndentPrinter printer, boolean printComments,
      Collection<ASTExpression> ignore
  ) {
    super(printer, printComments);
    this.ignore = ignore;
  }

  @Override
  public void handle(ASTCallExpression node) {
    // ignore field and name expressions that are direct children of ASTCallExpression
    ignore.add(node.getExpression());
    super.handle(node);
  }

  @Override
  public void handle(ASTFieldAccessExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getExpression().accept(getTraverser());

    getPrinter().stripTrailing();
    getPrinter().print(".");

    if (ignore.contains(node)) {
      getPrinter().print(node.getName() + " ");
    }
    else {
      getPrinter().print(attr2Getter(node) + " ");
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }

  }

  protected String attr2Getter(ASTFieldAccessExpression node) {
    if (GUIGeneratorUtil.getInstance().isEnumMember(node)) {
      return node.getName();
    }
    SymTypeExpression ste = GUIGeneratorUtil.getInstance().getType(node);

    if (SymTypeRelations.isBoolean(ste)) {
      return "is" + StringTransformations.capitalize(node.getName()) + "()";
    }
    return "get" + StringTransformations.capitalize(node.getName()) + "()";
  }

}
