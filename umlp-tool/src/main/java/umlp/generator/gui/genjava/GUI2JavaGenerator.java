/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIComponentsVisitor;

import java.util.Arrays;

public class GUI2JavaGenerator extends CDGenerator {

  protected static CD4CodeFullPrettyPrinter astPrinter;

  public GUI2JavaGenerator(GeneratorSetup genSetup) {
    super(genSetup);
    astPrinter = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    genSetup.getGlex().setGlobalValue("cdPrinter", new CdUtilsPrinter()); // TODO GV
  }

  protected static void logTrafoExec(String trafo, ASTCDCompilationUnit ast) {
    Log.info("========= CD after '" + trafo + "': =========", GUI2JavaGenerator.class.getName());
    Log.info(astPrinter.prettyprint(ast), GUI2JavaGenerator.class.getName());
  }

  public void generate(ASTGUICompilationUnit modelAST, String configTemplate) {
    this.setup.setTracing(true);

    // create an empty classdiagram
    ASTCDCompilationUnit decoratedAST = GUI2CDTrafo.createCDCompilationUnit(modelAST);
    logTrafoExec("GUI2CDTrafo", decoratedAST);

    TemplateController tc = setup.getNewTemplateController(configTemplate);
    TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);

    // translate the GUI-AST into Java
    hpp.processValue(tc, modelAST,
        Arrays.asList(decoratedAST, setup.getGlex(), setup.getHandcodedPath(), this));
  }

}
