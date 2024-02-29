/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genhtml;

import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import org.junit.jupiter.api.Test;
import umlp.UMLPTool;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.gui.DomainGUITest;
import umlp.generator.gui.GUIGeneratorUtil;

import java.io.File;

public class GUI2HtmlGeneratorTest extends DomainGUITest {

  @Test
  public void testGenerate() {
    ASTGUICompilationUnit ast;

    ast = processGUIModel("gui/genhtml/HTMLHelper.gui", "src/test/resources/gui");

    // gen setup
    GeneratorSetup genSetup = new GeneratorSetup();
    File outDir = new File(BUILD_FOLDER + "/generated-test-sources");
    genSetup.setOutputDirectory(outDir);
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    genSetup.setGlex(glex);
    genSetup.getGlex().setGlobalValue("genHelper", GUIGeneratorUtil.getInstance());
    UMLPGeneratorConfig conf = UMLPGeneratorConfig.getInstance()
        .setUseSpring(false)
        .setUseJsweet(false)
        .setUseShell(false)
        .setGenerateTypescript(false);
    genSetup.getGlex().setGlobalValue("UMLPGeneratorConfig", conf);

    GUI2HtmlGenerator htmlGenerator = new GUI2HtmlGenerator(genSetup);
    String htmlConfigTemplate = UMLPTool.DEFAULT_CONFIGTEMPLATE_GUI2HTML;

    htmlGenerator.generate(ast, htmlConfigTemplate);

    assertNoErrors();
  }

}
