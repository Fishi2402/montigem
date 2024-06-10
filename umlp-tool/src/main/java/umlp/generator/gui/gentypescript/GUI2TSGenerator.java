/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.prettyprint.IndentPrinter;
import umlp.generator.gui.genjava.GUI2CDTrafo;
import umlp.generator.gui.genjava.GUI2JavaGenerator;
import umlp.generator.gui.gentypescript.printer.TSUtilsPrinter;
import umlp.generator.gui.gentypescript.printer.TypeScriptTypesFullPrettyPrinter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class GUI2TSGenerator extends GUI2JavaGenerator {

  protected static final String TS_EXTENSION = ".ts";

  public GUI2TSGenerator(GeneratorSetup genSetup) {
    super(genSetup);
    genSetup.getGlex().setGlobalValue("cdPrinter", new TSUtilsPrinter());
  }

  protected Path getAsPath(String packageAsPath, String name) {
    return Paths.get(packageAsPath, name + TS_EXTENSION);
  }

  @Override
  public void generate(ASTGUICompilationUnit gui, String configTemplate) {
    this.setup.setTracing(true);

    // create an empty classdiagram
    ASTCDCompilationUnit cd = GUI2CDTrafo.createCDCompilationUnit(gui);

    TemplateController tc = setup.getNewTemplateController(configTemplate);
    TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);

    // translate the GUI-AST into TypeScript
    hpp.processValue(tc, gui, Arrays.asList(
        cd, setup.getGlex(), setup.getHandcodedPath(), setup.getOutputDirectory().toPath(), this));
  }

}
