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
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.GUITemplateHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class GUI2HtmlGenerator {

  protected final GeneratorEngine generatorEngine;
  protected GeneratorSetup setup;
  IGUIDSLArtifactScope symbolTableGui;
  protected GUIComponentsVisitor viewComponents;

  public GUI2HtmlGenerator(GeneratorSetup generatorSetup, IGUIDSLArtifactScope symbolTableGui, GUIComponentsVisitor viewComponents) {
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    this.setup = generatorSetup;
    this.symbolTableGui = symbolTableGui;
    this.viewComponents = viewComponents;
  }

  public void generate(ASTGUICompilationUnit modelAST, String configTemplate) {
    TemplateController tc = setup.getNewTemplateController(configTemplate);
    TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);

    // translate the GUI-AST into HTML
    hpp.processValue(tc, modelAST,
        Arrays.asList(modelAST, setup.getGlex(), setup.getHandcodedPath(), this));
  }

  public void generate(ASTGUICompilationUnit decoratedAst) {
    this.setup.setTracing(false);

    String componentName = GUIGeneratorUtil.getComponentNameFromCamelCaseName(decoratedAst.getComponentName());
    String pathName = String.join("/", decoratedAst.getMCPackageDeclaration().getMCQualifiedName().getPartsList());
    Path generatedFiles = Paths.get(pathName, componentName, componentName + ".component.html");

    GeneratorEngine e = new GeneratorEngine(setup);
    e.generate("umlp.gui2html.Page", generatedFiles, decoratedAst, viewComponents.getNotNestedComponents());
  }

}
