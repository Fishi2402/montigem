/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import umlp.generator.gui.DomainGUITest;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.genjava.decorator.GUI2JavaClassDecorator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GUI2JavaClassDecoratorTest extends DomainGUITest {

  @Test
  public void testEmptyClass() {
    ASTGUICompilationUnit gui;
    ASTCDCompilationUnit cd;

    gui = processGUIModel("gui/genjava/GUI2JavaClassDecorator.gui", "src/test/resources/gui");
    cd = GUI2CDTrafo.createCDCompilationUnit(gui);

    GUIGeneratorUtil.getInstance().init(List.of("Domain"), List.of("gui.genjava.GUI2JavaClassDecorator"));

    GUI2JavaClassDecorator decorator = new GUI2JavaClassDecorator(createGlex());
    decorator.transform(gui, cd);

    assertEquals(1, cd.getCDDefinition().getCDClassesList().size());

    ASTCDClass clazz = cd.getCDDefinition().getCDClassesList().get(0);
    assertEquals("GUI2JavaClassDecorator", clazz.getName());
  }

  @Test
  public void testAddBuilderAttr() {
    ASTGUICompilationUnit gui;
    ASTCDCompilationUnit cd;

    gui = processGUIModel("gui/genjava/GUI2JavaClassDecorator.gui", "src/test/resources/gui");
    cd = GUI2CDTrafo.createCDCompilationUnit(gui);

    GUIGeneratorUtil.getInstance().init(List.of("Domain"), List.of("gui.genjava.GUI2JavaClassDecorator"));

    GUI2JavaClassDecorator decorator = new GUI2JavaClassDecorator(createGlex());
    decorator.transform(gui, cd);

    ASTCDClass clazz = cd.getCDDefinition().getCDClassesList().get(0);

    List<Executable> assertions = new ArrayList<>();

    assertions.add(() -> assertEquals(9, clazz.getCDAttributeList().size()));

    List<ASTCDAttribute> attrs = clazz.getCDAttributeList();

    ASTCDAttribute person = getAttr(attrs, "personBuilder");
    ASTCDAttribute $per$son = getAttr(attrs, "$per$sonBuilder");
    ASTCDAttribute status = getAttr(attrs, "status");
    ASTCDAttribute la = getAttr(attrs, "la");
    ASTCDAttribute s = getAttr(attrs, "s");
    ASTCDAttribute b = getAttr(attrs, "b");
    ASTCDAttribute i = getAttr(attrs, "i");
    ASTCDAttribute v = getAttr(attrs, "v");
    ASTCDAttribute e = getAttr(attrs, "e");

    assertions.add(() -> assertNotNull(person));
    assertions.add(() -> assertNotNull($per$son));
    assertions.add(() -> assertNotNull(status));
    assertions.add(() -> assertNotNull(la));
    assertions.add(() -> assertNotNull(s));
    assertions.add(() -> assertNotNull(b));
    assertions.add(() -> assertNotNull(i));
    assertions.add(() -> assertNotNull(v));
    assertions.add(() -> assertNotNull(e));

    assertions.add(() -> assertEquals("PersonBuilder", person.getMCType().printType()));
    assertions.add(() -> assertEquals("PersonBuilder", $per$son.getMCType().printType()));
    assertions.add(() -> assertEquals("MaritalStatus", status.getMCType().printType()));
    assertions.add(() -> assertEquals("List<Address>", la.getMCType().printType()));
    assertions.add(() -> assertEquals("String", s.getMCType().printType()));
    assertions.add(() -> assertEquals("boolean", b.getMCType().printType()));
    assertions.add(() -> assertEquals("int", i.getMCType().printType()));
    assertions.add(() -> assertEquals("GUIViewElement", v.getMCType().printType()));
    assertions.add(() -> assertEquals("Event<Void>", e.getMCType().printType()));

    List<ASTMCImportStatement> imports = cd.getMCImportStatementList();

    assertions.add(() -> assertEquals(6, imports.size()));

    ASTMCImportStatement personBuilder = getImport(imports, "domain.PersonBuilder");
    ASTMCImportStatement maritalStatus = getImport(imports, "domain.MaritalStatus");
    ASTMCImportStatement list = getImport(imports, "java.util.List");
    ASTMCImportStatement address = getImport(imports, "domain.Address");
    ASTMCImportStatement guiViewElement = getImport(imports, "de.monticore.guidsl.types.GUIViewElement");
    ASTMCImportStatement event = getImport(imports, "de.monticore.guidsl.types.Event");

    assertions.add(() -> assertNotNull(personBuilder));
    assertions.add(() -> assertNotNull(maritalStatus));
    assertions.add(() -> assertNotNull(list));
    assertions.add(() -> assertNotNull(address));
    assertions.add(() -> assertNotNull(guiViewElement));
    assertions.add(() -> assertNotNull(event));
  }

  protected ASTCDAttribute getAttr(List<ASTCDAttribute> attrs, String name) {
    for (ASTCDAttribute attr : attrs) {
      if (attr.getName().equals(name)) {
        return attr;
      }
    }
    return null;
  }

  protected ASTMCImportStatement getImport(List<ASTMCImportStatement> imports, String name) {
    for (ASTMCImportStatement imp : imports) {
      if (imp.getQName().equals(name)) {
        return imp;
      }
    }
    return null;
  }

}
