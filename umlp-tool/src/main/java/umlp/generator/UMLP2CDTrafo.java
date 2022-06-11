/* (c) https://github.com/MontiCore/monticore */

package umlp.generator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.class2mc.Class2MCResolver;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import umlp.UMLPMill;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp._symboltable.IUMLPScope;

// TODO Importe zwischen UMLP Modellen auflösen
public class UMLP2CDTrafo {

  public static ASTCDCompilationUnit run(ASTUMLPCompilationUnit umlpAST) {
    UMLPMill.globalScope().clear();

    // use visitor to collect CD parts of the UMLP AST (to avoid casts)
    CDElementFinder finder = new CDElementFinder();
    finder.run(umlpAST);

    // create a CD definition with all the CDElements of the UMLP model...
    ASTCDDefinition cdDef = CD4CodeMill.cDDefinitionBuilder()
        .setName(umlpAST.getUMLPDef().getName())
        .setCDElementsList(finder.getCDElements())
        .setModifier(CDModifier.PUBLIC.build())
        .build();

    // ... and add it to a new CD compilation unit
    ASTCDCompilationUnit cdAST = CD4CodeMill.cDCompilationUnitBuilder()
        .setCDTargetImportStatementsList(finder.getCDTargetImportStatements())
        .setCDDefinition(cdDef)
        .build();

    // set the package
    if (umlpAST.isPresentMCPackageDeclaration()) {
      cdAST.setMCPackageDeclaration(umlpAST.getMCPackageDeclaration());
    }

    return cdAST;
  }

  public static void createCD4CodeSymTab(ASTCDCompilationUnit cdAST) {
    // create symtab
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    createListSymbol();
    CD4CodeMill.globalScope().addAdaptedTypeSymbolResolver(new Class2MCResolver());
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cdAST);
    cdAST.accept(new CD4CodeSymbolTableCompleter(cdAST).getTraverser());
  }

  public static ASTCDCompilationUnit createCompilationUnit(ASTCDCompilationUnit cd) {
    ASTCDCompilationUnit newCD = cd.deepClone();
    newCD.getCDDefinition().clearCDElements();
    return newCD;
  }

  protected static void createListSymbol() {
    IUMLPScope scope = UMLPMill.scope();
    TypeVarSymbol t = CD4CodeMill.typeVarSymbolBuilder()
        .setName("T")
        .setSpannedScope(UMLPMill.scope())
        .setEnclosingScope(scope)
        .build();
    scope.add(t);
    OOTypeSymbol list = UMLPMill.oOTypeSymbolBuilder()
        .setSpannedScope(UMLPMill.scope())
        .setName("List")
        .setEnclosingScope(UMLPMill.globalScope())
        .build();
    UMLPMill.globalScope().add(list);
  }
}
