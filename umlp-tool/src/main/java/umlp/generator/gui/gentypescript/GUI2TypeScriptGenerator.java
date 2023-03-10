/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.guidsl._symboltable.IGUIDSLArtifactScope;
import de.monticore.prettyprint.IndentPrinter;
import umlp.generator.gui.GUIComponentsVisitor;

import java.util.Arrays;

public class GUI2TypeScriptGenerator {

  protected final GeneratorEngine generatorEngine;
  protected GeneratorSetup setup;
  IGUIDSLArtifactScope symbolTableGui;
  protected static GUIDSLFullPrettyPrinter astPrinter;
  protected GUIComponentsVisitor viewComponents;

  public GUI2TypeScriptGenerator(GeneratorSetup generatorSetup, IGUIDSLArtifactScope symbolTableGui, GUIComponentsVisitor viewComponents) {
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    this.setup = generatorSetup;
    this.symbolTableGui = symbolTableGui;
    astPrinter = new GUIDSLFullPrettyPrinter(new IndentPrinter(), false);
    this.viewComponents = viewComponents;
    setup.getGlex().setGlobalValue("astPrinter", astPrinter);
  }

  public void generate(ASTGUICompilationUnit modelAST, String configTemplate) {
    TemplateController tc = setup.getNewTemplateController(configTemplate);
    TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);

    // translate the GUI-AST into Java
    hpp.processValue(tc, modelAST,
        Arrays.asList(modelAST, setup.getGlex(), setup.getHandcodedPath(), this, viewComponents));
  }

  public void generate(ASTGUICompilationUnit decoratedAst) {
  }

}
