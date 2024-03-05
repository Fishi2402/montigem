/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

public class RoutingGeneratorTest extends AbstractGUITest {

  /*
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
    GlobalExtensionManagement glex = createGlex();
    generatorSetup.setGlex(glex);
    generatorSetup.getGlex().setGlobalValue("genHelper", GUIGeneratorUtil.getInstance());
    GUI2TypeScriptGenerator typeScriptGenerator = new GUI2TypeScriptGenerator(generatorSetup, generatorSetup.getHandcodedPath(), null);
    Map<String,String> dummy=new HashMap<>();
    astList.forEach(ast->dummy.put(ast.getComponentName(),""));
    typeScriptGenerator.generateConfigFileForPackages(astList);
    assertNoErrors();
  }
   */

  /*
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

    typeScriptGenerator.generateAppConfigfile(astList);
    assertNoErrors();
  }

   */

}
