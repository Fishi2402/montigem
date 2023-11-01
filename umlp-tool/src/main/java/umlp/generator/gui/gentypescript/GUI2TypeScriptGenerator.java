/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript;

import com.google.common.collect.Iterables;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.GUIGeneratorUtil;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GUI2TypeScriptGenerator {

  public final static String ROUTES_FILE = "GemRouting";
  public final static String MODULES_FILE = "GemModule";
  public final static String TS_FILE_EXTENSION = "ts";
  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();
  protected final GeneratorEngine generatorEngine;
  protected GeneratorSetup setup;
  protected GUIComponentsVisitor viewComponents;
  protected MCPath hwcPath;

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



  public List<String> generateAppConfigfile() {
    this.setup.setTracing(false);
    GeneratorEngine genEngine = new GeneratorEngine(setup);

    boolean existHWCRoutes = existsHandwrittenFile(hwcPath, "App" + ROUTES_FILE + ".ts");

    Map<String,String> routingModules= findNextLevelModuleForAppConfig(setup.getOutputDirectory().toString());
    String defaultRouting="";
    if(!routingModules.isEmpty())
    {
      defaultRouting=routingModules.keySet().toArray()[0].toString();
    }
    Path generatedRoutingFiles = Paths.get(
        "App" + ROUTES_FILE + (existHWCRoutes ? "TOP" : "") + ".ts");
    genEngine.generateNoA("umlp.gui2ts.TSAppRoutes", generatedRoutingFiles,routingModules,defaultRouting);

    Path generatedModuleFiles = Paths.get(
        "App" + MODULES_FILE + ".ts");
    Boolean existedModuleConfig = existsHandwrittenFile(hwcPath, "App" + MODULES_FILE + "Config.ts");

    String pathHWRoutes = "./" + "App" + ROUTES_FILE + ".ts";
    if (existHWCRoutes) {
      Optional<String> filePath = util.getRelativeImportPathForAppConfigFile(
          hwcPath, setup.getOutputDirectory().toPath(),
          "App" + ROUTES_FILE, "ts");
      pathHWRoutes = filePath.get();
    }
    String pathModuleConfig = "";
    if (existedModuleConfig) {
      Optional<String> filePath = util.getRelativeImportPathForAppConfigFile(
          hwcPath, setup.getOutputDirectory().toPath(),
          "App" + MODULES_FILE + "Config", "ts");
      pathModuleConfig = filePath.get();
    }

    genEngine.generateNoA("umlp.gui2ts.TSAppModule", generatedModuleFiles, existedModuleConfig, existHWCRoutes,
        util.removeExtension(pathHWRoutes), util.removeExtension(pathModuleConfig));
    return Arrays.asList(generatedRoutingFiles.toString(), generatedModuleFiles.toString());
  }

  public List<String> generateConfigFileForPackages(Map.Entry<String, ModuleRoutingInfo> packageInfo) {
    List<ASTGUICompilationUnit> astguiCompilationUnitList = packageInfo.getValue().getComponents();
    this.setup.setTracing(false);
    GeneratorEngine genEngine = new GeneratorEngine(setup);

    List<String> paths = new ArrayList<>();
    astguiCompilationUnitList.forEach(ast -> {
      Optional<String> filePath = util.getRelativeImportPath(
          hwcPath, setup.getOutputDirectory().toPath(),
          ast.getMCPackageDeclaration().getMCQualifiedName().getQName()
              + "." + ast.getComponentName() + "Component", "ts");
      if (filePath.isPresent()) {
        paths.add(util.removeExtension(filePath.get()));
      }
      else {
        paths.add(util.removeExtension("./" + ast.getComponentName() + "Component"));
      }
    });

    List<List<String>> ids = new ArrayList<>();
    int fullPathIndex = -1;
    for (int index = 0; index < astguiCompilationUnitList.size(); index++) {
      ASTGUICompilationUnit ast = astguiCompilationUnitList.get(index);
      List<String> idStrings = util.getRoutingIds(ast);
      if (idStrings.isEmpty() && ast.getGUIComponentDeclaration().isPage()) {
        fullPathIndex = index;
      }
      ids.add(idStrings);
    }

    List<String> packageParts = packageInfo.getValue().getPackageParts();
    String pathName = packageInfo.getValue().getRoutingPath();
    String name = StringTransformations.capitalize(Iterables.getLast(packageParts));

    boolean existHWCRoutes = existsHandwrittenFile(hwcPath, pathName + "/" + name + ROUTES_FILE + "." + TS_FILE_EXTENSION);
    Path generatedRoutingFile = Paths.get(pathName,
        name + ROUTES_FILE + (existHWCRoutes ? "TOP" : "") + "." + TS_FILE_EXTENSION);

    genEngine.generateNoA("umlp.gui2ts.TSPackageRoutes", generatedRoutingFile,
        astguiCompilationUnitList, ids, fullPathIndex, paths, pathName);

    boolean existHWCModules = existsHandwrittenFile(hwcPath, pathName + "/" + name + "GemModuleConfig." + TS_FILE_EXTENSION);
    String pathHWModule = "";
    if (existHWCModules) {
      Optional<String> filePath = util.getRelativeImportPath(
          hwcPath, setup.getOutputDirectory().toPath(), pathName + "." +
              name + "GemModuleConfig", "ts");
      if (filePath.isPresent()) {
        pathHWModule = filePath.get();
      }
    }

    String pathRoutes = "./" + name + ROUTES_FILE + ".ts";
    if (existHWCRoutes) {
      Optional<String> filePath = util.getRelativeImportPath(
          hwcPath, setup.getOutputDirectory().toPath(), pathName + "." +
              name + ROUTES_FILE, "ts");
      if (filePath.isPresent()) {
        pathRoutes = filePath.get();
      }
    }

    Path generatedModuleFiles = Paths.get(pathName, name +
        MODULES_FILE + "." + TS_FILE_EXTENSION);
    String compDecl = name;

    Map<String, String> imports = packageInfo.getValue().getImports(util, compDecl);
    Map<String,String> subPackageImports= findNextLevelModuleForPackageConfig(packageInfo.getValue().getPackageModelPath());
    subPackageImports.keySet().forEach(key->imports.put(key,subPackageImports.get(key)));


    genEngine.generateNoA("umlp.gui2ts.TSPackageModule", generatedModuleFiles,
        astguiCompilationUnitList, ids, compDecl, paths, existHWCModules, util.removeExtension(pathHWModule),
        util.removeExtension(pathRoutes), imports);

    return Arrays.asList(generatedRoutingFile.toString(), generatedModuleFiles.toString());
  }

  private static Map<String,String> findNextLevelModuleForAppConfig(String path){
    Map<String, String> modules = new HashMap<>();

    File folder = new File(path);
    File[] listOfFiles = folder.listFiles();

    if(listOfFiles!=null) {
      for (int i = 0; i < listOfFiles.length; i++) {
        if (listOfFiles[i].isDirectory()) {
          String moduleName=listOfFiles[i].getName().toUpperCase().charAt(0)+listOfFiles[i].getName().substring(1);
          if(new File(listOfFiles[i].getPath()+"/"+moduleName+MODULES_FILE+".ts").exists())
          {
            modules.put(moduleName, listOfFiles[i].getName());
          }
        }
      }
    }
    return modules;

  }

  private static Map<String,String> findNextLevelModuleForPackageConfig(String path){
    Map<String, String> modules = new HashMap<>();

    File folder = new File(path);
    File[] listOfFiles = folder.listFiles();

    if(listOfFiles!=null) {
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isDirectory()) {
                String moduleName = listOfFile.getName().toUpperCase().charAt(0) + listOfFile.getName().substring(1);
                File[] subListOfFiles = listOfFile.listFiles();
                for (File file : subListOfFiles) {
                    if (file.isFile() && file.getName().contains(".gui")) {
                        modules.put(moduleName + "_Module", "./" + listOfFile.getName() + "/" + moduleName + MODULES_FILE);
                        break;
                    }
                }
            }
        }
    }
    return modules;

  }





}
