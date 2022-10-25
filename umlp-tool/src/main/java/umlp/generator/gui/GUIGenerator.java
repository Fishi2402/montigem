/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._symboltable.IGUIDSLArtifactScope;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.UMLPGenerator;

import java.util.Arrays;

public class GUIGenerator extends CDGenerator {

  protected static CD4CodeFullPrettyPrinter astPrinter;

  public GUIGenerator(GeneratorSetup genSetup, IGUIDSLArtifactScope symbolTableGui) {
    super(genSetup);
    astPrinter = new CD4CodeFullPrettyPrinter();
    genSetup.getGlex().setGlobalValue("cdPrinter", new CdUtilsPrinter()); // TODO GV

  }

  protected static void logTrafoExec(String trafo, ASTCDCompilationUnit ast) {
    Log.info("========= CD after '" + trafo + "': =========", UMLPGenerator.class.getName());
    Log.info(astPrinter.prettyprint(ast), UMLPGenerator.class.getName());
  }

  public void generate(ASTGUICompilationUnit modelAST, String configTemplate) {
    // create an empty classdiagram
    ASTCDCompilationUnit decoratedAST = GUI2CDTrafo.createCDCompilationUnit(modelAST);
    //GUI2CDTrafo.createCD4CodeSymTab(decoratedAST);
    logTrafoExec("GUI2CDTrafo", decoratedAST);

    TemplateController tc = setup.getNewTemplateController(configTemplate);
    TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);

    // translate the GUI-AST into Java
    hpp.processValue(tc, modelAST,
        Arrays.asList(decoratedAST, setup.getGlex(), setup.getHandcodedPath(), this));
  }

}
