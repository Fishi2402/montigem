/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import umlp.generator.gui.DomainGUITest;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.genjava.GUI2CDTrafo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GUI2JavaClassDecoratorTest extends DomainGUITest {

  GlobalExtensionManagement glex;

  @BeforeEach
  public void genSetup() {
    GeneratorSetup genSetup = new GeneratorSetup();
    // set output directory
    String genFolder = BUILD_FOLDER + "/guigentests";
    genSetup.setOutputDirectory(new File(genFolder));
    // set hwc directories
    // genSetup.setHandcodedPath(hwcPath);
    // set template path
    glex = createGlex();
    genSetup.setGlex(glex);
    CD4C.init(genSetup);
  }

  @Test
  public void testEmptyClass() {
    ASTGUICompilationUnit gui;
    ASTCDCompilationUnit cd;

    gui = processGUIModel(
        "gui/genjava/decorator/GUI2JavaClassDecorator.gui",
        "src/test/resources/gui",
        "packaged/Domain.umlp"
    );
    cd = GUI2CDTrafo.createCDCompilationUnit(gui);

    GUIGeneratorUtil.getInstance().init(List.of("packaged.Domain"), List.of("genjava.GUI2JavaClassDecorator"));

    GUI2JavaClassDecorator decorator = new GUI2JavaClassDecorator(glex);
    decorator.transform(gui, cd);

    assertEquals(1, cd.getCDDefinition().getCDClassesList().size());

    ASTCDClass clazz = cd.getCDDefinition().getCDClassesList().get(0);
    assertEquals("GUI2JavaClassDecorator", clazz.getName());
  }

  @Test
  public void testVisitGUIParam() {
    ASTGUICompilationUnit gui;
    ASTCDCompilationUnit cd;

    gui = processGUIModel(
        "gui/genjava/decorator/GUI2JavaClassDecorator.gui",
        "src/test/resources/gui",
        "packaged/Domain.umlp"
    );
    cd = GUI2CDTrafo.createCDCompilationUnit(gui);

    GUIGeneratorUtil.getInstance().init(
        List.of("packaged.Domain"),
        List.of("genjava.decorator.GUI2JavaClassDecorator")
    );

    GUI2JavaClassDecorator decorator = new GUI2JavaClassDecorator(glex);
    decorator.transform(gui, cd);

    ASTCDClass clazz = cd.getCDDefinition().getCDClassesList().get(0);

    List<Executable> assertions = new ArrayList<>();

    assertions.add(() -> assertEquals(10, clazz.getCDAttributeList().size()));

    List<ASTCDAttribute> attrs = clazz.getCDAttributeList();

    ASTCDAttribute person = getAttr(attrs, "person");
    ASTCDAttribute $per$son = getAttr(attrs, "$per$son");
    ASTCDAttribute status = getAttr(attrs, "status");
    ASTCDAttribute la = getAttr(attrs, "la");
    ASTCDAttribute lla = getAttr(attrs, "lla");
    ASTCDAttribute s = getAttr(attrs, "s");
    ASTCDAttribute b = getAttr(attrs, "b");
    ASTCDAttribute i = getAttr(attrs, "i");
    ASTCDAttribute v = getAttr(attrs, "v");
    ASTCDAttribute e = getAttr(attrs, "e");

    assertions.add(() -> assertNotNull(person));
    assertions.add(() -> assertNotNull($per$son));
    assertions.add(() -> assertNotNull(status));
    assertions.add(() -> assertNotNull(lla));
    assertions.add(() -> assertNotNull(la));
    assertions.add(() -> assertNotNull(s));
    assertions.add(() -> assertNotNull(b));
    assertions.add(() -> assertNotNull(i));
    assertions.add(() -> assertNotNull(v));
    assertions.add(() -> assertNotNull(e));

    assertions.add(() -> assertEquals("Person", person.getMCType().printType()));
    assertions.add(() -> assertEquals("Person", $per$son.getMCType().printType()));
    assertions.add(() -> assertEquals("MaritalStatus", status.getMCType().printType()));
    assertions.add(() -> assertEquals("List<Address>", la.getMCType().printType()));
    assertions.add(() -> assertEquals("List<List<Address>>", lla.getMCType().printType()));
    assertions.add(() -> assertEquals("String", s.getMCType().printType()));
    assertions.add(() -> assertEquals("boolean", b.getMCType().printType()));
    assertions.add(() -> assertEquals("int", i.getMCType().printType()));
    assertions.add(() -> assertEquals("GUIViewElement", v.getMCType().printType()));
    assertions.add(() -> assertEquals("Event<Void>", e.getMCType().printType()));

    Collection<ASTMCImportStatement> imports = CD4C.getInstance().getImportList(clazz);

    assertions.add(() -> assertEquals(8, imports.size()));

    ASTMCImportStatement personImport = getImport(imports, "packaged.domain.Person");
    ASTMCImportStatement maritalStatusImport = getImport(imports, "packaged.domain.MaritalStatus");
    ASTMCImportStatement listImport = getImport(imports, "java.util.List");
    ASTMCImportStatement addressImport = getImport(imports, "packaged.domain.Address");
    ASTMCImportStatement guiViewElementImport = getImport(imports, "de.monticore.guidsl.types.GUIViewElement");
    ASTMCImportStatement eventImport = getImport(imports, "de.monticore.guidsl.types.Event");
    // optional imports, byproduct of type handling
    ASTMCImportStatement stringImport = getImport(imports, "java.lang.String");
    ASTMCImportStatement voidImport = getImport(imports, "java.lang.Void");

    assertions.add(() -> assertNotNull(personImport));
    assertions.add(() -> assertNotNull(maritalStatusImport));
    assertions.add(() -> assertNotNull(listImport));
    assertions.add(() -> assertNotNull(addressImport));
    assertions.add(() -> assertNotNull(guiViewElementImport));
    assertions.add(() -> assertNotNull(eventImport));
    assertions.add(() -> assertNotNull(stringImport));
    assertions.add(() -> assertNotNull(voidImport));

    assertAll(assertions);
  }

  @Test
  public void testVisitGUINamedArg() {
    ASTGUICompilationUnit gui;
    ASTCDCompilationUnit cd;

    gui = processGUIModel(
        "gui/genjava/decorator/GUI2JavaClassDecorator.gui",
        "src/test/resources/gui",
        "packaged/Domain.umlp"
    );
    cd = GUI2CDTrafo.createCDCompilationUnit(gui);

    GUIGeneratorUtil.getInstance().init(
        List.of("packaged.Domain"),
        List.of("genjava.decorator.GUI2JavaClassDecorator")
    );

    GUI2JavaClassDecorator decorator = new GUI2JavaClassDecorator(glex);
    decorator.transform(gui, cd);

    ASTCDClass clazz = cd.getCDDefinition().getCDClassesList().get(0);

    List<Executable> assertions = new ArrayList<>();

    List<ASTCDMethod> methods = clazz.getCDMethodList();

    ASTCDMethod getAP = getMethod(methods, "getAP");
    ASTCDMethod getAM = getMethod(methods, "getAM");
    ASTCDMethod getAS = getMethod(methods, "getAS");
    ASTCDMethod getAB = getMethod(methods, "getAB");
    ASTCDMethod getAA = getMethod(methods, "getAA");
    ASTCDMethod getCP = getMethod(methods, "getCP");
    ASTCDMethod getCA = getMethod(methods, "getCA");
    ASTCDMethod getDI = getMethod(methods, "getDI");
    ASTCDMethod setDI = getMethod(methods, "setDI");
    ASTCDMethod getFI = getMethod(methods, "getFI");
    ASTCDMethod setFI = getMethod(methods, "setFI");
    ASTCDMethod getGGve = getMethod(methods, "getGGve");
    ASTCDMethod getGLgve = getMethod(methods, "getGLgve");
    ASTCDMethod getHE = getMethod(methods, "getHE");
    ASTCDMethod getJE = getMethod(methods, "getJE");
    ASTCDMethod getKA = getMethod(methods, "getKA");
    ASTCDMethod getKS = getMethod(methods, "getKS");
    ASTCDMethod getKM = getMethod(methods, "getKM");
    ASTCDMethod getMCondition = getMethod(methods, "getMCondition");

    assertions.add(() -> assertNotNull(getAP));
    assertions.add(() -> assertNotNull(getAM));
    assertions.add(() -> assertNotNull(getAS));
    assertions.add(() -> assertNotNull(getAB));
    assertions.add(() -> assertNotNull(getAA));
    assertions.add(() -> assertNotNull(getCP));
    assertions.add(() -> assertNotNull(getCA));
    assertions.add(() -> assertNotNull(getDI));
    assertions.add(() -> assertNotNull(setDI));
    assertions.add(() -> assertNotNull(getFI));
    assertions.add(() -> assertNotNull(setFI));
    assertions.add(() -> assertNotNull(getGGve));
    assertions.add(() -> assertNotNull(getGLgve));
    assertions.add(() -> assertNotNull(getHE));
    assertions.add(() -> assertNotNull(getJE));
    assertions.add(() -> assertNotNull(getKA));
    assertions.add(() -> assertNotNull(getKS));
    assertions.add(() -> assertNotNull(getKM));
    assertions.add(() -> assertNotNull(getMCondition));

    /*
    Collection<ASTMCImportStatement> imports = CD4C.getInstance().getImportList(clazz);

    assertions.add(() -> assertEquals(8, imports.size()));

    ASTMCImportStatement personImport = getImport(imports, "packaged.domain.Person");
    ASTMCImportStatement maritalStatusImport = getImport(imports, "packaged.domain.MaritalStatus");
    ASTMCImportStatement listImport = getImport(imports, "java.util.List");
    ASTMCImportStatement addressImport = getImport(imports, "packaged.domain.Address");
    ASTMCImportStatement guiViewElementImport = getImport(imports, "de.monticore.guidsl.types.GUIViewElement");
    ASTMCImportStatement eventImport = getImport(imports, "de.monticore.guidsl.types.Event");
    // optional imports, byproduct of type handling
    ASTMCImportStatement stringImport = getImport(imports, "java.lang.String");
    ASTMCImportStatement voidImport = getImport(imports, "java.lang.Void");

    assertions.add(() -> assertNotNull(personImport));
    assertions.add(() -> assertNotNull(maritalStatusImport));
    assertions.add(() -> assertNotNull(listImport));
    assertions.add(() -> assertNotNull(addressImport));
    assertions.add(() -> assertNotNull(guiViewElementImport));
    assertions.add(() -> assertNotNull(eventImport));
    assertions.add(() -> assertNotNull(stringImport));
    assertions.add(() -> assertNotNull(voidImport));

    assertAll(assertions);
    /**/
  }

  protected ASTCDAttribute getAttr(Collection<ASTCDAttribute> attrs, String name) {
    for (ASTCDAttribute attr : attrs) {
      if (attr.getName().equals(name)) {
        return attr;
      }
    }
    return null;
  }

  protected ASTCDMethod getMethod(Collection<ASTCDMethod> methods, String name) {
    for (ASTCDMethod method : methods) {
      if (method.getName().equals(name)) {
        return method;
      }
    }
    return null;
  }

  protected ASTMCImportStatement getImport(Collection<ASTMCImportStatement> imports, String name) {
    for (ASTMCImportStatement imp : imports) {
      if (imp.getQName().equals(name)) {
        return imp;
      }
    }
    return null;
  }

}
