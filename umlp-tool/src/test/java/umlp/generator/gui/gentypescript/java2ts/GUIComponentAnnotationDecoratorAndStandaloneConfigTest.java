package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import org.junit.jupiter.api.Test;
import umlp.generator.gui.DomainGUITest;
import umlp.generator.gui.gentypescript.decorator.TSImportHelperDummy;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class GUIComponentAnnotationDecoratorAndStandaloneConfigTest extends DomainGUITest {

  @Test
  public void testRecursiveComponentImportsStandaloneConfig() {
    GUIComponentAnnotationDecoratorAndStandaloneConfig generator = new GUIComponentAnnotationDecoratorAndStandaloneConfig(
        null, null, null, null
    );
    ASTGUICompilationUnit ast = processGUIModel(
        "gui/gentypescript/java2ts/DirNode.gui",
        "src/test/resources/gui"
    );
    ASTGUIComponentDeclaration decl = ast.getGUIComponentDeclaration();

    Set<String> imports = new HashSet<>();
    generator.imports = imports;
    generator.gui = ast;
    generator.importHelper = new TSImportHelperDummy();
    decl.accept(generator.traverser);

    assertFalse(imports.stream().anyMatch(s -> s.endsWith("DirNode")));
  }

}
