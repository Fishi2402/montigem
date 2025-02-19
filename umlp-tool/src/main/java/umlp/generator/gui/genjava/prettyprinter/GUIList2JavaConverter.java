package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.ast.ASTNode;
import de.monticore.ocl.setexpressions._ast.ASTSetCollectionItem;
import de.monticore.ocl.setexpressions._ast.ASTSetEnumeration;
import de.monticore.ocl.setexpressions._prettyprint.SetExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.genjava.decorator.Attribute;
import umlp.generator.gui.genjava.decorator.StaticObjectsCollector;

import java.util.Iterator;
import java.util.Map;

public class GUIList2JavaConverter extends SetExpressionsPrettyPrinter {

  public GUIList2JavaConverter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTSetEnumeration node) {
    Map<ASTNode, Attribute> node2Attr = StaticObjectsCollector.getInstance().getNode2Attr();
    // a special case with a list
    if (node2Attr.containsKey(node)) {
      Iterator<VariableSymbol> vs = GUIGeneratorUtil.getInstance()
          .findIterationItemSymbols(node.getEnclosingScope()).iterator();
      getPrinter().print("this.get" + StringTransformations.capitalize(
          node2Attr.get(node).getCdAttr().getName()
      ) + "(");
      if (vs.hasNext()) {
        getPrinter().print(vs.next().getName());
        while (vs.hasNext()) {
          getPrinter().stripTrailing();
          getPrinter().print(",");
          getPrinter().print(vs.next().getName());
        }
      }
      getPrinter().print(")");
    }
    // default printing
    else {
      if (this.isPrintComments()) {
        CommentPrettyPrinter.printPreComments(node, getPrinter());
      }
      Iterator<ASTSetCollectionItem> iter_gUIArgValue = node.getSetCollectionItemList().iterator();

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

}
