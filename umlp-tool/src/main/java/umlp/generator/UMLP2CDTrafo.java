/* (c) https://github.com/MontiCore/monticore */

package umlp.generator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import umlp._ast.ASTUMLPCompilationUnit;

// TODO Importe zwischen UMLP Modellen auflösen, falls es welche geben soll
public class UMLP2CDTrafo {

  public static ASTCDCompilationUnit run(ASTUMLPCompilationUnit umlpAST) {

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
    if(umlpAST.isPresentMCPackageDeclaration()){
      cdAST.setMCPackageDeclaration(umlpAST.getMCPackageDeclaration());
    }

    // TODO create symtab with cd tool ?
    return cdAST;
  }

}
