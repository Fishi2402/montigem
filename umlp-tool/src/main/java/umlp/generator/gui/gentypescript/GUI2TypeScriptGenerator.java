/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript;

import com.google.common.collect.Iterables;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.GUIGeneratorUtil;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class GUI2TypeScriptGenerator {

  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  protected final GeneratorEngine generatorEngine;
  protected GeneratorSetup setup;
  protected GUIComponentsVisitor viewComponents;
  protected MCPath hwcPath;
  public final static String ROUTES_FILE = "GemRouting";
  public final static String MODULES_FILE = "GemModule";
  public final static String TS_FILE_EXTENSION = "ts";

  public GUI2TypeScriptGenerator(GeneratorSetup generatorSetup, MCPath hwcPath,
      GUIComponentsVisitor viewComponents) {
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    this.setup = generatorSetup;
    this.hwcPath = hwcPath;
    this.viewComponents = viewComponents;
  }

  public GUI2TypeScriptGenerator(GeneratorSetup generatorSetup, MCPath hwcPath
                                 ) {
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    this.setup = generatorSetup;
    this.hwcPath = hwcPath;
  }


  public void generate(ASTGUICompilationUnit modelAST, String configTemplate) {
    TemplateController tc = setup.getNewTemplateController(configTemplate);
    TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);

    hpp.processValue(tc, modelAST,
        Arrays.asList(modelAST, setup.getGlex(), setup.getHandcodedPath(), this, viewComponents));
  }

  public List<String> generateAppConfigfile()
  {
    this.setup.setTracing(false);
    GeneratorEngine genEngine = new GeneratorEngine(setup);

    List<ASTGUICompilationUnit> astguiCompilationUnits=new ArrayList<>();
    List<String> paths=new ArrayList<>();

    boolean existHWCRoutes = existsHandwrittenFile(hwcPath, "App"+ROUTES_FILE+".ts");

    // TODO AGe: change default app files path
    Path generatedRoutingFiles = Paths.get("../../src/app",
        "app.routing.ts");
    genEngine.generateNoA("umlp.gui2ts.TSAppRoutes", generatedRoutingFiles,
        astguiCompilationUnits);
    // TODO AGe: change default app files path
    Path generatedModuleFiles = Paths.get("../../src/app",
            "app.module.ts");
    Boolean existedModuleConfig=existsHandwrittenFile(hwcPath,"App"+MODULES_FILE+"Config.ts");

    String pathHWRoutes="./"+"App"+ROUTES_FILE+".ts";
    if(existHWCRoutes)
    {
      Optional<String> filePath = util.getRelativeImportPathForAppConfigFile(
          hwcPath, setup.getOutputDirectory().toPath(),
          "App"+ROUTES_FILE, "ts");
      pathHWRoutes=filePath.get();
    }
    String pathModuleConfig="";
    if(existedModuleConfig)
    {
      Optional<String> filePath = util.getRelativeImportPathForAppConfigFile(
          hwcPath, setup.getOutputDirectory().toPath(),
          "App"+MODULES_FILE+"Config", "ts");
      pathModuleConfig=filePath.get();
    }

    Optional<String> filePath = util.getRelativeImportPathForAppConfigFile(
        hwcPath, setup.getOutputDirectory().toPath(),
        "app", "component.ts");
    boolean existAppComponent=filePath.isPresent();
    String pathAppComponent="";
    if(existAppComponent)
    {
      pathAppComponent=filePath.get();
    }

    genEngine.generateNoA("umlp.gui2ts.TSAppModule", generatedModuleFiles,existedModuleConfig,existHWCRoutes,
        existAppComponent,
        util.removeExtension(pathHWRoutes),util.removeExtension(pathModuleConfig),util.removeExtension(pathAppComponent));
    return Arrays.asList(generatedRoutingFiles.toString(),generatedModuleFiles.toString());
  }

  public List<String> generateConfigFileForPackages(Map.Entry<String, ModuleRoutingInfo> packageInfo,GUIGeneratorUtil util)
  {
    List<ASTGUICompilationUnit> astguiCompilationUnitList=packageInfo.getValue().getComponents();
    this.setup.setTracing(false);
    GeneratorEngine genEngine = new GeneratorEngine(setup);

    List<String> paths=new ArrayList<>();
    astguiCompilationUnitList.forEach(ast->{
      Optional<String> filePath = util.getRelativeImportPath(
          hwcPath, setup.getOutputDirectory().toPath(),
          ast.getMCPackageDeclaration().getMCQualifiedName().getQName()
              + "." + ast.getComponentName()+"Component", "ts");
      if(filePath.isPresent())
      {
        paths.add(util.removeExtension(filePath.get()));
      }
      else
      {
        paths.add(util.removeExtension("./"+ast.getComponentName()+"Component"));
      }
    });

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



    List<String> packageParts = packageInfo.getValue().getPackageParts();
    String pathName = packageInfo.getValue().getRoutingPath();
    String name = StringTransformations.capitalize(Iterables.getLast(packageParts));

    boolean existHWCRoutes = existsHandwrittenFile(hwcPath, pathName+"/"+name+ ROUTES_FILE+"."+TS_FILE_EXTENSION);
    Path generatedRoutingFile = Paths.get(pathName,
        name+ ROUTES_FILE +(existHWCRoutes?"TOP":"")+"." + TS_FILE_EXTENSION);

    genEngine.generateNoA("umlp.gui2ts.TSPackageRoutes", generatedRoutingFile,
        astguiCompilationUnitList, ids,fullPathIndex, paths,pathName);

    boolean existHWCModules = existsHandwrittenFile(hwcPath, pathName+"/" +name +"GemModuleConfig."+TS_FILE_EXTENSION);
    String pathHWModule="";
    if(existHWCModules)
    {
      Optional<String> filePath = util.getRelativeImportPath(
          hwcPath, setup.getOutputDirectory().toPath(),pathName+"."+
          name+"GemModuleConfig", "ts");
      if(filePath.isPresent())
          pathHWModule=filePath.get();
    }


    String pathRoutes ="./"+name+".Routing.ts";
    if(existHWCRoutes)
    {
      Optional<String> filePath = util.getRelativeImportPath(
          hwcPath, setup.getOutputDirectory().toPath(),pathName+"."+
              name , "Routing.ts");
      if(filePath.isPresent())
        pathRoutes =filePath.get();
    }



    Path generatedModuleFiles = Paths.get(pathName,name+
   MODULES_FILE + "." + TS_FILE_EXTENSION);
    String compDecl= name;


    Map<String,String> imports=new HashMap<>();
    for(ASTGUICompilationUnit ast:astguiCompilationUnitList)
    {
      for(ASTMCImportStatement imp:ast.getMCImportStatementList())
      {
        String moduleClassName =util.getModuleClassName(imp.getMCQualifiedName().getParts(imp.getMCQualifiedName().sizeParts() - 1));
        if(util.isGuiImport(imp) && !util.isCreatedTSComponent(moduleClassName))
        {
          imports.put(moduleClassName,util.java2tsModule(imp.getQName()));
        }
        else if(util.isGuiImport(imp) && util.isCreatedTSComponent(moduleClassName) && !(compDecl+"_Module").equals(util.getPackageModuleName(imp)))
        {
          imports.put(util.getPackageModuleName(imp),util.getPackageModulePath(imp));
        }
      }
    }



    genEngine.generateNoA("umlp.gui2ts.TSPackageModule", generatedModuleFiles,
        astguiCompilationUnitList, ids,compDecl, paths,existHWCModules,util.removeExtension(pathHWModule),
        util.removeExtension(pathRoutes),imports);


    return Arrays.asList(generatedRoutingFile.toString(),generatedModuleFiles.toString());
  }

  public void generate(ASTGUICompilationUnit decoratedAst) {

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

  /**
   * Check whewther an handwritten version of this generated file already exists and shall be integrated
   * (e.g. to apply TOP mechanism)
   */
  public static boolean existsHandwrittenFile(MCPath targetPath, String hwFile) {
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
