package umlp.generator.gui.gentypescript.printer;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._prettyprint.MCSimpleGenericTypesPrettyPrinter;

import java.util.Iterator;

public class TypeScriptSimpleGenericTypesPrettyPrinter extends MCSimpleGenericTypesPrettyPrinter {
  public TypeScriptSimpleGenericTypesPrettyPrinter(IndentPrinter printer) {
    super(printer, false);
  }

  @Override
  public void handle(ASTMCBasicGenericType node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    Iterator<ASTMCTypeArgument> iter_mCTypeArgument = node.getMCTypeArgumentList().iterator();

    if (!node.getNameList().isEmpty()) {
      String last = node.getName(node.sizeNames() - 1);
      // java.util.Optional and java.util.List land here
      if (last.equals(TypeScriptTypes.CONSUMER.getMCType())) {
        getPrinter().print("(arg: any) => void");
      }
      if (!last.equals("Optional")
          && !last.equals(TypeScriptTypes.CONSUMER.getMCType())
      ) {
        getPrinter().print(TypeScriptTypes.toTSType(last));
        getPrinter().stripTrailing();
        getPrinter().print("<");
        if (iter_mCTypeArgument.hasNext()) {
          iter_mCTypeArgument.next().accept(getTraverser());
          while (iter_mCTypeArgument.hasNext()) {
            getPrinter().stripTrailing();
            getPrinter().print(",");
            iter_mCTypeArgument.next().accept(getTraverser());
          }
        }
        getPrinter().stripTrailing();
        getPrinter().print(">");
      }
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}