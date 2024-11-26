package umlp.guidsl.additional_cocos;

import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._cocos.GUIDSLCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umlp.generator.gui.DomainGUITest;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.coco.GUIDSLCoCosForUMLP;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static umlp.generator.gui.TestUtils.removeSpaces;

public class RefGUIArgIncompatibleWithDerivedCoCoTest extends DomainGUITest {

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
    genSetup.getGlex().setGlobalValue("cdPrinter", new CdUtilsPrinter());
    CD4C.init(genSetup);
  }

  @Test
  public void testCheck1() {
    ASTGUICompilationUnit gui;

    gui = processGUIModel(
        "gui/coco/RefGUIArgIncompatibleWithDerivedCoCo1.gui",
        "src/test/resources/gui",
        "packaged/Domain.umlp"
    );

    GUIGeneratorUtil.getInstance().init(
        List.of("packaged.Domain"), Collections.emptyList());

    GUIDSLCoCoChecker checker = new GUIDSLCoCosForUMLP().createChecker();
    checker.addCoCo(new RefGUIArgIncompatibleWithDerivedCoCo());
    checker.checkAll(gui);

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCheck2() {
    ASTGUICompilationUnit gui;

    gui = processGUIModel(
        "gui/coco/RefGUIArgIncompatibleWithDerivedCoCo2.gui",
        "src/test/resources/gui",
        "packaged/Domain.umlp"
    );

    GUIGeneratorUtil.getInstance().init(
        List.of("packaged.Domain"), Collections.emptyList());

    GUIDSLCoCoChecker checker = new GUIDSLCoCosForUMLP().createChecker();
    checker.addCoCo(new RefGUIArgIncompatibleWithDerivedCoCo());
    checker.checkAll(gui);

    assertFalse(Log.getFindings().isEmpty());

    String expect = "0x00025";
    String result = Log.getFindings().get(0).getMsg();

    assertTrue(result.contains(expect));
  }

  @Test
  public void testCheck3() {
    ASTGUICompilationUnit gui;

    gui = processGUIModel(
        "gui/coco/RefGUIArgIncompatibleWithDerivedCoCo3.gui",
        "src/test/resources/gui",
        "packaged/Domain.umlp"
    );

    GUIGeneratorUtil.getInstance().init(
        List.of("packaged.Domain"), Collections.emptyList());

    GUIDSLCoCoChecker checker = new GUIDSLCoCosForUMLP().createChecker();
    checker.addCoCo(new RefGUIArgIncompatibleWithDerivedCoCo());
    checker.checkAll(gui);

    assertFalse(Log.getFindings().isEmpty());

    String expect = "0x00026";
    String result = Log.getFindings().get(0).getMsg();

    assertTrue(result.contains(expect));
  }
}
