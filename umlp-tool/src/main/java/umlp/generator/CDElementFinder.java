/* (c) https://github.com/MontiCore/monticore */

package umlp.generator;

import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDTargetImportStatement;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import umlp.UMLPMill;
import umlp._ast.ASTUMLPNode;
import umlp._visitor.UMLPTraverser;

import java.util.ArrayList;
import java.util.List;

public class CDElementFinder implements CDBasisVisitor2 {

  protected List<ASTCDElement> cDElements;

  protected List<ASTCDTargetImportStatement> cDTargetImportStatements;

  public CDElementFinder() {
    cDElements = new ArrayList<>();
    cDTargetImportStatements = new ArrayList<>();
  }

  @Override public void visit(ASTCDElement node) {
    cDElements.add(node);
  }

  public List<ASTCDElement> getCDElements() {
    return cDElements;
  }

  public List<ASTCDTargetImportStatement> getCDTargetImportStatements() {
    return cDTargetImportStatements;
  }

  public void run(ASTUMLPNode node) {
    UMLPTraverser traverser = UMLPMill.traverser();
    traverser.add4CDBasis(this);
    node.accept(traverser);
  }
}
