/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.GUIGeneratorUtil;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static de.se_rwth.commons.Names.constructQualifiedName;

public class GUI2TypeScriptGenerator {

  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  protected final GeneratorEngine generatorEngine;
  protected GeneratorSetup setup;
  protected GUIComponentsVisitor viewComponents;
  protected MCPath hwcPath;
  public final static String ROUTES_FILE = "Routing";
  public final static String MODULES_FILE = "Module";
  public final static String TS_FILE_EXTENSION = "ts";

  public GUI2TypeScriptGenerator(GeneratorSetup generatorSetup, MCPath hwcPath,
      GUIComponentsVisitor viewComponents) {
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    this.setup = generatorSetup;
    this.hwcPath = hwcPath;
    this.viewComponents = viewComponents;
  }

  public void generate(ASTGUICompilationUnit modelAST, String configTemplate) {
    TemplateController tc = setup.getNewTemplateController(configTemplate);
    TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);

    hpp.processValue(tc, modelAST,
        Arrays.asList(modelAST, setup.getGlex(), setup.getHandcodedPath(), this, viewComponents));
  }

  public List<String> generateAppConfigfile(List<List<ASTGUICompilationUnit>> astguiCompilationUnitList,Map<String,String> topNameMap)
  {
    this.setup.setTracing(false);
    GeneratorEngine genEngine = new GeneratorEngine(setup);

    List<ASTGUICompilationUnit> astguiCompilationUnits=new ArrayList<>();
    List<String> paths=new ArrayList<>();
    astguiCompilationUnitList.forEach(asts->{
      if(!asts.isEmpty())
      {
        asts.forEach(ast->{
          if(util.getRoutingIds(ast).isEmpty()) {
            astguiCompilationUnits.add(ast);
            paths.add(String.join("/", ast.getMCPackageDeclaration().getMCQualifiedName().getPartsList()) + "/");
          }
          });
      }
    });
    boolean existHWCRoutes = existsHandwrittenFile(hwcPath, "app"+ROUTES_FILE, TS_FILE_EXTENSION);
    Path generatedRoutingFiles = Paths.get("",
        "app"+ ROUTES_FILE +(existHWCRoutes?"TOP":"")+ "." + TS_FILE_EXTENSION);
    genEngine.generateNoA("umlp.gui2ts.TSAppRoutes", generatedRoutingFiles,
        astguiCompilationUnits, paths,topNameMap);

    Path generatedModuleFiles = Paths.get("",
            "app"+ MODULES_FILE + "." + TS_FILE_EXTENSION);
    Boolean existedModuleConfig=existsHandwrittenFile(hwcPath,"appModuleConfig",TS_FILE_EXTENSION);
    genEngine.generateNoA("umlp.gui2ts.TSAppModule", generatedModuleFiles,existedModuleConfig);
    return Arrays.asList(generatedRoutingFiles.toString(),generatedModuleFiles.toString());
  }

  public List<String> generateConfigFileForPackages(List<ASTGUICompilationUnit> astguiCompilationUnitList,  Map<String,String> topNameMap)
  {
    this.setup.setTracing(false);
    GeneratorEngine genEngine = new GeneratorEngine(setup);
    ASTGUICompilationUnit astGuiFirst;

    if(!astguiCompilationUnitList.isEmpty())
    {
      astGuiFirst=astguiCompilationUnitList.get(0);
    }
    else
    {
      return new ArrayList<>();
    }



    List<List<String>> ids=new ArrayList<>();
    int fullPathIndex=-1;
    for(int index=0;index<astguiCompilationUnitList.size();index++)
    {
      ASTGUICompilationUnit ast=astguiCompilationUnitList.get(index);
      List<String> idStrings=util.getRoutingIds(ast);
      if(idStrings.isEmpty() && ast.getGUIComponentDeclaration().isPage())
      {
        fullPathIndex=index;
      }
      ids.add(idStrings);
    }

    String pathName=String.join("/", astGuiFirst.getMCPackageDeclaration().getMCQualifiedName().getPartsList());
    String temp=astGuiFirst.getPackage();
    String []tempArray=temp.split("\\.");
    String name =tempArray[tempArray.length-1].substring(0,1).toUpperCase()+tempArray[tempArray.length-1].substring(1);

    boolean existHWCRoutes = existsHandwrittenFile(hwcPath, pathName+"/"+name + ROUTES_FILE, TS_FILE_EXTENSION);

    Path generatedRoutingFile = Paths.get(pathName,
        name + ROUTES_FILE +(existHWCRoutes?"TOP":"")+"." + TS_FILE_EXTENSION);
    genEngine.generateNoA("umlp.gui2ts.TSPackageRoutes", generatedRoutingFile,
        astguiCompilationUnitList, ids,fullPathIndex, topNameMap);

    boolean existHWCModules = existsHandwrittenFile(hwcPath, pathName+"/" +name + MODULES_FILE, TS_FILE_EXTENSION);
    Path generatedModuleFiles = Paths.get(pathName,name+
   MODULES_FILE + (existHWCModules?"TOP":"") + "." + TS_FILE_EXTENSION);
    String compDecl= name;
    genEngine.generateNoA("umlp.gui2ts.TSPackageModule", generatedModuleFiles,
        astguiCompilationUnitList, ids,compDecl, topNameMap);
    return Arrays.asList(generatedRoutingFile.toString(),generatedModuleFiles.toString());
  }

  public void generate(ASTGUICompilationUnit decoratedAst) {
    this.setup.setTracing(false);
    GeneratorEngine genEngine = new GeneratorEngine(setup);

    String pathName = String.join("/", decoratedAst.getMCPackageDeclaration().getMCQualifiedName().getPartsList());
    String pathToSearch = decoratedAst.getMCPackageDeclaration().getMCQualifiedName().getQName();
    boolean existHWCRoutes = existsHandwrittenFile(hwcPath, pathToSearch + "."
        + decoratedAst.getComponentName() + ROUTES_FILE, TS_FILE_EXTENSION);

    String componentPath = "./" + decoratedAst.getComponentName();

    Optional<String> filePath = util.getRelativeImportPath(
        hwcPath, setup.getOutputDirectory().toPath(),
        decoratedAst.getMCPackageDeclaration().getMCQualifiedName().getQName()
            + "." + decoratedAst.getComponentName(), "ts");
    if (filePath.isPresent()) {
      componentPath = util.removeExtension(filePath.get());
    }

    Path generatedFiles = Paths.get(pathName, decoratedAst.getComponentName()
        + (existHWCRoutes ? "TOP" : "") + ROUTES_FILE + "." + TS_FILE_EXTENSION);
    genEngine.generate("umlp.gui2ts.TSRoutes", generatedFiles,
        decoratedAst.getGUIComponentDeclaration(), "routes", existHWCRoutes,
        componentPath, util.getRoutingIds(decoratedAst));


    generatedFiles = Paths.get(pathName,
        decoratedAst.getComponentName() + MODULES_FILE + "." + TS_FILE_EXTENSION);
    genEngine.generate("umlp.gui2ts.TSModule", generatedFiles, decoratedAst,
        viewComponents.getModules(), componentPath);
    /*
    for (ASTMCImportStatement is : decoratedAst.getMCImportStatementList()) {
      is.getMCQualifiedName().getParts(is.getMCQualifiedName().sizeParts() - 1)
      is.getQName();
    }

     */
  }


  /**
   * Check whewther an handwritten version of this generated file already exists and shall be integrated
   * (e.g. to apply TOP mechanism)
   */
  public static boolean existsHandwrittenFile(MCPath targetPath, String qualifiedName, String fileExtension) {
    String hwFile = Names.getPathFromPackage(qualifiedName) + "." + fileExtension;
    Optional<URL> hwFilePath = targetPath.find(hwFile);
    boolean result = hwFilePath.isPresent();
    if (result) {
      Optional<Path> hwPath = MCPath.toPath(hwFilePath.get());
      hwPath.ifPresent(path -> Reporting.reportUseHandwrittenCodeFile(path,
          Paths.get(hwFile)));
    }
    Reporting.reportHWCExistenceCheck(targetPath, Paths.get(hwFile), hwFilePath);
    return result;
  }

}
