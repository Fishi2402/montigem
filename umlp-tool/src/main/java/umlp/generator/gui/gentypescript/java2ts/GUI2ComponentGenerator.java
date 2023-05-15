/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.genjava.GUI2CDTrafo;
import umlp.generator.gui.gentypescript.printer.TSUtilsPrinter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class GUI2ComponentGenerator extends CDGenerator {

  protected GUIComponentsVisitor viewComponents;

  public GUI2ComponentGenerator(GeneratorSetup genSetup, GUIComponentsVisitor viewComponents) {
    super(genSetup);
    this.viewComponents = viewComponents;
    // genSetup.getGlex().setGlobalValue("cdPrinter", new CdUtilsPrinter()); // TODO GV
    genSetup.getGlex().setGlobalValue("cdPrinter", new TSUtilsPrinter());

    genSetup.getGlex().replaceTemplate(
        CD2JavaTemplates.CLASS, new TemplateHookPoint(GUI2ComponentTemplates.CLASS)
    );
    genSetup.getGlex().replaceTemplate(
        CD2JavaTemplates.INTERFACE, new TemplateHookPoint(GUI2ComponentTemplates.INTERFACE)
    );
    genSetup.getGlex().replaceTemplate(
        CD2JavaTemplates.ATTRIBUTE, new TemplateHookPoint(GUI2ComponentTemplates.ATTRIBUTE)
    );
    genSetup.getGlex().replaceTemplate(
        CD2JavaTemplates.ANNOTATIONS, new TemplateHookPoint(GUI2ComponentTemplates.ANNOTATIONS)
    );
  }

  protected Path getAsPath(String packageAsPath, String name) {
    return Paths.get(packageAsPath, name + ".ts");
  }

  public void generate(ASTGUICompilationUnit modelAST, String configTemplate) {
    this.setup.setTracing(true);
    // create an empty classdiagram
    ASTCDCompilationUnit decoratedAST = GUI2CDTrafo.createCDCompilationUnit(modelAST);

    TemplateController tc = setup.getNewTemplateController(configTemplate);
    TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);

    // translate the GUI-AST into TypeScript
    hpp.processValue(tc, modelAST,
        Arrays.asList(decoratedAST, setup.getGlex(),
            setup.getHandcodedPath(), setup.getOutputDirectory().toPath(),
            this, viewComponents));
  }

}
