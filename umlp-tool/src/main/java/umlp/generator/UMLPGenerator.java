/* (c) https://github.com/MontiCore/monticore */
package umlp.generator;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
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

  protected CD4AnalysisAfterParseTrafo cdAfterParseTrafo;

  public UMLPGenerator(GeneratorSetup generatorSetup) {
    generatorSetup.getGlex().setGlobalValue("cdPrinter", new CdUtilsPrinter());
    CD4C.init(generatorSetup);

    cdGenerator = new CDGenerator(generatorSetup);
    cdAfterParseTrafo = new CD4AnalysisAfterParseTrafo();
  }

  public void generate(ASTUMLPCompilationUnit modelAST) {

    // Translate AST of UMLP Model into initial CD-AST
    ASTCDCompilationUnit cdAST = UMLP2CDTrafo.run(modelAST);

    // execute some basic CD-AST trafos (e.g., put everything in packages)
    cdAfterParseTrafo.transform(cdAST);

    // TODO hier trafos realisieren

    // translate the CD-AST into Java with the CD Generator
    cdGenerator.generate(cdAST);
  }

}
