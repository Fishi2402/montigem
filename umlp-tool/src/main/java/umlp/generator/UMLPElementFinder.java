package umlp.generator;

import de.monticore.ast.ASTNode;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDTargetImportStatement;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import umlp.UMLPMill;
import umlp._ast.ASTUMLPElement;
import umlp._ast.ASTUMLPNode;
import umlp._visitor.UMLPTraverser;
import umlp._visitor.UMLPVisitor2;

import java.util.ArrayList;
import java.util.List;

public class UMLPElementFinder implements CDBasisVisitor2 {

    protected List<ASTUMLPElement> umlpElements;


    public UMLPElementFinder() {
        umlpElements = new ArrayList<>();
    }

    @Override
    public void visit(ASTCDPackage node) {
        umlpElements.add(UMLPMill.uMLPCDElementBuilder().setCDElement(node).build());
    }

    @Override
    public void visit(ASTCDTargetImportStatement statement) {
        umlpElements.add(UMLPMill.uMLPCDElementBuilder().setCDTargetImportStatement(statement).build());
    }

    public List<ASTUMLPElement> getUMLPElements() {
        return umlpElements;
    }


    public void run(ASTCDBasisNode node) {
        UMLPTraverser traverser = UMLPMill.inheritanceTraverser();
        traverser.add4CDBasis(this);
        node.accept(traverser);
    }

}
