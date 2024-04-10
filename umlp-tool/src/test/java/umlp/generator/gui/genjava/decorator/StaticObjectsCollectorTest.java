/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.ast.ASTNode;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import umlp.generator.gui.DomainGUITest;
import umlp.generator.gui.GUIComponentStat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SuppressWarnings("unused")
public class StaticObjectsCollectorTest extends DomainGUITest {

  @Test
  public void testGetNode2Attr() {
    ASTGUIComponentDeclaration decl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;
    List<ASTGUIGuard> guards;
    List<ASTGUIIterate> iterates;

    decl = processGUIModel(
        "gui/genjava/decorator/StaticObjectsCollector.gui",
        "src/test/resources/gui"
    ).getGUIComponentDeclaration();

    StaticObjectsCollector.getInstance().init(decl);

    components = stat.getComponents(decl);
    guards = stat.getGuards(decl);
    iterates = stat.getIterates(decl);

    ASTGUIComponent a = stat.getCompById(components, "a");
    ASTGUIComponent b = stat.getCompById(components, "b");
    ASTGUIComponent c = stat.getCompById(components, "c");
    ASTGUIComponent d = stat.getCompById(components, "d");
    ASTGUIGuard e = stat.getGuardById(guards, "e");
    ASTGUIIterate f = stat.getIterById(iterates, "f");
    ASTGUIComponent g = stat.getCompById(components, "g");
    ASTGUIComponent h = stat.getCompById(components, "h");
    ASTGUIComponent i = stat.getCompById(components, "i");
    ASTGUIComponent j = stat.getCompById(components, "j");

    List<Executable> assertions = new ArrayList<>();

    Map<ASTNode, ASTCDAttribute> node2Attr = StaticObjectsCollector.getInstance().getNode2Attr();

    assertions.add(() -> assertEquals(12, node2Attr.size()));

    ASTCDAttribute bP0 = node2Attr.get(
        stat.getArgByName(b, "p").getGUIArgValue().getExpression());
    assertions.add(() -> assertEquals("Person", bP0.getMCType().printType()));
    assertions.add(() -> assertEquals("bP_0", bP0.getName()));

    ASTCDAttribute cA1 = node2Attr.get(
        stat.getArgByName(c, "a").getGUIArgValue().getGUIList());
    assertions.add(() -> assertEquals("List<Address>", cA1.getMCType().printType()));
    assertions.add(() -> assertEquals("cA_1", cA1.getName()));

    ASTCDAttribute cA2 = node2Attr.get(
        stat.getArgByName(c, "a").getGUIArgValue().getGUIList().getGUIArgValue(1).getExpression());
    assertions.add(() -> assertEquals("Address", cA2.getMCType().printType()));
    assertions.add(() -> assertEquals("cA_2", cA2.getName()));

    ASTCDAttribute dLp3 = node2Attr.get(
        stat.getArgByName(d, "lp").getGUIArgValue().getGUIList());
    assertions.add(() -> assertEquals("List<List<Person>>", dLp3.getMCType().printType()));
    assertions.add(() -> assertEquals("dLp_3", dLp3.getName()));

    ASTCDAttribute dLp4 = node2Attr.get(
        stat.getArgByName(d, "lp").getGUIArgValue().getGUIList().getGUIArgValue(0).getGUIList());
    assertions.add(() -> assertEquals("List<Person>", dLp4.getMCType().printType()));
    assertions.add(() -> assertEquals("dLp_4", dLp4.getName()));

    ASTCDAttribute dLp5 = node2Attr.get(
        stat.getArgByName(d, "lp").getGUIArgValue().getGUIList().getGUIArgValue(0).getGUIList().getGUIArgValue(1).getExpression());
    assertions.add(() -> assertEquals("Person", dLp5.getMCType().printType()));
    assertions.add(() -> assertEquals("dLp_5", dLp5.getName()));

    ASTCDAttribute dLp6 = node2Attr.get(
        stat.getArgByName(d, "lp").getGUIArgValue().getGUIList().getGUIArgValue(1).getGUIList());
    assertions.add(() -> assertEquals("List<Person>", dLp6.getMCType().printType()));
    assertions.add(() -> assertEquals("dLp_6", dLp6.getName()));

    ASTCDAttribute eCondition7 = node2Attr.get(
        ((ASTFieldAccessExpression) e.getCondition()).getExpression());
    assertions.add(() -> assertEquals("Person", eCondition7.getMCType().printType()));
    assertions.add(() -> assertEquals("eCondition_7", eCondition7.getName()));

    ASTCDAttribute fIterable8 = node2Attr.get(
        ((ASTFieldAccessExpression) ((ASTGUIShortIterateControl) f.getIterateControl()).getIterable()).getExpression());
    assertions.add(() -> assertEquals("Person", fIterable8.getMCType().printType()));
    assertions.add(() -> assertEquals("fIterable_8", fIterable8.getName()));

    ASTCDAttribute gA9 = node2Attr.get(
        stat.getArgByName(g, "a").getGUIArgValue().getGUIList());
    assertions.add(() -> assertEquals("List<Address>", gA9.getMCType().printType()));
    assertions.add(() -> assertEquals("gA_9", gA9.getName()));

    ASTCDAttribute hA10 = node2Attr.get(
        stat.getArgByName(h, "a").getGUIArgValue().getGUIList());
    assertions.add(() -> assertEquals("List<Address>", hA10.getMCType().printType()));
    assertions.add(() -> assertEquals("hA_10", hA10.getName()));

    ASTCDAttribute iP11 = node2Attr.get(
        stat.getArgByName(i, "p").getGUIArgValue().getExpression());
    assertions.add(() -> assertEquals("Person", iP11.getMCType().printType()));
    assertions.add(() -> assertEquals("iP_11", iP11.getName()));

    boolean contains_jV12 = node2Attr.containsKey(
        stat.getArgByName(j, "v").getGUIArgValue().getGUIList());
    assertions.add(() -> assertFalse(contains_jV12));

    assertAll(assertions);
  }

}



