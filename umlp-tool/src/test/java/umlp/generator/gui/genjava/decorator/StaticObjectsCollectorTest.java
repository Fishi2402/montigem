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
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.TestUtils;

import java.util.ArrayList;
import java.util.Collections;
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

    GUIGeneratorUtil.getInstance().init(List.of("Domain"), Collections.emptyList());
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

    Map<ASTNode, Attribute> node2Attr = StaticObjectsCollector.getInstance().getNode2Attr();

    assertions.add(() -> assertEquals(12, node2Attr.size()));

    Attribute bP0Wrapper = node2Attr.get(
        stat.getArgByName(b, "p").getGUIArgValue().getExpression());
    ASTCDAttribute bP0 = bP0Wrapper.getCdAttr();
    assertions.add(() -> assertEquals("Person", bP0.getMCType().printType()));
    assertions.add(() -> assertEquals("bP_0", bP0.getName()));
    assertions.add(() -> assertEquals(0, bP0Wrapper.getIterateArgs().size()));

    Attribute cA1Wrapper = node2Attr.get(
        stat.getArgByName(c, "a").getGUIArgValue().getGUIList());
    ASTCDAttribute cA1 = cA1Wrapper.getCdAttr();
    assertions.add(() -> assertEquals("List<Address>", cA1.getMCType().printType()));
    assertions.add(() -> assertEquals("cA_1", cA1.getName()));
    assertions.add(() -> assertEquals(0, cA1Wrapper.getIterateArgs().size()));

    Attribute cA2Wrapper = node2Attr.get(
        stat.getArgByName(c, "a").getGUIArgValue().getGUIList()
            .getGUIArgValue(1).getExpression());
    ASTCDAttribute cA2 = cA2Wrapper.getCdAttr();
    assertions.add(() -> assertEquals("Address", cA2.getMCType().printType()));
    assertions.add(() -> assertEquals("cA_2", cA2.getName()));
    assertions.add(() -> assertEquals(0, cA2Wrapper.getIterateArgs().size()));

    Attribute dLp3Wrapper = node2Attr.get(
        stat.getArgByName(d, "lp").getGUIArgValue().getGUIList());
    ASTCDAttribute dLp3 = dLp3Wrapper.getCdAttr();
    assertions.add(() -> assertEquals("List<List<Person>>", dLp3.getMCType().printType()));
    assertions.add(() -> assertEquals("dLp_3", dLp3.getName()));
    assertions.add(() -> assertEquals(0, dLp3Wrapper.getIterateArgs().size()));

    Attribute dLp4Wrapper = node2Attr.get(
        stat.getArgByName(d, "lp").getGUIArgValue().getGUIList()
            .getGUIArgValue(0).getGUIList());
    ASTCDAttribute dLp4 = dLp4Wrapper.getCdAttr();
    assertions.add(() -> assertEquals("List<Person>", dLp4.getMCType().printType()));
    assertions.add(() -> assertEquals("dLp_4", dLp4.getName()));
    assertions.add(() -> assertEquals(0, dLp4Wrapper.getIterateArgs().size()));

    Attribute dLp5Wrapper = node2Attr.get(
        stat.getArgByName(d, "lp").getGUIArgValue().getGUIList()
            .getGUIArgValue(0).getGUIList().getGUIArgValue(1).getExpression());
    ASTCDAttribute dLp5 = dLp5Wrapper.getCdAttr();
    assertions.add(() -> assertEquals("Person", dLp5.getMCType().printType()));
    assertions.add(() -> assertEquals("dLp_5", dLp5.getName()));
    assertions.add(() -> assertEquals(0, dLp5Wrapper.getIterateArgs().size()));

    Attribute dLp6Wrapper = node2Attr.get(
        stat.getArgByName(d, "lp").getGUIArgValue().getGUIList()
            .getGUIArgValue(1).getGUIList());
    ASTCDAttribute dLp6 = dLp6Wrapper.getCdAttr();
    assertions.add(() -> assertEquals("List<Person>", dLp6.getMCType().printType()));
    assertions.add(() -> assertEquals("dLp_6", dLp6.getName()));
    assertions.add(() -> assertEquals(0, dLp6Wrapper.getIterateArgs().size()));

    Attribute eCondition7Wrapper = node2Attr.get(
        ((ASTFieldAccessExpression) e.getCondition()).getExpression());
    ASTCDAttribute eCondition7 = eCondition7Wrapper.getCdAttr();
    assertions.add(() -> assertEquals("Person", eCondition7.getMCType().printType()));
    assertions.add(() -> assertEquals("eCondition_7", eCondition7.getName()));
    assertions.add(() -> assertEquals(0, eCondition7Wrapper.getIterateArgs().size()));

    Attribute fIterable8Wrapper = node2Attr.get(
        ((ASTFieldAccessExpression) ((ASTGUIShortIterateControl) f.getIterateControl())
            .getIterable()).getExpression());
    ASTCDAttribute fIterable8 = fIterable8Wrapper.getCdAttr();
    assertions.add(() -> assertEquals("Person", fIterable8.getMCType().printType()));
    assertions.add(() -> assertEquals("fIterable_8", fIterable8.getName()));
    assertions.add(() -> assertEquals(0, fIterable8Wrapper.getIterateArgs().size()));

    Attribute gA9Wrapper = node2Attr.get(
        stat.getArgByName(g, "a").getGUIArgValue().getGUIList());
    ASTCDAttribute gA9 = gA9Wrapper.getCdAttr();
    assertions.add(() -> assertEquals(
        TestUtils.removeSpaces("Map<Person, Map<Person, List<Address>>>"),
        TestUtils.removeSpaces(gA9.getMCType().printType())
    ));
    assertions.add(() -> assertEquals("gA_9", gA9.getName()));
    assertions.add(() -> assertEquals(2, gA9Wrapper.getIterateArgs().size()));

    Attribute hA10Wrapper = node2Attr.get(
        stat.getArgByName(h, "a").getGUIArgValue().getGUIList());
    ASTCDAttribute hA10 = hA10Wrapper.getCdAttr();
    assertions.add(() -> assertEquals(
        TestUtils.removeSpaces("Map<Person, Map<Person, List<Address>>>"),
        TestUtils.removeSpaces(hA10.getMCType().printType())
    ));
    assertions.add(() -> assertEquals("hA_10", hA10.getName()));
    assertions.add(() -> assertEquals(2, hA10Wrapper.getIterateArgs().size()));

    Attribute iP11Wrapper = node2Attr.get(
        stat.getArgByName(i, "p").getGUIArgValue().getExpression());
    ASTCDAttribute iP11 = iP11Wrapper.getCdAttr();
    assertions.add(() -> assertEquals(
        TestUtils.removeSpaces("Map<Person, Map<Person, Person>>"),
        TestUtils.removeSpaces(iP11.getMCType().printType())
    ));
    assertions.add(() -> assertEquals("iP_11", iP11.getName()));
    assertions.add(() -> assertEquals(2, iP11Wrapper.getIterateArgs().size()));

    boolean contains_jV12 = node2Attr.containsKey(
        stat.getArgByName(j, "v").getGUIArgValue().getGUIList());
    assertions.add(() -> assertFalse(contains_jV12));

    assertAll(assertions);
  }

}



