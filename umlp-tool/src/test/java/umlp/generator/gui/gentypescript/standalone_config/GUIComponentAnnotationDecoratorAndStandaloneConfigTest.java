package umlp.generator.gui.gentypescript.standalone_config;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.io.paths.MCPath;
import org.junit.jupiter.api.Test;
import umlp.UMLPMill;
import umlp.generator.gui.DomainGUITest;
import umlp.generator.gui.GUIComponentStat;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.gentypescript.java2ts.GUIComponentAnnotationDecoratorAndStandaloneConfig;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GUIComponentAnnotationDecoratorAndStandaloneConfigTest extends DomainGUITest {

  @Test
  public void testRecursiveComponentImportsStandaloneConfig() {
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("genHelper", GUIGeneratorUtil.getInstance());
    MCPath hwcPath = new MCPath();
    hwcPath.addEntry(Paths.get("src/test/resources/gui"));
    Path outPath = Paths.get(BUILD_FOLDER + "/generated-test-sources/gui");

    ASTGUICompilationUnit ast;
    ast = processGUIModel("gui/gentypescript/standalone_config/DirNode.gui", "src/test/resources/gui");

    GUIComponentAnnotationDecoratorAndStandaloneConfig generator = new GUIComponentAnnotationDecoratorAndStandaloneConfig(glex, hwcPath, outPath);
    ASTCDCompilationUnit astCD = parse("src/test/resources", "gui/Domain.umlp");
    GUIComponentsVisitor componentsVisitor =  new GUIComponentsVisitor(ast, glex);
    generator.transform(astCD, null, componentsVisitor);

    assertNoErrors();
  }

}
