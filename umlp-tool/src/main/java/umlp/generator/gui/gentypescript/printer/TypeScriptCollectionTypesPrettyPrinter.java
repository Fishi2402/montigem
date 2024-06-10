package umlp.generator.gui.gentypescript.printer;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._prettyprint.MCCollectionTypesPrettyPrinter;

public class TypeScriptCollectionTypesPrettyPrinter extends MCCollectionTypesPrettyPrinter {
  public TypeScriptCollectionTypesPrettyPrinter(IndentPrinter printer) {
    super(printer, false);
  }

  public void handle(de.monticore.types.mccollectiontypes._ast.ASTMCListType node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().print("Array ");
    getPrinter().stripTrailing();
    getPrinter().print("<");
    node.getMCTypeArgument().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(">");
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getMCTypeArgument().accept(getTraverser());
    getPrinter().stripTrailing();
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.types.mccollectiontypes._ast.ASTMCSetType node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().print("Set ");
    getPrinter().stripTrailing();
    getPrinter().print("<");
    node.getMCTypeArgument().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(">");
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}