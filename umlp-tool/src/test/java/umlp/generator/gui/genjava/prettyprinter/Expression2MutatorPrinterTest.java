/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.prettyprinter;

import com.google.common.collect.Iterables;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIComponentStat;
import org.junit.jupiter.api.Test;

import java.util.List;

import static umlp.generator.gui.TestUtils.removeSpaces;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Expression2MutatorPrinterTest extends DomainGUITest {

  @Test
  public void testMutatorConversion1() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;
    List<ASTGUIGuard> guards;
    List<ASTGUIIterate> iterates;

    Expression2MutatorPrinter printer = new Expression2MutatorPrinter(new IndentPrinter(), true);

    componentDecl = processGUIModel("gui/person/Overview.gui", "src/test/resources/gui");

    components = stat.getComponents(componentDecl);
    guards = stat.getGuards(componentDecl);
    iterates = stat.getIterates(componentDecl);

    // there is only one guard in Overview.gui
    ASTGUIGuard guard = guards.get(0);
    String expect1 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "p.address.street != \"Ahornstrasse\""
        + "'. FieldAccess2MutatorConverter cannot produce setter."
    );
    printer.prettyprint(guard.getCondition());
    String result1 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    // there is only one iterate in Overview.gui
    ASTGUIIterate iterate = iterates.get(0);
    ASTGUIShortIterateControl control = ((ASTGUIShortIterateControl) iterate.getIterateControl());
    String expect2 = removeSpaces("this.setPersons(val)");
    String result2 = removeSpaces(printer.prettyprint(control.getIterable()));

    // component with id gemText5
    ASTGUIComponent gemText5 = components.get(4);
    String expect3 = removeSpaces("this.getP().setName(val)");
    String result3 = removeSpaces(printer.prettyprint(
        gemText5.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    // GemText component showing address in form: "<street> <house>"
    ASTGUIComponent address = components.get(5);
    String expect4 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "p.address.street + \" \" + p.address.house"
        + "'. FieldAccess2MutatorConverter cannot produce setter."
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
  public void testMutatorConversion2() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;

    Expression2MutatorPrinter printer = new Expression2MutatorPrinter(new IndentPrinter(), true);

    componentDecl = processGUIModel(
        "gui/example_input/GemCheckBoxInputExample.gui",
        "src/test/resources/gui"
    );

    components = stat.getComponents(componentDecl);

    // the only component in GemCheckboxInputExample.gui
    ASTGUIComponent checkbox = components.get(0);
    // GemCheckBoxInput#labelText
    String expect1 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "p.name + \" is employed\""
        + "'. FieldAccess2MutatorConverter cannot produce setter."
    );
    printer.prettyprint(checkbox);
    String result1 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    // GemCheckBoxInput#checked
    String expect2 = removeSpaces("this.getP().setEmployed(val)");
    String result2 = removeSpaces(printer.prettyprint(
        checkbox.getGUINamedArgs().getGUINamedArg(1).getGUIArgValue()));

    assertAll(
        () -> assertEquals(expect1, result1),
        () -> assertEquals(expect2, result2)
    );
  }

  @Test
  public void testMutatorConversion3() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;

    Expression2MutatorPrinter printer = new Expression2MutatorPrinter(new IndentPrinter(), true);

    componentDecl = processGUIModel(
        "gui/person/Create.gui",
        "src/test/resources/gui"
    );

    components = stat.getComponents(componentDecl);

    // GemButton
    ASTGUIComponent button = Iterables.getLast(components);
    // GemButton#leftClick
    String expect1 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "create(person)"
        + "'. FieldAccess2MutatorConverter cannot produce setter."
    );
    printer.prettyprint(button);
    String result1 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    assertAll(
        () -> assertEquals(expect1, result1)
    );
  }

  @Test
  public void testMutatorConversion4() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;

    Expression2MutatorPrinter printer = new Expression2MutatorPrinter(new IndentPrinter(), true);

    componentDecl = processGUIModel(
        "gui/genjava/Prettyprinter.gui",
        "src/test/resources/gui"
    );

    components = stat.getComponents(componentDecl);

    ASTGUIComponent call = stat.getById(components, "call");
    ASTGUIComponent ternary = stat.getById(components, "ternary");
    ASTGUIComponent fieldCall1 = stat.getById(components, "fieldCall1");
    ASTGUIComponent fieldCall2 = stat.getById(components, "fieldCall2");
    ASTGUIComponent fieldCall3 = stat.getById(components, "fieldCall3");
    ASTGUIComponent fieldCall4 = stat.getById(components, "fieldCall4");

    String expect1 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "getAge()"
        + "'. FieldAccess2MutatorConverter cannot produce setter."
    );
    printer.prettyprint(call);
    String result1 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    String expect2 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "p.friends.size() > 0 ? p.friends.get(p.friends.size() - 1).getAge() : \"no friends\""
        + "'. FieldAccess2MutatorConverter cannot produce setter."
    );
    printer.prettyprint(ternary);
    String result2 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    String expect3 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "p.getAge()"
        + "'. FieldAccess2MutatorConverter cannot produce setter."
    );
    printer.prettyprint(fieldCall1);
    String result3 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    String expect4 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "p.friends.get(0).getAge()"
        + "'. FieldAccess2MutatorConverter cannot produce setter."
    );
    printer.prettyprint(fieldCall2);
    String result4 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    String expect5 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "p.get(0).getAge()"
        + "'. FieldAccess2MutatorConverter cannot produce setter."
    );
    printer.prettyprint(fieldCall3);
    String result5 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    String expect6 = removeSpaces("0x0000D Expected FieldAccessExpression but got '"
        + "p.eat()"
        + "'. FieldAccess2MutatorConverter cannot produce setter."
    );
    printer.prettyprint(fieldCall4);
    String result6 = removeSpaces(Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    assertAll(
        () -> assertEquals(expect1, result1),
        () -> assertEquals(expect2, result2),
        () -> assertEquals(expect3, result3),
        () -> assertEquals(expect4, result4),
        () -> assertEquals(expect5, result5),
        () -> assertEquals(expect6, result6)
    );
  }

}
