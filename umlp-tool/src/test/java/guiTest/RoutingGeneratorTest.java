package guiTest;
import de.monticore.generating.GeneratorSetup;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import org.junit.jupiter.api.Test;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.gentypescript.GUI2TypeScriptGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RoutingGeneratorTest extends AbstractGUITest {

  @Test
  public void testRoutingGenerateMethod()
  {
    GUIGeneratorUtil.getInstance().init(new ArrayList<>(),new ArrayList<>());
    List<ASTGUICompilationUnit> astList=new ArrayList<>();
    ASTGUICompilationUnit astguiCompilationUnit=parseGUI("gui.models/stanley/gui/AccountCreate.gui");
    astList.add(astguiCompilationUnit);
    assert astguiCompilationUnit.getComponentName().equals("AccountCreate");
    astguiCompilationUnit=parseGUI("gui.models/stanley/gui/TransactionsView.gui");
    astList.add(astguiCompilationUnit);
    GeneratorSetup generatorSetup=AbstractGUITest.getGenSetup();
    GUI2TypeScriptGenerator typeScriptGenerator = new GUI2TypeScriptGenerator(generatorSetup, generatorSetup.getHandcodedPath(), null);
    typeScriptGenerator.generateRoutingForPackages(astList);
    assertNoErrors();
  }

  @Test
  public void testAppRoutingGenerateMethod()
  {
    GUIGeneratorUtil.getInstance().init(new ArrayList<>(),new ArrayList<>());
    List<List<ASTGUICompilationUnit>> astList=new ArrayList<>();
    ASTGUICompilationUnit astguiCompilationUnit=parseGUI("gui.models/stanley/gui/AccountCreate.gui");
    astList.add(Collections.singletonList(astguiCompilationUnit));
    assert astguiCompilationUnit.getComponentName().equals("AccountCreate");
    astguiCompilationUnit=parseGUI("gui.models/stanley/personcreate/PersonCreate.gui");
    astList.add(Collections.singletonList(astguiCompilationUnit));
    GeneratorSetup generatorSetup=AbstractGUITest.getGenSetup();
    GUI2TypeScriptGenerator typeScriptGenerator = new GUI2TypeScriptGenerator(generatorSetup, generatorSetup.getHandcodedPath(), null);
    typeScriptGenerator.generateAppRouting(astList);
    assertNoErrors();
  }

}
