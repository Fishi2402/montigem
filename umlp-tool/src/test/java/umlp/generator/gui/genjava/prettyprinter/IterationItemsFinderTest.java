/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import umlp.generator.gui.DomainGUITest;
import umlp.generator.gui.GUIComponentStat;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("unused")
public class IterationItemsFinderTest extends DomainGUITest {

  @Test
  public void testGetIgnore() {
    ASTGUIComponentDeclaration decl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    IterationItemsFinder finder = new IterationItemsFinder();

    List<ASTGUIComponent> components;
    List<ASTGUIGuard> guards;
    List<ASTGUIIterate> iterates;

    decl = processGUIModel(
        "gui/genhtml/HTMLHelper.gui",
        "src/test/resources/gui"
    ).getGUIComponentDeclaration();

    components = stat.getComponents(decl);
    guards = stat.getGuards(decl);
    iterates = stat.getIterates(decl);

    ASTGUIComponent a = stat.getCompById(components, "a");
    ASTGUIIterate b = stat.getIterById(iterates, "b");
    ASTGUIComponent c = stat.getCompById(components, "c");
    ASTGUIGuard d = stat.getGuardById(guards, "d");
    ASTGUIIterate e = stat.getIterById(iterates, "e");
    ASTGUIComponent f = stat.getCompById(components, "f");
    ASTGUIGuard g = stat.getGuardById(guards, "g");
    ASTGUIComponent h = stat.getCompById(components, "h");
    ASTGUIComponent i = stat.getCompById(components, "i");
    ASTGUIComponent j = stat.getCompById(components, "j");
    ASTGUIComponent k = stat.getCompById(components, "k");
    ASTGUIComponent l = stat.getCompById(components, "l");
    ASTGUIComponent m = stat.getCompById(components, "m");
    ASTGUIComponent n = stat.getCompById(components, "n");
    ASTGUIComponent o = stat.getCompById(components, "o");
    ASTGUIComponent p = stat.getCompById(components, "p");
    ASTGUIGuard q = stat.getGuardById(guards, "q");
    ASTGUIComponent r = stat.getCompById(components, "r");
    ASTGUIComponent s = stat.getCompById(components, "s");
    ASTGUIIterate t = stat.getIterById(iterates, "t");
    ASTGUIIterate u = stat.getIterById(iterates, "u");
    ASTGUIComponent v = stat.getCompById(components, "v");
    ASTGUIComponent w = stat.getCompById(components, "w");
    ASTGUIComponent x = stat.getCompById(components, "x");
    ASTGUIGuard y = stat.getGuardById(guards, "y");
    ASTGUIGuard z = stat.getGuardById(guards, "z");
    ASTGUIIterate aa = stat.getIterById(iterates, "aa");
    ASTGUIIterate ab = stat.getIterById(iterates, "ab");
    ASTGUIGuard ac = stat.getGuardById(guards, "ac");

    List<Executable> assertions = new ArrayList<>();

    Collection<ASTExpression> result1 = finder.getItems(w);
    Collection<ASTExpression> result2 = finder.getItems(ac);
    Collection<ASTExpression> result3 = finder.getItems(g);

    assertions.add(() -> assertEquals(0, result1.size()));
    assertions.add(() -> assertEquals(1, result2.size()));
    assertions.add(() -> assertEquals(2, result3.size()));

    GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

    assertions.add(() -> assertTrue(result2.stream().anyMatch(exp -> util.print(exp).equals("s1"))));
    assertions.add(() -> assertTrue(result3.stream().anyMatch(exp -> util.print(exp).equals("s1"))));
    assertions.add(() -> assertTrue(result3.stream().anyMatch(exp -> util.print(exp).equals("s2"))));

    assertAll(assertions);
  }

}



