/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdassociation._ast.ASTCDAssocLeftSide;
import de.monticore.cdassociation._ast.ASTCDAssocRightSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.IndentPrinter;
import umlp._prettyprint.UMLPFullPrettyPrinter;

/*
 * This transformation sets default values if an optional node is not defined.
 *
 **/
public class UMLSetDefaultsTrafo implements CDAssociationVisitor2 {

  protected CD4CodeTraverser traverser;

  public UMLSetDefaultsTrafo(CD4CodeTraverser traverser) {
    this.traverser = traverser;
    this.traverser.add4CDAssociation(this);
  }

  @Override
  public void visit(ASTCDAssociation ast) {
    if (ast.isPresentName()) {
      return;
    }
    UMLPFullPrettyPrinter pp = new UMLPFullPrettyPrinter(new IndentPrinter());
    String name = pp.prettyprint(ast.getLeft().getMCQualifiedType()) +
            "_" + ast.getLeft().getName().replace(".", "_") +
            "_" + ast.getRight().getName().replace(".", "_") +
            "_" + pp.prettyprint(ast.getRight().getMCQualifiedType());
    ast.setName(name);
  }

  @Override
  public void visit(ASTCDAssocRightSide ast) {
    if (!ast.isPresentCDCardinality()) {
      ast.setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
    }
  }

  @Override
  public void visit(ASTCDAssocLeftSide ast) {
    if (!ast.isPresentCDCardinality()) {
      ast.setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
    }
  }

  public void transform(ASTCDCompilationUnit ast) {
    ast.accept(this.traverser);
  }

}
