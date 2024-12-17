package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import org.junit.jupiter.api.Test;
import umlp.generator.gui.DomainGUITest;
import umlp.generator.gui.GUIComponentStat;
import umlp.generator.gui.gentypescript.decorator.TSImportHelperDummy;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class GUIComponentAnnotationDecoratorAndStandaloneConfigTest extends DomainGUITest {

  @Test
  public void testRecursiveComponentImportsStandaloneConfig() {
    GUIComponentAnnotationDecoratorAndStandaloneConfig generator = new GUIComponentAnnotationDecoratorAndStandaloneConfig(null, null, null);
    ASTGUICompilationUnit ast = processGUIModel(
        "gui/gentypescript/java2ts/DirNode.gui",
        "src/test/resources/gui"
    );
    ASTGUIComponentDeclaration decl = ast.getGUIComponentDeclaration();
    GUIComponentStat stat = GUIComponentStat.getInstance();
    List<ASTGUIComponent> components = stat.getComponents(decl);

    HashSet<String> calculatedImports = generator.calculateImports("DirNode", components, new TSImportHelperDummy());

    assertFalse(calculatedImports.stream().anyMatch(s -> s.endsWith("DirNode")));
  }

}
