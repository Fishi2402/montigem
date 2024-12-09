package umlp.generator.gui.gentypescript.standalone_config;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.io.paths.MCPath;
import org.junit.jupiter.api.Test;
import umlp.UMLPMill;
import umlp.generator.gui.DomainGUITest;
import umlp.generator.gui.GUIComponentStat;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.genhtml.HTMLHelper;
import umlp.generator.gui.gentypescript.java2ts.GUIComponentAnnotationDecoratorAndStandaloneConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GUIComponentAnnotationDecoratorAndStandaloneConfigTest extends DomainGUITest {

  @Test
  public void testRecursiveComponentImportsStandaloneConfig() {
    var generator = new GUIComponentAnnotationDecoratorAndStandaloneConfig(null, null, null) {
      @Override
      public HashSet<String> calculateImports(List<ASTGUIComponent> guiComponents) {
        return super.calculateImports(guiComponents);
      }
    };

    ASTGUICompilationUnit ast = processGUIModel(
        "gui/gentypescript/standalone_config/DirNode.gui",
        "src/test/resources/gui"
    );
    ASTGUIComponentDeclaration decl = ast.getGUIComponentDeclaration();
    GUIComponentStat stat = GUIComponentStat.getInstance();
    List<ASTGUIComponent> components = stat.getComponents(decl);
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("genHelper", GUIGeneratorUtil.getInstance());
    GUIComponentsVisitor componentsVisitor =  new GUIComponentsVisitor(ast, glex);

    HashSet<String> calculatedImports = generator.calculateImports(componentsVisitor.getComponents());

    assertFalse(calculatedImports.stream().anyMatch(s -> s.endsWith("DirNode")));
  }



}
