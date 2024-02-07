/* (c) https://github.com/MontiCore/monticore */
package guiTest.generator.gui;

import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import guiTest.AbstractGUITest;
import guiTest.GUIComponentStat;
import org.junit.jupiter.api.Test;
import umlp.generator.gui.GUINodeIdTrafo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GUINodeIdTrafoTest extends AbstractGUITest {

  @Test
  public void testOverviewIdTrafo() {
    ASTGUICompilationUnit ast = parseGUI("gui/person/Overview.gui");

    ASTGUIComponentDeclaration componentDecl = ast.getGUIComponentDeclaration();

    GUIComponentStat stat = GUIComponentStat.getInstance();
    List<ASTGUIComponent> components = stat.getComponents(componentDecl);
    List<ASTGUIGuard> guards = stat.getGuards(componentDecl);
    List<ASTGUIIterate> iterates = stat.getIterates(componentDecl);

    assertAll(
        () -> assertFalse(components.get(0).isPresentId()),
        () -> assertFalse(components.get(1).isPresentId()),
        () -> assertFalse(components.get(2).isPresentId()),
        () -> assertFalse(components.get(3).isPresentId()),
        () -> assertTrue(components.get(4).isPresentId()),
        () -> assertFalse(components.get(5).isPresentId()),
        () -> assertFalse(guards.get(0).isPresentId()),
        () -> assertFalse(iterates.get(0).isPresentId())
    );

    GUINodeIdTrafo trafo = new GUINodeIdTrafo();
    trafo.transform(ast);

    assertAll(
        () -> assertEquals("gemColumn0", components.get(0).getId().getName()),
        () -> assertEquals("create1", components.get(1).getId().getName()),
        () -> assertEquals("row4", components.get(2).getId().getName()),
        () -> assertEquals("gemText6", components.get(3).getId().getName()),
        () -> assertEquals("gemText5", components.get(4).getId().getName()),
        () -> assertEquals("gemText7", components.get(5).getId().getName()),
        () -> assertEquals("guard3", guards.get(0).getId().getName()),
        () -> assertEquals("it2", iterates.get(0).getId().getName())
    );
  }

}
