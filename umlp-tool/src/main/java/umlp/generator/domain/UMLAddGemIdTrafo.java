/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain;

import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDInterface;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;
import de.monticore.cdassociation._ast.ASTCDAssocLeftSide;
import de.monticore.cdassociation._ast.ASTCDAssocRightSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import umlp.UMLPMill;

/*
 * This transformation sets default values if an optional node is not defined.
 *
 **/
public class UMLAddGemIdTrafo implements CDBasisVisitor2, CD4CodeBasisVisitor2 {

  protected CD4CodeTraverser traverser;

  public static final String GEM_ID = "gemId";

  public UMLAddGemIdTrafo(CD4CodeTraverser traverser) {
    this.traverser = traverser;
    this.traverser.add4CDBasis(this);
    this.traverser.add4CD4CodeBasis(this);
  }

  @Override
  public void visit(ASTCDClass ast) {
    if (!ast.isPresentCDExtendUsage() && !ast.getCDAttributeList().stream().anyMatch(a -> GEM_ID.equals(a.getName()))) {
      // Add attribute
      ast.addCDMember(CDAttributeFacade.getInstance().
          createAttribute(CDModifier.PUBLIC.build(), "long", GEM_ID));
    }
  }

  @Override
  public void visit(ASTCDInterface ast) {
    if (!ast.isPresentCDExtendUsage() && !ast.getCDAttributeList().stream().anyMatch(a -> GEM_ID.equals(a.getName()))) {
      // Add attribute
      ast.addCDMember(CDAttributeFacade.getInstance().
          createAttribute(CDModifier.PUBLIC.build(), "long", GEM_ID));
    }
  }

  public void transform(ASTCDCompilationUnit ast) {
    ast.accept(this.traverser);
  }

}
