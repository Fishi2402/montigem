/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.prettyprinter;

import com.google.common.collect.Iterables;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.prettyprint.IndentPrinter;
import org.junit.jupiter.api.Test;
import umlp.generator.gui.DomainGUITest;
import umlp.generator.gui.GUIComponentStat;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.genjava.decorator.StaticObjectsCollector;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static umlp.generator.gui.TestUtils.removeSpaces;

public class Expression2AccessorPrinterTest extends DomainGUITest {

  @Test
  public void testAccessorConversion1() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;
    List<ASTGUIGuard> guards;
    List<ASTGUIIterate> iterates;

    Expression2AccessorPrinter printer = new Expression2AccessorPrinter(new IndentPrinter(), true);

    componentDecl = processGUIModel(
        "gui/person/Overview.gui",
        "src/test/resources/gui"
    ).getGUIComponentDeclaration();

    components = stat.getComponents(componentDecl);
    guards = stat.getGuards(componentDecl);
    iterates = stat.getIterates(componentDecl);

    // there is only one guard in Overview.gui
    ASTGUIGuard guard = guards.get(0);
    String expect1 = removeSpaces("this.getP().getAddress().getStreet() != \"Ahornstrasse\"");
    String result1 = removeSpaces(printer.prettyprint(guard.getCondition()));

    // there is only one iterate in Overview.gui
    ASTGUIIterate iterate = iterates.get(0);
    ASTGUIShortIterateControl control = ((ASTGUIShortIterateControl) iterate.getIterateControl());
    String expect2 = removeSpaces("this.getPersons()");
    String result2 = removeSpaces(printer.prettyprint(control.getIterable()));

    // component with id gemText5
    ASTGUIComponent gemText5 = components.get(4);
    String expect3 = removeSpaces("this.getP().getName()");
    String result3 = removeSpaces(printer.prettyprint(
        gemText5.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    // GemText component showing address in form: "<street> <house>"
    ASTGUIComponent address = components.get(5);
    String expect4 = removeSpaces("this.getP().getAddress().getStreet() + \" \" + this.getP().getAddress().getHouse()");
    String result4 = removeSpaces(printer.prettyprint(
        address.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    assertAll(
        () -> assertEquals(expect1, result1),
        () -> assertEquals(expect2, result2),
        () -> assertEquals(expect3, result3),
        () -> assertEquals(expect4, result4)
    );
  }

  @Test
  public void testAccessorConversion2() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;

    Expression2AccessorPrinter printer = new Expression2AccessorPrinter(new IndentPrinter(), true);

    componentDecl = processGUIModel(
        "gui/example_input/GemCheckBoxInputExample.gui",
        "src/test/resources/gui"
    ).getGUIComponentDeclaration();

    components = stat.getComponents(componentDecl);

    // the only component in GemCheckBoxInputExample.gui
    ASTGUIComponent checkbox = components.get(0);
    // GemCheckBoxInput#labelText
    String expect1 = removeSpaces("this.getP().getName() + \" is employed\"");
    String result1 = removeSpaces(printer.prettyprint(
        checkbox.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    // GemCheckBoxInput#checked
    String expect2 = removeSpaces("this.getP().isEmployed()");
    String result2 = removeSpaces(printer.prettyprint(
        checkbox.getGUINamedArgs().getGUINamedArg(1).getGUIArgValue()));

    assertAll(
        () -> assertEquals(expect1, result1),
        () -> assertEquals(expect2, result2)
    );
  }

  @Test
  public void testAccessorConversion3() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;

    Expression2AccessorPrinter printer = new Expression2AccessorPrinter(new IndentPrinter(), true);

    componentDecl = processGUIModel(
        "gui/person/Create.gui",
        "src/test/resources/gui"
    ).getGUIComponentDeclaration();

    components = stat.getComponents(componentDecl);

    // GemButton
    ASTGUIComponent button = Iterables.getLast(components);
    // GemButton#leftClick
    String expect1 = removeSpaces("this.create(this.getPerson())");
    String result1 = removeSpaces(printer.prettyprint(
        button.getGUINamedArgs().getGUINamedArg(1).getGUIArgValue()));

    assertAll(
        () -> assertEquals(expect1, result1)
    );
  }

  @Test
  public void testAccessorConversion4() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;

    Expression2AccessorPrinter printer = new Expression2AccessorPrinter(new IndentPrinter(), true);

    componentDecl = processGUIModel(
        "gui/genjava/Prettyprinter.gui",
        "src/test/resources/gui"
    ).getGUIComponentDeclaration();

    GUIGeneratorUtil.getInstance().init(List.of("Domain"), Collections.emptyList());

    components = stat.getComponents(componentDecl);

    ASTGUIComponent call = stat.getCompById(components, "call");
    ASTGUIComponent ternary = stat.getCompById(components, "ternary");
    ASTGUIComponent fieldCall1 = stat.getCompById(components, "fieldCall1");
    ASTGUIComponent fieldCall2 = stat.getCompById(components, "fieldCall2");
    ASTGUIComponent fieldCall3 = stat.getCompById(components, "fieldCall3");
    ASTGUIComponent fieldCall4 = stat.getCompById(components, "fieldCall4");
    ASTGUIComponent fieldCall5 = stat.getCompById(components, "fieldCall5");
    ASTGUIComponent fieldCall6 = stat.getCompById(components, "fieldCall6");
    ASTGUIComponent literal = stat.getCompById(components, "literal");

    String expect1 = removeSpaces("this.getAge()");
    String result1 = removeSpaces(printer.prettyprint(
        call.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect2 = removeSpaces("this.getP().getFriendsList().size() > 0"
        + " ? this.getP().getFriendsList().get(this.getP().getFriendsList().size() - 1).getAge()"
        + " : \"no friends\"");
    String result2 = removeSpaces(printer.prettyprint(
        ternary.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect3 = removeSpaces("this.getP().getAge()");
    String result3 = removeSpaces(printer.prettyprint(
        fieldCall1.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect4 = removeSpaces("this.getP().getFriendsList().get(0).getAge()");
    String result4 = removeSpaces(printer.prettyprint(
        fieldCall2.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect5 = removeSpaces("this.getPersons().get(0).getAge()");
    String result5 = removeSpaces(printer.prettyprint(
        fieldCall3.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect6 = removeSpaces("this.getPersons().get(0).eat()");
    String result6 = removeSpaces(printer.prettyprint(
        fieldCall4.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect7 = removeSpaces("\"bob\"");
    String result7 = removeSpaces(printer.prettyprint(
        literal.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect8 = removeSpaces("this.getPersons().get(0).getStatus()");
    String result8 = removeSpaces(printer.prettyprint(
        fieldCall5.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect9 = removeSpaces("MaritalStatus.MARRIED");
    String result9 = removeSpaces(printer.prettyprint(
        fieldCall6.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    assertAll(
        () -> assertEquals(expect1, result1),
        () -> assertEquals(expect2, result2),
        () -> assertEquals(expect3, result3),
        () -> assertEquals(expect4, result4),
        () -> assertEquals(expect5, result5),
        () -> assertEquals(expect6, result6),
        () -> assertEquals(expect7, result7),
        () -> assertEquals(expect8, result8),
        () -> assertEquals(expect9, result9)
    );
  }

  @Test
  public void testAccessorConversion5() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;
    List<ASTGUIGuard> guards;
    List<ASTGUIIterate> iterates;

    componentDecl = processGUIModel(
        "gui/person/Overview.gui",
        "src/test/resources/gui"
    ).getGUIComponentDeclaration();

    IterationItemsFinder finder = new IterationItemsFinder();
    Collection<ASTExpression> iterationItems = new HashSet<>(finder.getItems(componentDecl));
    Expression2AccessorPrinter printer = new Expression2AccessorPrinter(
        new IndentPrinter(), true, iterationItems, iterationItems
    );

    components = stat.getComponents(componentDecl);
    guards = stat.getGuards(componentDecl);
    iterates = stat.getIterates(componentDecl);

    // there is only one guard in Overview.gui
    ASTGUIGuard guard = guards.get(0);
    String expect1 = removeSpaces("p.getAddress().getStreet() != \"Ahornstrasse\"");
    String result1 = removeSpaces(printer.prettyprint(guard.getCondition()));

    // there is only one iterate in Overview.gui
    ASTGUIIterate iterate = iterates.get(0);
    ASTGUIShortIterateControl control = ((ASTGUIShortIterateControl) iterate.getIterateControl());
    String expect2 = removeSpaces("this.getPersons()");
    String result2 = removeSpaces(printer.prettyprint(control.getIterable()));

    // component with id gemText5
    ASTGUIComponent gemText5 = components.get(4);
    String expect3 = removeSpaces("p.getName()");
    String result3 = removeSpaces(printer.prettyprint(
        gemText5.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    // GemText component showing address in form: "<street> <house>"
    ASTGUIComponent address = components.get(5);
    String expect4 = removeSpaces("p.getAddress().getStreet() + \" \" + p.getAddress().getHouse()");
    String result4 = removeSpaces(printer.prettyprint(
        address.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    assertAll(
        () -> assertEquals(expect1, result1),
        () -> assertEquals(expect2, result2),
        () -> assertEquals(expect3, result3),
        () -> assertEquals(expect4, result4)
    );
  }

  @Test
  public void testAccessorConversion6() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;
    List<ASTGUIGuard> guards;
    List<ASTGUIIterate> iterates;

    componentDecl = processGUIModel(
        "gui/genjava/decorator/StaticObjectsCollector.gui",
        "src/test/resources/gui"
    ).getGUIComponentDeclaration();

    StaticObjectsCollector.getInstance().init(componentDecl);

    IterationItemsFinder finder = new IterationItemsFinder();
    Collection<ASTExpression> iterationItems = new HashSet<>(finder.getItems(componentDecl));
    Expression2AccessorPrinter printer = new Expression2AccessorPrinter(
        new IndentPrinter(), true, iterationItems, iterationItems
    );

    components = stat.getComponents(componentDecl);
    guards = stat.getGuards(componentDecl);
    iterates = stat.getIterates(componentDecl);

    ASTGUIComponent a = stat.getCompById(components, "a");
    ASTGUIComponent b = stat.getCompById(components, "b");
    ASTGUIComponent c = stat.getCompById(components, "c");
    ASTGUIComponent d = stat.getCompById(components, "d");
    ASTGUIGuard e = stat.getGuardById(guards, "e");
    ASTGUIIterate f = stat.getIterById(iterates, "f");
    ASTGUIComponent g = stat.getCompById(components, "g");
    ASTGUIComponent h = stat.getCompById(components, "h");
    ASTGUIComponent i = stat.getCompById(components, "i");
    ASTGUIComponent k = stat.getCompById(components, "k");

    String expect1 = removeSpaces("this.getPerson()");
    String result1 = removeSpaces(printer.prettyprint(a.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect2 = removeSpaces("this.getBP_0()");
    String result2 = removeSpaces(printer.prettyprint(b.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect3 = removeSpaces("this.getCA_1()");
    String result3 = removeSpaces(printer.prettyprint(c.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect4 = removeSpaces("this.getDLp_4()");
    String result4 = removeSpaces(printer.prettyprint(d.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect5 = removeSpaces("this.getECondition_9().isEmployed()");
    String result5 = removeSpaces(printer.prettyprint(e.getCondition()));

    String expect6 = removeSpaces("this.getFIterable_11(p).getFriendsList()");
    String result6 = removeSpaces(printer.prettyprint(((ASTGUIShortIterateControl) f.getIterateControl()).getIterable()));

    String expect7 = removeSpaces("this.getGA_13(p2, p)");
    String result7 = removeSpaces(printer.prettyprint(g.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect8 = removeSpaces("this.getHA_16(p2, p)");
    String result8 = removeSpaces(printer.prettyprint(h.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect9 = removeSpaces("this.getIP_19(p2, p)");
    String result9 = removeSpaces(printer.prettyprint(i.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect10 = removeSpaces("this.getKMd_21(p2, p)");
    String result10 = removeSpaces(printer.prettyprint(k.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect11 = removeSpaces("this.getKLmd_22(p2, p)");
    String result11 = removeSpaces(printer.prettyprint(k.getGUINamedArgs().getGUINamedArg(2).getGUIArgValue()));

    assertAll(
        () -> assertEquals(expect1, result1),
        () -> assertEquals(expect2, result2),
        () -> assertEquals(expect3, result3),
        () -> assertEquals(expect4, result4),
        () -> assertEquals(expect5, result5),
        () -> assertEquals(expect6, result6),
        () -> assertEquals(expect7, result7),
        () -> assertEquals(expect8, result8),
        () -> assertEquals(expect9, result9),
        () -> assertEquals(expect10, result10),
        () -> assertEquals(expect11, result11)
    );
  }

}
