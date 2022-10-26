/* (c) https://github.com/MontiCore/monticore */
package umlp.generator;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cd4code.trafo.CD4CodeTrafo4Defaults;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.logging.Log;
import umlp._ast.ASTUMLPCompilationUnit;

import java.util.Arrays;

/**
 * configures and employs generators of individual UML/P languages to generate
 * code. Currently, it uses only the CDGenerator.
 *
 * Later do: make this customizable as Groovy script
 */
public class UMLPGenerator {

  protected GeneratorSetup genSetup;

  protected CDGenerator cdGenerator;

  protected static CD4CodeFullPrettyPrinter astPrinter;

  protected CD4CodeAfterParseTrafo cD4CodeAfterParseTrafo;

  protected CD4CodeTrafo4Defaults cD4CodeTrafo4Defaults;

  protected CD4CodeDirectCompositionTrafo cD4CodeDirectCompositionTrafo;

  protected CDAssociationCreateFieldsFromNavigableRoles cDAssociationCreateFieldsFromNavigableRoles;

  public UMLPGenerator(GeneratorSetup genSetup) {
    this.genSetup = genSetup;
    astPrinter = new CD4CodeFullPrettyPrinter();

    genSetup.getGlex().setGlobalValue("cdPrinter", new CdUtilsPrinter());

    cdGenerator = new CDGenerator(genSetup);
    cD4CodeAfterParseTrafo = new CD4CodeAfterParseTrafo();
    cD4CodeTrafo4Defaults = new CD4CodeTrafo4Defaults();
    cD4CodeDirectCompositionTrafo = new CD4CodeDirectCompositionTrafo();
    cDAssociationCreateFieldsFromNavigableRoles = new CDAssociationCreateFieldsFromNavigableRoles();
    cDAssociationCreateFieldsFromNavigableRoles.setTraverser(CD4CodeMill.traverser());

  }

  public void generate(ASTUMLPCompilationUnit modelAST, String configTemplate) {

    // Translate AST of UMLP Model into initial CD-AST
    ASTCDCompilationUnit cdAST = UMLP2CDTrafo.run(modelAST);
    logTrafoExec("UMLP2CDTrafo", cdAST);

    // execute some basic CD-AST trafos (e.g., put everything in packages)
    cD4CodeAfterParseTrafo.transform(cdAST);
    logTrafoExec("CD4AnalysisAfterParseTrafo", cdAST);
    ICD4CodeArtifactScope artifactScope = UMLP2CDTrafo.createCD4CodeSymTab(cdAST);
    logTrafoExec("createCD4CodeSymTab", cdAST);

    cD4CodeTrafo4Defaults.transform(cdAST);
    logTrafoExec("CD4CodeTrafo4Defaults", cdAST);

    UMLAttrToAssocTrafo assocTrafo = new UMLAttrToAssocTrafo(CD4CodeMill.traverser());
    assocTrafo.transform(cdAST);
    logTrafoExec("UMLAttrToAssocTrafo", cdAST);

    // rebuild symbol table
    CD4CodeMill.globalScope().removeSubScope(artifactScope);
    UMLP2CDTrafo.createCD4CodeSymTab(cdAST);
    logTrafoExec("rebuildCD4CodeSymTab", cdAST);

    // create an empty classdiagram
    ASTCDCompilationUnit decoratedAST = UMLP2CDTrafo.createCompilationUnit(cdAST);

    TemplateController tc = genSetup.getNewTemplateController(configTemplate);
    TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);
    // translate the CD-AST into Java with the CD Generator
    hpp.processValue(tc, cdAST, Arrays.asList(decoratedAST, genSetup.getGlex(), genSetup.getHandcodedPath(), cdGenerator));
  }

  protected static void logTrafoExec(String trafo, ASTCDCompilationUnit ast){
    Log.info("========= CD after '"+trafo+"': =========", UMLPGenerator.class.getName());
    Log.info(astPrinter.prettyprint(ast), UMLPGenerator.class.getName());
  }
}
