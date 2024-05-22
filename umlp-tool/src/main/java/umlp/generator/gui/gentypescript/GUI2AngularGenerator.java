/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import umlp.generator.gui.genjava.GUI2CDTrafo;
import umlp.generator.gui.gentypescript.printer.TSUtilsPrinter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class GUI2AngularGenerator extends GUI2TSGenerator {

  public GUI2AngularGenerator(GeneratorSetup genSetup) {
    super(genSetup);
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
