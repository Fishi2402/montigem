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
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
        .setUseShell(false);
    genSetup.getGlex().setGlobalValue("UMLPGeneratorConfig", conf);
    genSetup.getGlex().setGlobalValue("htmlHelper", new HTMLHelper(ast));

    GUI2HtmlGenerator htmlGenerator = new GUI2HtmlGenerator(genSetup);
    String htmlConfigTemplate = UMLPTool.DEFAULT_CONFIGTEMPLATE_GUI2HTML;

    htmlGenerator.generate(ast, htmlConfigTemplate);

    assertNoErrors();
  }

  @Test
  public void testAutomaticTestTextInputValidityBinding() throws Exception {
    ASTGUICompilationUnit ast = processGUIModel(
        "gui/genjava/decorator/TestTextInputAutoValidity.gui",
        "src/test/resources/gui",
        "packaged/Domain.umlp"
    );

    GeneratorSetup genSetup = new GeneratorSetup();
    File outDir = new File(BUILD_FOLDER + "/generated-test-sources");
    genSetup.setOutputDirectory(outDir);
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    genSetup.setGlex(glex);
    genSetup.getGlex().setGlobalValue("genHelper", GUIGeneratorUtil.getInstance());
    UMLPGeneratorConfig conf = UMLPGeneratorConfig.getInstance()
        .setUseSpring(false)
        .setUseShell(false);
    genSetup.getGlex().setGlobalValue("UMLPGeneratorConfig", conf);

    GUI2HtmlGenerator htmlGenerator = new GUI2HtmlGenerator(genSetup);
    htmlGenerator.generate(ast);

    Path html = outDir.toPath().resolve("genjava/decorator/TestTextInputAutoValidity.html");
    String content = Files.readString(html);

    assertTrue(content.contains("[validity] = 'getAutoValidityForAngular__ChangeDetection("));
  }

}
