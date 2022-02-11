/* (c) https://github.com/MontiCore/monticore */
package umlp.generator;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cd4analysis.trafo.CD4AnalysisTrafo4Defaults;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cd4code.trafo.CD4CodeTrafo4Defaults;
import de.monticore.cdassociation.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
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

  protected static CD4CodeFullPrettyPrinter astPrinter;

  protected CD4CodeAfterParseTrafo cD4CodeAfterParseTrafo;

  protected CD4CodeTrafo4Defaults cD4CodeTrafo4Defaults;

  protected CD4CodeDirectCompositionTrafo cD4CodeDirectCompositionTrafo;

  protected CDAssociationCreateFieldsFromNavigableRoles cDAssociationCreateFieldsFromNavigableRoles;


  public UMLPGenerator(GeneratorSetup generatorSetup) {
    astPrinter = new CD4CodeFullPrettyPrinter();

    generatorSetup.getGlex().setGlobalValue("cdPrinter", new CdUtilsPrinter());
    CD4C.init(generatorSetup);

    cdGenerator = new CDGenerator(generatorSetup);
    cD4CodeAfterParseTrafo = new CD4CodeAfterParseTrafo();
    cD4CodeTrafo4Defaults = new CD4CodeTrafo4Defaults();
    cD4CodeDirectCompositionTrafo = new CD4CodeDirectCompositionTrafo();
    cDAssociationCreateFieldsFromNavigableRoles = new CDAssociationCreateFieldsFromNavigableRoles();
    cDAssociationCreateFieldsFromNavigableRoles.setTraverser(CD4CodeMill.traverser());
  }

  public void generate(ASTUMLPCompilationUnit modelAST) {

    // Translate AST of UMLP Model into initial CD-AST
    ASTCDCompilationUnit cdAST = UMLP2CDTrafo.run(modelAST);
    logTrafoExec("UMLP2CDTrafo", cdAST);

    // execute some basic CD-AST trafos (e.g., put everything in packages)
    cD4CodeAfterParseTrafo.transform(cdAST);
    logTrafoExec("CD4AnalysisAfterParseTrafo", cdAST);

    cD4CodeTrafo4Defaults.transform(cdAST);
    logTrafoExec("CD4CodeTrafo4Defaults", cdAST);

    cD4CodeDirectCompositionTrafo.transform(cdAST);
    logTrafoExec("CD4CodeDirectCompositionTrafo", cdAST);

    cDAssociationCreateFieldsFromNavigableRoles.init(cdAST);
    cDAssociationCreateFieldsFromNavigableRoles.transform(cdAST);
    logTrafoExec("CDAssociationCreateFieldsFromNavigableRoles", cdAST);


    // TODO hier trafos realisieren


    // translate the CD-AST into Java with the CD Generator
    cdGenerator.generate(cdAST);
  }

  protected static void logTrafoExec(String trafo, ASTCDCompilationUnit ast){
    System.out.println("========= CD after '"+trafo+"': =========");
    System.out.println(astPrinter.prettyprint(ast));
  }

}
