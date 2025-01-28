/* (c) https://github.com/MontiCore/monticore */
package umlp._prettyprint;

import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._prettyprint.CDBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class CD2UMLPPrettyPrinter extends CDBasisPrettyPrinter {

  public CD2UMLPPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
    public void handle(ASTCDDefinition node) {
        // Print the stereotype
        if (node.getModifier().isPresentStereotype())
          node.getModifier().getStereotype().accept(getTraverser());

        getPrinter().print("umlp");

        getPrinter().print(" ");
        getPrinter().print(node.getName());
        getPrinter().print(" ");
        getPrinter().println("{");
        getPrinter().indent();
        for (ASTCDElement element : node.getCDElementList()) {
            element.accept(getTraverser());
            getPrinter().println();
        }
        getPrinter().unindent();
        getPrinter().print("}");
    }

}

