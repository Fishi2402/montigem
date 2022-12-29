/* (c) https://github.com/MontiCore/monticore */
package umlp;

import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._visitor.CDBasisHandler;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdbasis.prettyprint.CDBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import org.apache.tools.ant.taskdefs.optional.extension.LibFileSet;
import umlp._ast.*;
import umlp._visitor.UMLPHandler;
import umlp._visitor.UMLPTraverser;
import umlp._visitor.UMLPVisitor2;

import java.util.List;

public class UMLPPrettyPrinter extends CDBasisPrettyPrinter {

    public UMLPPrettyPrinter(IndentPrinter indentPrinter) {
        super(indentPrinter);
    }

    @Override
    public void handle(ASTCDDefinition node) {
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

