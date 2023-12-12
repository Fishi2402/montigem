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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

  private static Map<String, String> findNextLevelModuleForAppConfig(String path) {
    Map<String, String> modules = new HashMap<>();

    File folder = new File(path);
    File[] listOfFiles = folder.listFiles();

    if (listOfFiles != null) {
      for (int i = 0; i < listOfFiles.length; i++) {
        if (listOfFiles[i].isDirectory()) {
          String moduleName = listOfFiles[i].getName().toUpperCase().charAt(0) + listOfFiles[i].getName().substring(1);
          if (new File(listOfFiles[i].getPath() + "/" + moduleName + ROUTES_FILE + ".ts").exists()) {
            modules.put(moduleName, listOfFiles[i].getName());
          }
        }
      }
    }
    return modules;

  }

  private static Map<String, String> findSubmoduleForPackageConfig(Map.Entry<String, ModuleRoutingInfo> packageInfo,
      Map<String, ModuleRoutingInfo> routingInfoMap) {
    Map<String, String> modules = new HashMap<>();
    Set<String> subModuleInfo = new HashSet<>();
    routingInfoMap.forEach((key, value) -> {
      if (key.contains(packageInfo.getKey()) && !key.equals(packageInfo.getKey())) {
        subModuleInfo.add(key);
      }
    });
    for (String info : subModuleInfo) {
      boolean imported = true;
      for (String infoParent : subModuleInfo) {
        if (info.contains(infoParent) && !info.equals(infoParent)) {
          imported = false;
          break;
        }
      }
      if (imported) {
        String name = StringTransformations.capitalize(Iterables.getLast(routingInfoMap.get(info).getPackageParts()));
        String path = "." + routingInfoMap.get(info).getGeneratedFileDir().substring(packageInfo.getValue().getGeneratedFileDir().length());
        modules.put(name + "GemModule", path + "/" + name + MODULES_FILE);
      }
    }
    return modules;

  }

  private static Map<String, String> findSubroutesForPackageConfig(Map.Entry<String, ModuleRoutingInfo> packageInfo,
      Map<String, ModuleRoutingInfo> routingInfoMap) {
    Map<String, String> routes = new HashMap<>();
    Set<String> subModuleInfo = new HashSet<>();
    routingInfoMap.forEach((key, value) -> {
      if (key.contains(packageInfo.getKey()) && !key.equals(packageInfo.getKey())) {
        subModuleInfo.add(key);
      }
    });
    for (String info : subModuleInfo) {
      boolean imported = true;
      for (String infoParent : subModuleInfo) {
        if (info.contains(infoParent) && !info.equals(infoParent)) {
          imported = false;
          break;
        }
      }
      if (imported) {
        String name = StringTransformations.capitalize(Iterables.getLast(routingInfoMap.get(info).getPackageParts()));
        String path = "." + routingInfoMap.get(info).getGeneratedFileDir().substring(packageInfo.getValue().getGeneratedFileDir().length());
        routes.put(name+"Route", path + "/" + name + ROUTES_FILE);
      }
    }
    return routes;

  }

  public List<String> generateAppConfigfile() {
    this.setup.setTracing(false);
    GeneratorEngine genEngine = new GeneratorEngine(setup);

    boolean existHWCRoutes = existsHandwrittenFile(hwcPath, "App" + ROUTES_FILE + ".ts");

    Map<String, String> routingModules = findNextLevelModuleForAppConfig(setup.getOutputDirectory().toString());
    String defaultRouting = "";
    if (!routingModules.isEmpty()) {
      defaultRouting = routingModules.keySet().toArray()[0].toString();
    }
    Path generatedRoutingFiles = Paths.get(
        "App" + ROUTES_FILE + (existHWCRoutes ? "TOP" : "") + ".ts");
    genEngine.generateNoA("umlp.gui2ts.TSAppRoutes", generatedRoutingFiles, routingModules, defaultRouting);

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

  public List<String> generateConfigFileForPackages(Map.Entry<String, ModuleRoutingInfo> packageInfo, Map<String, ModuleRoutingInfo> routingInfoMap) {
    List<ASTGUICompilationUnit> astguiCompilationUnitList = packageInfo.getValue().getComponents();
    this.setup.setTracing(false);
    GeneratorEngine genEngine = new GeneratorEngine(setup);

    List<String> paths = new ArrayList<>();
    List<Boolean> existHWC = new ArrayList<>();
    astguiCompilationUnitList.forEach(ast -> {
      boolean existsHandwrittenDomainClass = GUIGeneratorUtil.getInstance().existsHandwrittenDomainClass(hwcPath, ast);
      existHWC.add(existsHandwrittenDomainClass);
      Optional<String> filePath = util.getRelativeImportPath(
          hwcPath, setup.getOutputDirectory().toPath(),
          ast.getMCPackageDeclaration().getMCQualifiedName().getQName()
              + "." + ast.getComponentName() + (existsHandwrittenDomainClass ? "" : "Component"), "ts");
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
    String pathName = packageInfo.getValue().getGeneratedFileDir();
    String routingLinkPrefix = packageInfo.getValue().getRoutingPath();
    String name = StringTransformations.capitalize(Iterables.getLast(packageParts));

    boolean existHWCRoutes = existsHandwrittenFile(hwcPath, pathName + "/" + name + ROUTES_FILE + "." + TS_FILE_EXTENSION);
    Path generatedRoutingFile = Paths.get(pathName,
        name + ROUTES_FILE + (existHWCRoutes ? "TOP" : "") + "." + TS_FILE_EXTENSION);
    Map<String, String> subRoutes= findSubroutesForPackageConfig(packageInfo, routingInfoMap);
    genEngine.generateNoA("umlp.gui2ts.TSPackageRoutes", generatedRoutingFile,
        astguiCompilationUnitList, existHWC, ids, fullPathIndex, paths, routingLinkPrefix,subRoutes);

    return Arrays.asList(generatedRoutingFile.toString());
  }

  public String genStandaloneConfig(ASTGUICompilationUnit ast) {
    this.setup.setTracing(false);
    GeneratorEngine genEngine = new GeneratorEngine(setup);
    String componentName = ast.getComponentName();
    String pathName = util.getGenFileFolder(ast.getMCPackageDeclaration());
    Path filePath = Paths.get(pathName, componentName + "StandaloneConfig.ts");
    genEngine.generateNoA("umlp.gui2ts.TSStandaloneComponentConfig", filePath, ast, hwcPath);
    return filePath.toString();
  }

}
