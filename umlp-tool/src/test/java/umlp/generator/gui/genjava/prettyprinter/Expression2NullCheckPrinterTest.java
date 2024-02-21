/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.prettyprinter;

import com.google.common.collect.Iterables;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.prettyprint.IndentPrinter;
import umlp.generator.gui.GUIComponentStat;
import org.junit.jupiter.api.Test;
import umlp.generator.gui.DomainGUITest;

import java.util.List;

import static umlp.generator.gui.TestUtils.removeSpaces;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Expression2NullCheckPrinterTest extends DomainGUITest {

  @Test
  public void testAccessorConversion1() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;
    List<ASTGUIGuard> guards;
    List<ASTGUIIterate> iterates;

    Expression2NullCheckPrinter printer = new Expression2NullCheckPrinter(new IndentPrinter(), true);

    componentDecl = processGUIModel("gui/person/Overview.gui", "src/test/resources/gui");

    components = stat.getComponents(componentDecl);
    guards = stat.getGuards(componentDecl);
    iterates = stat.getIterates(componentDecl);

    // there is only one guard in Overview.gui
    ASTGUIGuard guard = guards.get(0);
    String expect1 = removeSpaces("true || this.getP() == null || this.getP().getAddress() == null");
    String result1 = removeSpaces(printer.prettyprint(guard.getCondition()));

    // there is only one iterate in Overview.gui
    ASTGUIIterate iterate = iterates.get(0);
    ASTGUIShortIterateControl control = ((ASTGUIShortIterateControl) iterate.getIterateControl());
    String expect2 = removeSpaces("true");
    String result2 = removeSpaces(printer.prettyprint(control.getIterable()));

    // component with id gemText5
    ASTGUIComponent gemText5 = components.get(4);
    String expect3 = removeSpaces("true || this.getP() == null");
    String result3 = removeSpaces(printer.prettyprint(
        gemText5.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    // GemText component showing address in form: "<street> <house>"
    ASTGUIComponent address = components.get(5);
    String expect4 = removeSpaces("true"
        + " || this.getP() == null || this.getP().getAddress() == null"
        + " || this.getP() == null || this.getP().getAddress() == null");
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

    Expression2NullCheckPrinter printer = new Expression2NullCheckPrinter(new IndentPrinter(), true);

    componentDecl = processGUIModel(
        "gui/example_input/GemCheckboxInputExample.gui",
        "src/test/resources/gui"
    );

    components = stat.getComponents(componentDecl);

    // the only component in GemCheckboxInputExample.gui
    ASTGUIComponent checkbox = components.get(0);
    // GemCheckBoxInput#labelText
    String expect1 = removeSpaces("true || this.getP() == null");
    String result1 = removeSpaces(printer.prettyprint(
        checkbox.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    // GemCheckBoxInput#checked
    String expect2 = removeSpaces("true || this.getP() == null");
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

    Expression2NullCheckPrinter printer = new Expression2NullCheckPrinter(new IndentPrinter(), true);

    componentDecl = processGUIModel(
        "gui/person/Create.gui",
        "src/test/resources/gui"
    );

    components = stat.getComponents(componentDecl);

    // GemButton
    ASTGUIComponent button = Iterables.getLast(components);
    // GemButton#leftClick
    String expect1 = removeSpaces("true");
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

    Expression2NullCheckPrinter printer = new Expression2NullCheckPrinter(new IndentPrinter(), true);

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

    String expect1 = removeSpaces("true");
    String result1 = removeSpaces(printer.prettyprint(
        call.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect2 = removeSpaces("true"
        + " || this.getP() == null || this.getP().getFriends() == null"
        + " || this.getP() == null || this.getP().getFriends() == null"
        + " || this.getP() == null || this.getP().getFriends() == null"
        + " || this.getP().getFriends().get(this.getP().getFriends().size() - 1) == null");
    String result2 = removeSpaces(printer.prettyprint(
        ternary.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect3 = removeSpaces("true"
        + " || this.getP() == null");
    String result3 = removeSpaces(printer.prettyprint(
        fieldCall1.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect4 = removeSpaces("true"
        + " || this.getP() == null"
        + " || this.getP().getFriends() == null"
        + " || this.getP().getFriends().get(0) == null");
    String result4 = removeSpaces(printer.prettyprint(
        fieldCall2.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect5 = removeSpaces("true"
        + " || this.getP() == null"
        + " || this.getP().get(0) == null");
    String result5 = removeSpaces(printer.prettyprint(
        fieldCall3.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    String expect6 = removeSpaces("true"
        + " || this.getP() == null");
    String result6 = removeSpaces(printer.prettyprint(
        fieldCall4.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

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
