/* (c) https://github.com/MontiCore/monticore */
package guiTest.generator.gui.genjava.prettyprinter;

import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.io.paths.MCPath;
import de.monticore.prettyprint.IndentPrinter;
import guiTest.AbstractGUITest;
import guiTest.GUIComponentStat;
import org.junit.jupiter.api.Test;
import umlp.generator.gui.genjava.prettyprinter.CommonExpressionsConversionPrinter;
import umlp.generator.gui.genjava.prettyprinter.ExpressionConversion;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommonExpressionsConversionPrinterTest extends AbstractGUITest {

  @Test
  public void testGetterConversion() {
    /*
    ASTCDCompilationUnit ast = parse("src/test/resources/gui", "Domain.umlp");
    createSymTab(ast);
    /**/

    ASTGUICompilationUnit guiAst;
    MCPath importedSymbolPath;
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;
    List<ASTGUIGuard> guards;
    List<ASTGUIIterate> iterates;

    // Overview.gui

    guiAst = parseGUI("gui/person/Overview.gui");
    importedSymbolPath = new MCPath();
    importedSymbolPath.addEntry(Paths.get("src/test/resources/gui"));
    createSymTab(guiAst, Optional.of(Paths.get("src/test/resources/gui/person/Overview.gui")), importedSymbolPath);
    componentDecl = guiAst.getGUIComponentDeclaration();

    components = stat.getComponents(componentDecl);
    guards = stat.getGuards(componentDecl);
    iterates = stat.getIterates(componentDecl);

    CommonExpressionsConversionPrinter printer = new CommonExpressionsConversionPrinter(
        new IndentPrinter(), true,
        x -> ExpressionConversion.getInstance().attr2Getter(x),
        x -> ExpressionConversion.getInstance().attr2Getter(x));

    // there is only one guard in Overview.gui
    ASTGUIGuard guard = guards.get(0);
    String expectation1 = removeSpaces("this.getP().getAddress().getStreet() != \"Ahornstrasse\"");
    String result1 = removeSpaces(printer.prettyprint(guard.getCondition()));

    // there is only one iterate in Overview.gui
    ASTGUIIterate iterate = iterates.get(0);
    ASTGUIShortIterateControl control = ((ASTGUIShortIterateControl) iterate.getIterateControl());
    String expectation2 = removeSpaces("this.getPersons()");
    String result2 = removeSpaces(printer.prettyprint(control.getIterable()));

    // component with id gemText5
    ASTGUIComponent gemText5 = components.get(4);
    String expectation3 = removeSpaces("this.getP().getName()");
    String result3 = removeSpaces(printer.prettyprint(gemText5.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    // GemText component showing address in form: "<street> <house>"
    ASTGUIComponent address = components.get(5);
    String expectation4 = removeSpaces("this.getP().getAddress().getStreet() + \" \" + this.getP().getAddress().getHouse()");
    String result4 = removeSpaces(printer.prettyprint(address.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    // GemCheckboxInputExample.gui

    guiAst = parseGUI("gui/example_input/GemCheckboxInputExample.gui");
    importedSymbolPath = new MCPath();
    importedSymbolPath.addEntry(Paths.get("src/test/resources/gui"));
    createSymTab(guiAst, Optional.of(Paths.get("src/test/resources/gui/example_input/GemCheckboxInputExample.gui")), importedSymbolPath);
    componentDecl = guiAst.getGUIComponentDeclaration();

    components = stat.getComponents(componentDecl);
    guards = stat.getGuards(componentDecl);
    iterates = stat.getIterates(componentDecl);

    // the only component in GemCheckboxInputExample.gui
    ASTGUIComponent checkbox = components.get(0);
    // GemCheckBoxInput#labelText
    String expectation5 = removeSpaces("this.getP().getName() + \" is employed\"");
    String result5 = removeSpaces(printer.prettyprint(checkbox.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    // GemCheckBoxInput#checked
    String expectation6 = removeSpaces("this.getP().isEmployed()");
    String result6 = removeSpaces(printer.prettyprint(checkbox.getGUINamedArgs().getGUINamedArg(1).getGUIArgValue()));

    assertAll(
        () -> assertEquals(expectation1, result1),
        () -> assertEquals(expectation2, result2),
        () -> assertEquals(expectation3, result3),
        () -> assertEquals(expectation4, result4),
        () -> assertEquals(expectation5, result5),
        () -> assertEquals(expectation6, result6)
    );
  }

}
