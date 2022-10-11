/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;

public class GUI2CDTrafo {

  public static ASTCDCompilationUnit createCDCompilationUnit(ASTGUICompilationUnit guiAst) {
    // create an empty CD definition
    ASTCDDefinition cdDef = CD4CodeMill.cDDefinitionBuilder()
        .setName(guiAst.getComponentName())
        .setModifier(CDModifier.PUBLIC.build())
        .build();

    ASTCDCompilationUnit cdAst = CD4CodeMill.cDCompilationUnitBuilder()
        .setCDDefinition(cdDef)
        .build();

    // set the package
    cdAst.setMCPackageDeclaration(guiAst.getMCPackageDeclaration());

    // set imports
    cdAst.setMCImportStatementList(guiAst.getMCImportStatementList());

    return cdAst;
  }

  public static void createCD4CodeSymTab(ASTCDCompilationUnit cdAST) {
    // create symtab
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cdAST);
    cdAST.accept(new CD4CodeSymbolTableCompleter(cdAST).getTraverser());
  }

}
