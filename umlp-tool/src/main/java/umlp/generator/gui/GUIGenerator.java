/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;

import java.util.Arrays;

/**
 * configures and employs generators of individual UML/P languages to generate
 * code. Currently, it uses only the CDGenerator.
 * <p>
 * Later do: make this customizable as Groovy script
 */
public class GUIGenerator {

  protected GeneratorSetup genSetup;

  public GUIGenerator(GeneratorSetup genSetup) {
    this.genSetup = genSetup;
  }

  public void generate(ASTGUICompilationUnit modelAST, String configTemplate) {
    TemplateController tc = genSetup.getNewTemplateController(configTemplate);
    TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);
    // translate the GUI-AST into Java
    hpp.processValue(tc, modelAST,
        Arrays.asList(modelAST.deepClone(), genSetup.getGlex(), genSetup.getHandcodedPath(), this));
  }

  public void generate(ASTGUICompilationUnit compilationUnit) {
    System.err.println("CompilationUnit: " + compilationUnit.getComponentName());
  }

}
