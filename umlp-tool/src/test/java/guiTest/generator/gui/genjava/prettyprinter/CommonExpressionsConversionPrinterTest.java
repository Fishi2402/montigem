/* (c) https://github.com/MontiCore/monticore */
package guiTest.generator.gui.genjava.prettyprinter;

import com.google.common.collect.Iterables;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.io.paths.MCPath;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import guiTest.AbstractGUITest;
import guiTest.GUIComponentStat;
import org.junit.jupiter.api.Test;
import umlp.UMLPMill;
import umlp.UMLPTool;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._symboltable.UMLPSymbols2Json;
import umlp.generator.gui.genjava.prettyprinter.CommonExpressionsConversionPrinter;
import umlp.generator.gui.genjava.prettyprinter.ExpressionConversion;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommonExpressionsConversionPrinterTest extends AbstractGUITest {

  @Test
  public void testGetterConversion1() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;
    List<ASTGUIGuard> guards;
    List<ASTGUIIterate> iterates;

    CommonExpressionsConversionPrinter printer = new CommonExpressionsConversionPrinter(
        new IndentPrinter(), true,
        x -> ExpressionConversion.getInstance().attr2Getter(x),
        x -> ExpressionConversion.getInstance().attr2Getter(x));

    componentDecl = processGUIModel("gui/person/Overview.gui", "src/test/resources/gui");

    components = stat.getComponents(componentDecl);
    guards = stat.getGuards(componentDecl);
    iterates = stat.getIterates(componentDecl);

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
    String result3 = removeSpaces(printer.prettyprint(
        gemText5.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    // GemText component showing address in form: "<street> <house>"
    ASTGUIComponent address = components.get(5);
    String expectation4 = removeSpaces("this.getP().getAddress().getStreet() + \" \" + this.getP().getAddress().getHouse()");
    String result4 = removeSpaces(printer.prettyprint(
        address.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    assertAll(
        () -> assertEquals(expectation1, result1),
        () -> assertEquals(expectation2, result2),
        () -> assertEquals(expectation3, result3),
        () -> assertEquals(expectation4, result4)
    );
  }

  @Test
  public void testGetterConversion2() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;

    CommonExpressionsConversionPrinter printer = new CommonExpressionsConversionPrinter(
        new IndentPrinter(), true,
        x -> ExpressionConversion.getInstance().attr2Getter(x),
        x -> ExpressionConversion.getInstance().attr2Getter(x));

    componentDecl = processGUIModel(
        "gui/example_input/GemCheckboxInputExample.gui",
        "src/test/resources/gui"
    );

    components = stat.getComponents(componentDecl);

    // the only component in GemCheckboxInputExample.gui
    ASTGUIComponent checkbox = components.get(0);
    // GemCheckBoxInput#labelText
    String expectation1 = removeSpaces("this.getP().getName() + \" is employed\"");
    String result1 = removeSpaces(printer.prettyprint(
        checkbox.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()));

    // GemCheckBoxInput#checked
    String expectation2 = removeSpaces("this.getP().isEmployed()");
    String result2 = removeSpaces(printer.prettyprint(
        checkbox.getGUINamedArgs().getGUINamedArg(1).getGUIArgValue()));

    assertAll(
        () -> assertEquals(expectation1, result1),
        () -> assertEquals(expectation2, result2)
    );
  }

  @Test
  public void testGetterConversion3() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;

    CommonExpressionsConversionPrinter printer = new CommonExpressionsConversionPrinter(
        new IndentPrinter(), true,
        x -> ExpressionConversion.getInstance().attr2Getter(x),
        x -> ExpressionConversion.getInstance().attr2Getter(x));

    componentDecl = processGUIModel(
        "gui/person/Create.gui",
        "src/test/resources/gui"
    );

    components = stat.getComponents(componentDecl);

    // GemButton
    ASTGUIComponent button = Iterables.getLast(components);
    // GemButton#leftClick
    String expectation1 = removeSpaces("this.create(this.getPerson())");
    String result1 = removeSpaces(printer.prettyprint(
        button.getGUINamedArgs().getGUINamedArg(1).getGUIArgValue()));

    assertAll(
        () -> assertEquals(expectation1, result1)
    );
  }

  protected ASTGUIComponentDeclaration processGUIModel(
      String modelPath,
      String symbolPath
  ) {
    UMLPMill.reset();
    UMLPMill.globalScope().clear();
    UMLPMill.init();
    BuiltInTypes.addBuiltInTypes(UMLPMill.globalScope());

    String symFolder = BUILD_FOLDER + "/guitests";
    String domainSymFile = symFolder + "/Domain.umlpsym";
    ASTCDCompilationUnit ast = parse("gui/Domain.umlp");
    runAfterParseTrafo(ast);
    IUMLPArtifactScope as = createSymTab(ast);
    runDefaultTrafos(ast);
    recreateSymbolTable(ast);

    UMLPSymbols2Json s2j = new UMLPSymbols2Json();
    s2j.store(as, domainSymFile);

    ASTGUICompilationUnit guiAst;
    MCPath importedSymbolPath;

    guiAst = parseGUI(modelPath);
    importedSymbolPath = new MCPath();
    importedSymbolPath.addEntry(Paths.get(symFolder));
    importedSymbolPath.addEntry(Paths.get(symbolPath));
    String[] guidslTypesJarPath = System.getProperty("guidsl-default-types").split(File.pathSeparator);
    for (String path : guidslTypesJarPath) {
      importedSymbolPath.addEntry(Paths.get(path));
    }
    UMLPTool.createSymbolTable(
        guiAst,
        Optional.of(Paths.get("src/test/resources/" + modelPath)),
        importedSymbolPath
    );
    return guiAst.getGUIComponentDeclaration();
  }

}
