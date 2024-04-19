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
import de.se_rwth.commons.logging.Log;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

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
  public void testAmounts() {
    ASTGUICompilationUnit gui;
    ASTCDCompilationUnit cd;

    gui = processGUIModel(
        "gui/genjava/decorator/GUI2JavaClassDecorator.gui",
        "src/test/resources/gui",
        "packaged/Domain.umlp"
    );
    cd = GUI2CDTrafo.createCDCompilationUnit(gui);

    GUIGeneratorUtil.getInstance().init(
        List.of("packaged.Domain"), List.of("genjava.GUI2JavaClassDecorator"));

    GUI2JavaClassDecorator decorator = new GUI2JavaClassDecorator(glex);
    decorator.transform(gui, cd);

    assertEquals(1, cd.getCDDefinition().getCDClassesList().size());

    ASTCDClass clazz = cd.getCDDefinition().getCDClassesList().get(0);
    Collection<ASTMCImportStatement> imports = CD4C.getInstance().getImportList(clazz);

    assertAll(
        () -> assertTrue(Log.getFindings().isEmpty()),
        () -> assertEquals(12, clazz.getCDAttributeList().size()),
        () -> assertEquals(66, clazz.getCDMethodList().size()),
        () -> assertEquals(1, clazz.getCDConstructorList().size()),
        () -> assertEquals(9, imports.size())
    );
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

    GUIGeneratorUtil.getInstance().init(
        List.of("packaged.Domain"), List.of("genjava.GUI2JavaClassDecorator"));

    GUI2JavaClassDecorator decorator = new GUI2JavaClassDecorator(glex);
    decorator.transform(gui, cd);

    ASTCDClass clazz = cd.getCDDefinition().getCDClassesList().get(0);
    assertEquals("GUI2JavaClassDecorator", clazz.getName());

    List<ASTCDMethod> methods = clazz.getCDMethodList();
    ASTCDMethod init = getMethod(methods, "init");

    Collection<ASTMCImportStatement> imports = CD4C.getInstance().getImportList(clazz);
    ASTMCImportStatement directiveImport = getImport(imports, "umlp.jsweet.extension.annotation.Directive");

    assertAll(
        () -> assertNotNull(init),
        () -> assertEquals("void", init.getMCReturnType().printType()),
        () -> assertEquals(0, init.getCDParameterList().size()),
        () -> assertNotNull(directiveImport)
    );
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
    List<ASTCDAttribute> attrs = clazz.getCDAttributeList();

    ASTCDAttribute _person = getAttr(attrs, "_person");
    ASTCDAttribute _$per$son = getAttr(attrs, "_$per$son");
    ASTCDAttribute _status = getAttr(attrs, "_status");
    ASTCDAttribute _la = getAttr(attrs, "_la");
    ASTCDAttribute _lla = getAttr(attrs, "_lla");
    ASTCDAttribute _s = getAttr(attrs, "_s");
    ASTCDAttribute _b = getAttr(attrs, "_b");
    ASTCDAttribute _i = getAttr(attrs, "_i");
    ASTCDAttribute _e = getAttr(attrs, "_e");

    assertions.add(() -> assertNotNull(_person));
    assertions.add(() -> assertNotNull(_$per$son));
    assertions.add(() -> assertNotNull(_status));
    assertions.add(() -> assertNotNull(_lla));
    assertions.add(() -> assertNotNull(_la));
    assertions.add(() -> assertNotNull(_s));
    assertions.add(() -> assertNotNull(_b));
    assertions.add(() -> assertNotNull(_i));
    assertions.add(() -> assertNotNull(_e));

    assertions.add(() -> assertEquals("Person", _person.getMCType().printType()));
    assertions.add(() -> assertEquals("Person", _$per$son.getMCType().printType()));
    assertions.add(() -> assertEquals("MaritalStatus", _status.getMCType().printType()));
    assertions.add(() -> assertEquals("GemList<Address>", _la.getMCType().printType()));
    assertions.add(() -> assertEquals("List<List<Address>>", _lla.getMCType().printType()));
    assertions.add(() -> assertTrue(
        "java.lang.String".equals(_s.getMCType().printType())
        || "String".equals(_s.getMCType().printType())
    ));
    assertions.add(() -> assertTrue(
        "java.lang.Boolean".equals(_b.getMCType().printType())
            || "Boolean".equals(_b.getMCType().printType())
    ));
    assertions.add(() -> assertTrue(
        "java.lang.Integer".equals(_i.getMCType().printType())
        || "Integer".equals(_i.getMCType().printType())
    ));
    assertions.add(() -> assertEquals("Event<Void>", _e.getMCType().printType()));

    List<ASTCDMethod> methods = clazz.getCDMethodList();

    ASTCDMethod getPerson = getMethod(methods, "getPerson");
    ASTCDMethod setPerson = getMethod(methods, "setPerson");
    ASTCDMethod initPerson = getMethod(methods, "initPerson");
    int initPersonCount = methodCount(methods, "initPerson");
    ASTCDMethod get$per$son = getMethod(methods, "get$per$son");
    ASTCDMethod set$per$son = getMethod(methods, "set$per$son");
    ASTCDMethod init$per$son = getMethod(methods, "init$per$son");
    int init$per$sonCount = methodCount(methods, "init$per$son");
    ASTCDMethod getStatus = getMethod(methods, "getStatus");
    ASTCDMethod setStatus = getMethod(methods, "setStatus");
    ASTCDMethod initStatus = getMethod(methods, "initStatus");
    ASTCDMethod getLa = getMethod(methods, "getLa");
    ASTCDMethod setLa = getMethod(methods, "setLa");
    ASTCDMethod initLa = getMethod(methods, "initLa");
    int initLaCount = methodCount(methods, "initLa");
    ASTCDMethod getLla = getMethod(methods, "getLla");
    ASTCDMethod setLla = getMethod(methods, "setLla");
    ASTCDMethod initLla = getMethod(methods, "initLla");
    int initLlaCount = methodCount(methods, "initLla");
    ASTCDMethod getS = getMethod(methods, "getS");
    ASTCDMethod setS = getMethod(methods, "setS");
    ASTCDMethod initS = getMethod(methods, "initS");
    ASTCDMethod getB = getMethod(methods, "getB");
    ASTCDMethod setB = getMethod(methods, "setB");
    ASTCDMethod initB = getMethod(methods, "initB");
    ASTCDMethod getI = getMethod(methods, "getI");
    ASTCDMethod setI = getMethod(methods, "setI");
    ASTCDMethod initI = getMethod(methods, "initI");
    ASTCDMethod getE = getMethod(methods, "getE");
    ASTCDMethod setE = getMethod(methods, "setE");
    ASTCDMethod initE = getMethod(methods, "initE");

    assertions.add(() -> assertNotNull(getPerson));
    assertions.add(() -> assertNotNull(setPerson));
    assertions.add(() -> assertNotNull(initPerson));
    assertions.add(() -> assertEquals(2, initPersonCount));
    assertions.add(() -> assertNotNull(get$per$son));
    assertions.add(() -> assertNotNull(set$per$son));
    assertions.add(() -> assertNotNull(init$per$son));
    assertions.add(() -> assertEquals(2, init$per$sonCount));
    assertions.add(() -> assertNotNull(getStatus));
    assertions.add(() -> assertNotNull(setStatus));
    assertions.add(() -> assertNotNull(initStatus));
    assertions.add(() -> assertNotNull(getLa));
    assertions.add(() -> assertNotNull(setLa));
    assertions.add(() -> assertNotNull(initLa));
    assertions.add(() -> assertEquals(2, initLaCount));
    assertions.add(() -> assertNotNull(getLla));
    assertions.add(() -> assertNotNull(setLla));
    assertions.add(() -> assertNotNull(initLla));
    assertions.add(() -> assertEquals(2, initLlaCount));
    assertions.add(() -> assertNotNull(getS));
    assertions.add(() -> assertNotNull(setS));
    assertions.add(() -> assertNotNull(initS));
    assertions.add(() -> assertNotNull(getB));
    assertions.add(() -> assertNotNull(setB));
    assertions.add(() -> assertNotNull(initB));
    assertions.add(() -> assertNotNull(getI));
    assertions.add(() -> assertNotNull(setI));
    assertions.add(() -> assertNotNull(initI));
    assertions.add(() -> assertNotNull(getE));
    assertions.add(() -> assertNotNull(setE));
    assertions.add(() -> assertNotNull(initE));

    Collection<ASTMCImportStatement> imports = CD4C.getInstance().getImportList(clazz);

    ASTMCImportStatement personImport = getImport(imports, "packaged.domain.Person");
    ASTMCImportStatement maritalStatusImport = getImport(imports, "packaged.domain.MaritalStatus");
    ASTMCImportStatement listImport = getImport(imports, "java.util.List");
    ASTMCImportStatement gemListImport = getImport(imports, "umlp.backendrte.common.GemList");
    ASTMCImportStatement addressImport = getImport(imports, "packaged.domain.Address");
    ASTMCImportStatement eventImport = getImport(imports, "de.monticore.guidsl.types.Event");
    // optional imports, byproduct of type handling
    ASTMCImportStatement stringImport = getImport(imports, "java.lang.String");
    ASTMCImportStatement voidImport = getImport(imports, "java.lang.Void");

    assertions.add(() -> assertNotNull(personImport));
    assertions.add(() -> assertNotNull(maritalStatusImport));
    assertions.add(() -> assertNotNull(listImport));
    assertions.add(() -> assertNotNull(gemListImport));
    assertions.add(() -> assertNotNull(addressImport));
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
    ASTCDMethod getCS = getMethod(methods, "getCS");
    ASTCDMethod calcStr = getMethod(methods, "calcStr");
    ASTCDMethod getCA = getMethod(methods, "getCA");
    ASTCDMethod getCA_0 = getMethod(methods, "getCA_0");
    ASTCDMethod syncCA_0 = getMethod(methods, "syncCA_0");
    ASTCDMethod getDI = getMethod(methods, "getDI");
    ASTCDMethod setDI = getMethod(methods, "setDI");
    ASTCDMethod getFI = getMethod(methods, "getFI");
    ASTCDMethod setFI = getMethod(methods, "setFI");
    ASTCDMethod hE = getMethod(methods, "hE");
    ASTCDMethod jE = getMethod(methods, "jE");
    ASTCDMethod action = getMethod(methods, "action");
    ASTCDMethod getKA = getMethod(methods, "getKA");
    ASTCDMethod getKS = getMethod(methods, "getKS");
    ASTCDMethod getKS_1 = getMethod(methods, "getKS_1");
    ASTCDMethod syncKS_1 = getMethod(methods, "syncKS_1");
    ASTCDMethod getKM = getMethod(methods, "getKM");
    ASTCDMethod getKM_2 = getMethod(methods, "getKM_2");
    ASTCDMethod initKM_2 = getMethod(methods, "getKM_2");
    ASTCDMethod getLIterable = getMethod(methods, "getLIterable");
    ASTCDMethod getMCondition = getMethod(methods, "getMCondition");
    ASTCDMethod getNP = getMethod(methods, "getNP");
    ASTCDMethod getPIterable = getMethod(methods, "getPIterable");

    assertions.add(() -> assertNotNull(getAP));
    assertions.add(() -> assertNotNull(getAM));
    assertions.add(() -> assertNotNull(getAS));
    assertions.add(() -> assertNotNull(getAB));
    assertions.add(() -> assertNotNull(getAA));
    assertions.add(() -> assertNotNull(getCP));
    assertions.add(() -> assertNotNull(getCS));
    assertions.add(() -> assertNotNull(calcStr));
    assertions.add(() -> assertNotNull(getCA));
    assertions.add(() -> assertNotNull(getCA_0));
    assertions.add(() -> assertNotNull(syncCA_0));
    assertions.add(() -> assertNotNull(getDI));
    assertions.add(() -> assertNotNull(setDI));
    assertions.add(() -> assertNotNull(getFI));
    assertions.add(() -> assertNotNull(setFI));
    assertions.add(() -> assertNotNull(hE));
    assertions.add(() -> assertNotNull(jE));
    assertions.add(() -> assertNotNull(action));
    assertions.add(() -> assertNotNull(getKA));
    assertions.add(() -> assertNotNull(getKS));
    assertions.add(() -> assertNotNull(getKS_1));
    assertions.add(() -> assertNotNull(syncKS_1));
    assertions.add(() -> assertNotNull(getKM));
    assertions.add(() -> assertNotNull(getKM_2));
    assertions.add(() -> assertNotNull(initKM_2));
    assertions.add(() -> assertNotNull(getLIterable));
    assertions.add(() -> assertNotNull(getMCondition));
    assertions.add(() -> assertNotNull(getNP));
    assertions.add(() -> assertNotNull(getPIterable));

    assertAll(assertions);
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

  protected int methodCount(Collection<ASTCDMethod> methods, String name) {
    int count = 0;
    for (ASTCDMethod method : methods) {
      if (method.getName().equals(name)) {
        count++;
      }
    }
    return count;
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
