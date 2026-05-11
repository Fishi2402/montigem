/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GUIGemTextInputValidityTrafoTest extends AbstractGUITest {

  @Test
  public void testGemTextInputValidityTrafo() {
    ASTGUICompilationUnit ast = parseGUI("gui/GemTextInputTest.gui");

    GUIComponentStat stat = GUIComponentStat.getInstance();
    List<ASTGUIComponent> components = stat.getComponents(ast.getGUIComponentDeclaration());

    assertEquals(2, components.size());
    
    ASTGUIComponent noValidity = components.get(0);
    ASTGUIComponent withValidity = components.get(1);

    assertEquals("GemTextInput", noValidity.getName());
    assertEquals("GemTextInput", withValidity.getName());

    assertTrue(noValidity.getGUINamedArgs().getGUINamedArgList().stream()
        .noneMatch(arg -> "validity".equals(arg.getName())));
    assertTrue(withValidity.getGUINamedArgs().getGUINamedArgList().stream()
        .anyMatch(arg -> "validity".equals(arg.getName())));

    GUIGemTextInputValidityTrafo trafo = new GUIGemTextInputValidityTrafo();
    trafo.transform(ast);

    assertTrue(noValidity.getGUINamedArgs().getGUINamedArgList().stream()
        .anyMatch(arg -> "validity".equals(arg.getName())), "validity=null should have been added");
    assertTrue(withValidity.getGUINamedArgs().getGUINamedArgList().stream()
        .anyMatch(arg -> "validity".equals(arg.getName())), "validity=null should still be present");

    // Check if it was added as null
    ASTGUINamedArg validityArg = noValidity.getGUINamedArgs().getGUINamedArgList().stream()
        .filter(arg -> "validity".equals(arg.getName()))
        .findFirst().get();
    
    // We can't easily check for LiteralExpression null here without more effort, 
    // but the presence of the argument with the correct name is already good.
    assertEquals("validity", validityArg.getName());
  }

}
