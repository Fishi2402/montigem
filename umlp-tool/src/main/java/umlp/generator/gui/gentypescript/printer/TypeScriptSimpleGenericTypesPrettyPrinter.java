package umlp.generator.gui.gentypescript.printer;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcsimplegenerictypes._prettyprint.MCSimpleGenericTypesPrettyPrinter;

public class TypeScriptSimpleGenericTypesPrettyPrinter extends MCSimpleGenericTypesPrettyPrinter {
  public TypeScriptSimpleGenericTypesPrettyPrinter(IndentPrinter printer) {
    super(printer, false);
  }

  @Override
  public void handle(de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    java.util.Iterator<de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> iter_mCTypeArgument = node.getMCTypeArgumentList().iterator();

    if (node.getNameList().size() > 0) {
      String last = node.getName(node.sizeNames() - 1);
      // TODO AGe: some Optionals land here ...
      if (!last.equals("Optional")) {
        if (last.equals("Consumer")) {
          getPrinter().print("(arg: any) => void");
        } else {
          getPrinter().print(TypeScriptTypes.toTSType(last));
          getPrinter().stripTrailing();
          getPrinter().print("<");
        }
      }
      if (iter_mCTypeArgument.hasNext() && !last.equals("Consumer")) {
        iter_mCTypeArgument.next().accept(getTraverser());
        while (iter_mCTypeArgument.hasNext()) {
          getPrinter().stripTrailing();
          getPrinter().print(",");
          iter_mCTypeArgument.next().accept(getTraverser());
        }
      }
      getPrinter().stripTrailing();
      if (!last.equals("Optional") && !last.equals("Consumer")) {
        getPrinter().print(">");
      }
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}