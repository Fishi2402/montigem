/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdassociation._ast.ASTCDAssocLeftSide;
import de.monticore.cdassociation._ast.ASTCDAssocRightSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor2;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import umlp.generator.domain.decorator.GenService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

/*
 * This transformation converts attributes with "CDType" into a suitable association.
 * It is assumed that at least one package exists and that the symbol table is constructed
 *
 **/
public class UMLAttrToAssocTrafo implements CDBasisVisitor2, CDInterfaceAndEnumVisitor2 {

  protected ASTCDPackage astcdPackage;

  protected ASTCDType astcdType;

  protected Multimap<ASTCDType, ASTCDAttribute> attrMap = Multimaps.newMultimap(new HashMap<>(), ArrayList::new);

  protected Map<ASTCDPackage, ASTCDAssociation> assocMap = new HashMap<>();

  protected CD4CodeTraverser traverser;

  public UMLAttrToAssocTrafo(CD4CodeTraverser traverser) {
    this.traverser = traverser;
    this.traverser.add4CDBasis(this);
    this.traverser.add4CDInterfaceAndEnum(this);
  }

  @Override
  public void visit(ASTCDPackage node) {
    astcdPackage = node;
  }

  @Override
  public void visit(ASTCDClass node) {
    astcdType = node;
  }

  @Override
  public void visit(ASTCDInterface node) {
    astcdType = node;
  }

  @Override
  public void visit(ASTCDAttribute attr) {
    SymTypeExpression attrType = attr.getSymbol().getType();
    if (GenService.isCDType(attrType)) {
      assocMap.put(astcdPackage,
              createAssoc(attr.getName(), astcdType.getSymbol().getName(),
                      CD4CodeMill.cDCardOneBuilder().build(),
                      attrType.print()));
      attrMap.put(astcdType, attr);
    } else if (attrType.isGenericType()) {
      SymTypeExpression argType = ((SymTypeOfGenerics) attrType).getArgument(0);
      if (GenService.isCDType(argType)) {
        if (GenService.isOptional(attrType)) {
          assocMap.put(astcdPackage, createAssoc(attr.getName(), astcdType.getSymbol().getName(),
                  CD4CodeMill.cDCardOptBuilder().build(),
                  argType.print()));
          attrMap.put(astcdType, attr);
        } else if (GenService.isList(attrType)) {
          assocMap.put(astcdPackage, createAssoc(attr.getName(), astcdType.getSymbol().getName(),
                  CD4CodeMill.cDCardMultBuilder().build(),
                  argType.print()));
          attrMap.put(astcdType, attr);
        }
      }
    }
  }

  @Override
  public void endVisit(ASTCDDefinition node) {
   attrMap.entries().forEach(e -> e.getKey().removeCDMember(e.getValue()));
   assocMap.entrySet().forEach(e -> e.getKey().addCDElement(e.getValue()));
  }

  protected ASTCDAssociation createAssoc(String attrName, String sourceName, ASTCDCardinality card, String targetName) {
    ASTCDAssocLeftSide leftSide = CD4CodeMill.cDAssocLeftSideBuilder()
            .setModifier(PUBLIC.build())
            .setMCQualifiedType(MCTypeFacade.getInstance().createQualifiedType(sourceName))
            .build();
    ASTCDAssocRightSide rightSide = CD4CodeMill.cDAssocRightSideBuilder()
            .setModifier(PUBLIC.build())
            .setMCQualifiedType(MCTypeFacade.getInstance().createQualifiedType(targetName))
            .setCDRole(CD4CodeMill.cDRoleBuilder().setName(attrName).build())
            .build();
    return CD4CodeMill.cDAssociationBuilder()
            .setModifier(PUBLIC.build())
            .setCDAssocType(CD4CodeMill.cDAssocTypeAssocBuilder().build())
            .setLeft(leftSide)
            .setCDAssocDir(CD4CodeMill.cDLeftToRightDirBuilder().build())
            .setRight(rightSide)
            .build();
  }

  public void transform(ASTCDCompilationUnit ast) {
    ast.accept(this.traverser);
  }

}
