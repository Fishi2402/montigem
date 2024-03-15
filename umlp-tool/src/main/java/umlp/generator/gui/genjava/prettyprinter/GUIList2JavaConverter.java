package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.guidsl._ast.ASTGUIArgValue;
import de.monticore.guidsl._ast.ASTGUIList;
import de.monticore.guidsl._prettyprint.GUIDSLPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Iterator;

public class GUIList2JavaConverter extends GUIDSLPrettyPrinter {

  public GUIList2JavaConverter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTGUIList node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    Iterator<ASTGUIArgValue> iter_gUIArgValue = node.getGUIArgValueList().iterator();

    getPrinter().stripTrailing();
    getPrinter().print("List.of(");

    if (iter_gUIArgValue.hasNext()) {
      iter_gUIArgValue.next().accept(getTraverser());
      while (iter_gUIArgValue.hasNext()) {
        getPrinter().stripTrailing();
        getPrinter().print(",");
        iter_gUIArgValue.next().accept(getTraverser());
      }
    }

    getPrinter().stripTrailing();
    getPrinter().print(")");

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

}
