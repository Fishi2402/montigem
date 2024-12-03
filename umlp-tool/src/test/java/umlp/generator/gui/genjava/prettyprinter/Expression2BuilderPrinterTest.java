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
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;
import umlp.generator.gui.DomainGUITest;
import umlp.generator.gui.GUIComponentStat;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static umlp.generator.gui.TestUtils.removeSpaces;

public class Expression2BuilderPrinterTest extends DomainGUITest {

  @Test
  public void testBuilderConversion1() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;
    List<ASTGUIGuard> guards;
    List<ASTGUIIterate> iterates;

    Expression2BuilderPrinter printer = new Expression2BuilderPrinter(new IndentPrinter(), true);

    componentDecl = processGUIModel(
        "gui/person/Overview.gui",
        "src/test/resources/gui"
    ).getGUIComponentDeclaration();

    components = stat.getComponents(componentDecl);
    guards = stat.getGuards(componentDecl);
    iterates = stat.getIterates(componentDecl);

    // there is only one guard in Overview.gui
    ASTGUIGuard guard = guards.get(0);
    String expect1 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "p.address.street != \"Ahornstrasse\""
        + "'. FieldAccess2BuilderConverter cannot produce setter."
    );
    printer.prettyprint(guard.getCondition());
    String result1 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    // there is only one iterate in Overview.gui
    ASTGUIIterate iterate = iterates.get(0);
    ASTGUIShortIterateControl control = ((ASTGUIShortIterateControl) iterate.getIterateControl());
    String expect2 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "persons"
        + "'. Name2BuilderConverter cannot produce setter."
    );
    printer.prettyprint(control.getIterable());
    String result2 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    // component with id gemText5
    ASTGUIComponent gemText5 = components.get(4);
    String expect3 = removeSpaces("this.getP().name(val_$)");
    String result3 = removeSpaces(printer.prettyprint(
        gemText5.getGUINamedArgs().getGUINamedArg(0).getExpression()));

    // GemText component showing address in form: "<street> <house>"
    ASTGUIComponent address = components.get(5);
    String expect4 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "p.address.street + \" \" + p.address.house"
        + "'. FieldAccess2BuilderConverter cannot produce setter."
    );
    printer.prettyprint(address);
    String result4 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    assertAll(
        () -> assertEquals(expect1, result1),
        () -> assertEquals(expect2, result2),
        () -> assertEquals(expect3, result3),
        () -> assertEquals(expect4, result4)
    );
  }

  @Test
  public void testBuilderConversion2() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;

    Expression2BuilderPrinter printer = new Expression2BuilderPrinter(new IndentPrinter(), true);

    componentDecl = processGUIModel(
        "gui/example_input/GemCheckBoxInputExample.gui",
        "src/test/resources/gui"
    ).getGUIComponentDeclaration();

    components = stat.getComponents(componentDecl);

    // the only component in GemCheckBoxInputExample.gui
    ASTGUIComponent checkbox = components.get(0);
    // GemCheckBoxInput#labelText
    String expect1 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "p.name + \" is employed\""
        + "'. FieldAccess2BuilderConverter cannot produce setter."
    );
    printer.prettyprint(checkbox);
    String result1 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    // GemCheckBoxInput#checked
    String expect2 = removeSpaces("this.getP().employed(val_$)");
    String result2 = removeSpaces(printer.prettyprint(
        checkbox.getGUINamedArgs().getGUINamedArg(1).getExpression()));

    assertAll(
        () -> assertEquals(expect1, result1),
        () -> assertEquals(expect2, result2)
    );
  }

  @Test
  public void testBuilderConversion3() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;

    Expression2BuilderPrinter printer = new Expression2BuilderPrinter(new IndentPrinter(), true);

    componentDecl = processGUIModel(
        "gui/person/Create.gui",
        "src/test/resources/gui"
    ).getGUIComponentDeclaration();

    components = stat.getComponents(componentDecl);

    // GemButton
    ASTGUIComponent button = Iterables.getLast(components);
    // GemButton#leftClick
    String expect1 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "create(person)"
        + "'. FieldAccess2BuilderConverter cannot produce setter."
    );
    printer.prettyprint(button);
    String result1 = removeSpaces(Log.getFindings().get(1).getMsg());
    Log.clearFindings();

    assertAll(
        () -> assertEquals(expect1, result1)
    );
  }

  @Test
  public void testBuilderConversion4() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;

    Expression2BuilderPrinter printer = new Expression2BuilderPrinter(new IndentPrinter(), true);

    componentDecl = processGUIModel(
        "gui/genjava/Prettyprinter.gui",
        "src/test/resources/gui"
    ).getGUIComponentDeclaration();

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

    String expect1 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "getAge()"
        + "'. FieldAccess2BuilderConverter cannot produce setter."
    );
    printer.prettyprint(call);
    String result1 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    String expect2 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "p.friends.size() > 0 ? p.friends.get(p.friends.size() - 1).getAge() : \"no friends\""
        + "'. FieldAccess2BuilderConverter cannot produce setter."
    );
    printer.prettyprint(ternary);
    String result2 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    String expect3 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "p.getAge()"
        + "'. FieldAccess2BuilderConverter cannot produce setter."
    );
    printer.prettyprint(fieldCall1);
    String result3 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    String expect4 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "p.friends.get(0).getAge()"
        + "'. FieldAccess2BuilderConverter cannot produce setter."
    );
    printer.prettyprint(fieldCall2);
    String result4 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    String expect5 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "persons.get(0).getAge()"
        + "'. FieldAccess2BuilderConverter cannot produce setter."
    );
    printer.prettyprint(fieldCall3);
    String result5 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    String expect6 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "persons.get(0).eat()"
        + "'. FieldAccess2BuilderConverter cannot produce setter."
    );
    printer.prettyprint(fieldCall4);
    String result6 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    String expect7 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "\"bob\""
        + "'. Name2BuilderConverter cannot produce setter."
    );
    printer.prettyprint(literal);
    String result7 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    String expect8 = removeSpaces("this.getPersons().get(0).status(val_$)");
    String result8 = removeSpaces(printer.prettyprint(
        fieldCall5.getGUINamedArgs().getGUINamedArg(0).getExpression()));

    String expect9 = removeSpaces("0x0000B Expected a mutable value, but got "
        + "MaritalStatus.MARRIED");
    printer.prettyprint(fieldCall6);
    String result9 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();


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
  public void testBuilderConversion5() {
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
    Expression2BuilderPrinter printer = new Expression2BuilderPrinter(
        new IndentPrinter(), true, iterationItems, iterationItems
    );

    components = stat.getComponents(componentDecl);
    guards = stat.getGuards(componentDecl);
    iterates = stat.getIterates(componentDecl);

    // there is only one guard in Overview.gui
    ASTGUIGuard guard = guards.get(0);
    String expect1 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "p.address.street != \"Ahornstrasse\""
        + "'. FieldAccess2BuilderConverter cannot produce setter."
    );
    printer.prettyprint(guard.getCondition());
    String result1 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    // there is only one iterate in Overview.gui
    ASTGUIIterate iterate = iterates.get(0);
    ASTGUIShortIterateControl control = ((ASTGUIShortIterateControl) iterate.getIterateControl());
    String expect2 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "persons"
        + "'. Name2BuilderConverter cannot produce setter."
    );
    printer.prettyprint(control.getIterable());
    String result2 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    // component with id gemText5
    ASTGUIComponent gemText5 = components.get(4);
    String expect3 = removeSpaces("p.name(val_$)");
    String result3 = removeSpaces(printer.prettyprint(
        gemText5.getGUINamedArgs().getGUINamedArg(0).getExpression()));

    // GemText component showing address in form: "<street> <house>"
    ASTGUIComponent address = components.get(5);
    String expect4 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "p.address.street + \" \" + p.address.house"
        + "'. FieldAccess2BuilderConverter cannot produce setter."
    );
    printer.prettyprint(address);
    String result4 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    assertAll(
        () -> assertEquals(expect1, result1),
        () -> assertEquals(expect2, result2),
        () -> assertEquals(expect3, result3),
        () -> assertEquals(expect4, result4)
    );
  }

}
