/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain;

import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.IndentPrinter;

/*
 * This transformation sets the association name if it is not present.
 *
 **/
public class UMLAssocNameTrafo implements CDAssociationVisitor2 {

  protected CD4CodeTraverser traverser;

  public UMLAssocNameTrafo(CD4CodeTraverser traverser) {
    this.traverser = traverser;
    this.traverser.add4CDAssociation(this);
  }

  @Override
  public void visit(ASTCDAssociation ast) {
    if (ast.isPresentName()) {
      return;
    }
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    String name = pp.prettyprint(ast.getLeft().getMCQualifiedType()) +
            "_" + ast.getLeft().getName().replace(".", "_") +
            "_" + ast.getRight().getName().replace(".", "_") +
            pp.prettyprint(ast.getRight().getMCQualifiedType());
    ast.setName(name);
  }

  public void transform(ASTCDCompilationUnit ast) {
    ast.accept(this.traverser);
  }

}
