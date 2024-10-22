/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;
import de.monticore.cdassociation._ast.ASTCDAssocLeftSide;
import de.monticore.cdassociation._ast.ASTCDAssocRightSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;

/*
 * This transformation sets default values if an optional node is not defined.
 *
 **/
public class UMLSetDefaultsTrafo implements CDAssociationVisitor2, CDBasisVisitor2, CD4CodeBasisVisitor2 {

  protected CD4CodeTraverser traverser;

  public UMLSetDefaultsTrafo(CD4CodeTraverser traverser) {
    this.traverser = traverser;
    this.traverser.add4CDAssociation(this);
    this.traverser.add4CDBasis(this);
    this.traverser.add4CD4CodeBasis(this);
  }

  @Override
  public void visit(ASTCDAssociation ast) {
    if (!(ast.getModifier().isPrivate() || ast.getModifier().isProtected() || ast.getModifier().isPublic())) {
      ast.getModifier().setPublic(true);
    }

    if (ast.isPresentName()) {
      return;
    }

    String name = StringTransformations.uncapitalize(
        UMLPMill.prettyPrint(ast.getLeft().getMCQualifiedType(), false)
            .replace(".", "_")) +
        "_" + ast.getLeft().getName().replace(".", "_") +
        "_" + ast.getRight().getName().replace(".", "_") +
        "_" + StringTransformations.uncapitalize(
            UMLPMill.prettyPrint(ast.getRight().getMCQualifiedType(), false)
                    .replace(".", "_"));
    ast.setName(name);
  }

  @Override
  public void visit(ASTCDAssocRightSide ast) {
    if (!ast.isPresentCDCardinality()) {
      ast.setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
    }

    if (!(ast.getModifier().isPrivate() || ast.getModifier().isProtected() || ast.getModifier().isPublic())) {
      ast.getModifier().setPublic(true);
    }
  }

  @Override
  public void visit(ASTCDAssocLeftSide ast) {
    if (!ast.isPresentCDCardinality()) {
      ast.setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
    }

    if (!(ast.getModifier().isPrivate() || ast.getModifier().isProtected() || ast.getModifier().isPublic())) {
      ast.getModifier().setPublic(true);
    }
  }

 @Override
  public void visit(ASTCDAttribute ast) {
    if (!(ast.getModifier().isPrivate() || ast.getModifier().isProtected() || ast.getModifier().isPublic())) {
      ast.getModifier().setPublic(true);
    }
  }

  @Override
  public void visit(ASTCDMethod ast) {
    if (!(ast.getModifier().isPrivate() || ast.getModifier().isProtected() || ast.getModifier().isPublic())) {
      ast.getModifier().setPublic(true);
    }
  }

  public void transform(ASTCDCompilationUnit ast) {
    ast.accept(this.traverser);
  }

}
