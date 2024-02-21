package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.guidsl._symboltable.IGUIDSLScope;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.Collection;
import java.util.Optional;

public class Name2AccessorConverter extends ExpressionsBasisPrettyPrinter {

  protected final Collection<ASTExpression> ignore;

  public Name2AccessorConverter(
      IndentPrinter printer,
      boolean printComments,
      Collection<ASTExpression> ignore
  ) {
    super(printer, printComments);
    this.ignore = ignore;
  }

  @Override
  public void handle(ASTNameExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    if (ignore.contains(node)) {
      getPrinter().print("this." + node.getName() + " ");
    }
    else {
      getPrinter().print("this." + attr2Getter(node) + " ");
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  protected String attr2Getter(ASTNameExpression node) {
    if (isEnum(node)) {
      return node.getName();
    }
    SymTypeExpression ste = GUIGeneratorUtil.getInstance().getType(node);

    if (SymTypeRelations.isBoolean(ste)) {
      return "is" + StringTransformations.capitalize(node.getName()) + "()";
    }
    return "get" + StringTransformations.capitalize(node.getName()) + "()";
  }

  protected String attr2Getter(ASTNameExpression node, Collection<String> ignore) {
    if (ignore.contains(node.getName())) {
      return node.getName();
    }
    return attr2Getter(node);
  }

  protected boolean isEnum(ASTNameExpression node) {
    if (node.getEnclosingScope() instanceof IGUIDSLScope) {
      Optional<OOTypeSymbol> sym = ((IGUIDSLScope) node.getEnclosingScope())
          .resolveOOType(node.getName());
      return sym.isPresent() && sym.get().isIsEnum();
    }
    else {
      Log.error("0x0000C Attempted to resolve type of '"
          + GUIGeneratorUtil.getInstance().print(node) + "' outside of GUI model context,"
          + " but expression conversion is restricted to GUI models.");
      return false;
    }
  }

}
