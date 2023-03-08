/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genhtml;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.guidsl._symboltable.IGUIDSLArtifactScope;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.UMLPGenerator;

import java.util.Arrays;

public class GUI2HtmlGenerator {

  protected final GeneratorEngine generatorEngine;
  protected GeneratorSetup setup;
  IGUIDSLArtifactScope symbolTableGui;
  protected static GUIDSLFullPrettyPrinter astPrinter;

  public GUI2HtmlGenerator(GeneratorSetup generatorSetup, IGUIDSLArtifactScope symbolTableGui) {
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    this.setup = generatorSetup;
    this.symbolTableGui = symbolTableGui;
    astPrinter = new GUIDSLFullPrettyPrinter(new IndentPrinter(), false);
    setup.getGlex().setGlobalValue("astPrinter", astPrinter); // TODO GV
  }

  protected static void logTrafoExec(String trafo, ASTGUICompilationUnit ast) {
    Log.info("========= CD after '" + trafo + "': =========", UMLPGenerator.class.getName());
    Log.info(astPrinter.prettyprint(ast), GUI2HtmlGenerator.class.getName());
  }

  public void generate(ASTGUICompilationUnit modelAST, String configTemplate) {
    // create the empty AST to decorate
    ASTGUICompilationUnit decoratedAST = GUI2GUITrafo.createDecoratedGUIAst(modelAST);
    logTrafoExec("GUI2CDTrafo", decoratedAST);

    TemplateController tc = setup.getNewTemplateController(configTemplate);
    TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);

    // translate the GUI-AST into Java
    hpp.processValue(tc, modelAST,
        Arrays.asList(decoratedAST, setup.getGlex(), setup.getHandcodedPath(), this));
  }

  public void generate(ASTGUICompilationUnit decoratedAst) {
  }

}
