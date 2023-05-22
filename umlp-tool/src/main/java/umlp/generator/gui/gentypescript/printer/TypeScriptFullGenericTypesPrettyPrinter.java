package umlp.generator.gui.gentypescript.printer;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcfullgenerictypes._prettyprint.MCFullGenericTypesPrettyPrinter;

public class TypeScriptFullGenericTypesPrettyPrinter extends MCFullGenericTypesPrettyPrinter {
  public TypeScriptFullGenericTypesPrettyPrinter(IndentPrinter printer) {
    super(printer, false);
  }

  @Override
  public void handle(de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().print("T ");
    if (node.isPresentUpperBound()) { // opt: 0 req: 1
      if (node.isPresentUpperBound()) { // opt: 0 req: 1
        getPrinter().print("extends ");
        node.getUpperBound().accept(getTraverser());
      }
    }
    else if (node.isPresentLowerBound()) { // opt: 0 req: 1
      getPrinter().print("any");
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}