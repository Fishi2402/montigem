/* (c) https://github.com/MontiCore/monticore */
package umlp.generator;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.generating.GeneratorSetup;
import umlp._ast.ASTUMLPCompilationUnit;

/**
 * configures and employs generators of individual UML/P languages to generate
 * code. Currently, it uses only the CDGenerator.
 *
 * Later do: make this customizable as Groovy script
 */
public class UMLPGenerator {

  protected CDGenerator cdGenerator;

  public UMLPGenerator(GeneratorSetup generatorSetup) {
    cdGenerator = new CDGenerator(generatorSetup);
  }

  public void generate(ASTUMLPCompilationUnit modelAST) {
    ASTCDCompilationUnit cdAST = trafoUMLP2CD(modelAST);
    genCD2Java(cdAST);
  }

  public void genCD2Java(ASTCDCompilationUnit ast) {
    cdGenerator.generate(ast);
  }

  // TODO Importe zwischen UMLP Modellen auflösen, falls es welche geben soll
  public ASTCDCompilationUnit trafoUMLP2CD(ASTUMLPCompilationUnit modelAST) {

    // use visitor to collect CD parts of the UMLP AST (to avoid casts)
    CDElementFinder finder = new CDElementFinder();
    finder.run(modelAST);

    // create a CD definition with all the CDElemnts of the UMLP model...
    ASTCDDefinition cdDef = CD4CodeMill.cDDefinitionBuilder()
        .setName(modelAST.getUMLPDef().getName())
        .setCDElementsList(finder.getCDElements())
        .build();

    // ... and add it to a new CD compilation unit
    ASTCDCompilationUnit cdAST = CD4CodeMill.cDCompilationUnitBuilder()
        .setMCPackageDeclaration(modelAST.getMCPackageDeclaration())
        .setCDTargetImportStatementsList(finder.getCDTargetImportStatements())
        .setCDDefinition(cdDef)
        .build();

    // TODO create symtab with cd tool
    return cdAST;
  }


}
