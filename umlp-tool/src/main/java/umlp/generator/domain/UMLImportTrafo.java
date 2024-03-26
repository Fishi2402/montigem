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
import umlp.UMLPMill;

import java.util.Arrays;

/*
 * This transformation sets default values if an optional node is not defined.
 *
 **/
public class UMLImportTrafo implements  CDBasisVisitor2 {

  protected CD4CodeTraverser traverser;

  public UMLImportTrafo(CD4CodeTraverser traverser) {
    this.traverser = traverser;
    this.traverser.add4CDBasis(this);
  }

  @Override
  public void visit(ASTCDCompilationUnit ast) {
    for (BuiltInTypes t : BuiltInTypes.values()) {
      ast.addMCImportStatement(UMLPMill.mCImportStatementBuilder()
          .setMCQualifiedName(UMLPMill.mCQualifiedNameBuilder()
              .setPartsList(Arrays.asList(t.getQName().split("\\.")))
              .build())
          .setStar(false)
          .build());
    }
  }

  public void transform(ASTCDCompilationUnit ast) {
    ast.accept(this.traverser);
  }

  protected enum BuiltInTypes {
    LIST("java.util.List"),
    MAP("java.util.Map"),
    SET("java.util.Set"),
    OPTIONAL("java.util.Optional");

    private final String qName;

    BuiltInTypes(String qName) {
      this.qName = qName;
    }

    /**
     * @return qualified name
     */
    public String getQName() {
      return qName;
    }
  }

}

