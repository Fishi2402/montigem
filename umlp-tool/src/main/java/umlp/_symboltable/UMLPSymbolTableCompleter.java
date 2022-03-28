/* (c) https://github.com/MontiCore/monticore */

package umlp._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4code.typescalculator.DeriveSymTypeOfCD4Code;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCompleter;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCompleter;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCompleter;
import de.monticore.class2mc.Class2MCResolver;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import umlp.UMLPMill;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp._visitor.UMLPTraverser;

import java.util.List;

public class UMLPSymbolTableCompleter {

  protected UMLPTraverser traverser;
  protected CDSymbolTableHelper symbolTableHelper;

  public UMLPSymbolTableCompleter(ASTUMLPCompilationUnit ast) {
    this(ast.getMCImportStatementList(),
        ast.isPresentMCPackageDeclaration() ?
            ast.getMCPackageDeclaration().getMCQualifiedName() :
            MCQualifiedNameFacade.createQualifiedName(""));
  }

  public UMLPSymbolTableCompleter(List<ASTMCImportStatement> imports, ASTMCQualifiedName packageDeclaration) {
    this.symbolTableHelper = new CDSymbolTableHelper(new DeriveSymTypeOfCD4Code())
        .setPackageDeclaration(packageDeclaration);

    // Set up the traverser
    this.traverser = UMLPMill.traverser();
    final CDBasisSymbolTableCompleter cDBasisVisitor = new CDBasisSymbolTableCompleter(symbolTableHelper);
    traverser.add4CDBasis(cDBasisVisitor);
    traverser.add4OOSymbols(cDBasisVisitor);
    final CDAssociationSymbolTableCompleter cDAssociationVisitor = new CDAssociationSymbolTableCompleter(symbolTableHelper);
    traverser.add4CDAssociation(cDAssociationVisitor);
    traverser.setCDAssociationHandler(cDAssociationVisitor);
    final CDInterfaceAndEnumSymbolTableCompleter cdInterfaceAndEnumVisitor = new CDInterfaceAndEnumSymbolTableCompleter(symbolTableHelper);
    traverser.add4CDInterfaceAndEnum(cdInterfaceAndEnumVisitor);
    final CD4CodeBasisSymbolTableCompleter cd4CodeBasisVisitor = new CD4CodeBasisSymbolTableCompleter(symbolTableHelper);
    traverser.add4CD4CodeBasis(cd4CodeBasisVisitor);

    // add built-in primitive types
    if (BasicSymbolsMill.globalScope().getLocalTypeSymbols().isEmpty()) {
      BasicSymbolsMill.initializePrimitives();
    }

    // add Java types
    UMLPMill.globalScope().addAdaptedTypeSymbolResolver(new Class2MCResolver());
  }

  public UMLPTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(UMLPTraverser t) {
    traverser = t;
  }


}

