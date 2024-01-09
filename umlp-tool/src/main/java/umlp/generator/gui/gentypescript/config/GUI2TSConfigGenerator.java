/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.config;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.io.paths.MCPath;
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

public class GUI2TSConfigGenerator {

  public final static String ROUTES_FILE = "GemRouting";
  public final static String MODULES_FILE = "GemModule";
  public final static String TS_FILE_EXTENSION = "ts";
  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();
  protected final GeneratorEngine generatorEngine;
  protected GeneratorSetup setup;
  protected MCPath hwcPath;

  public GUI2TSConfigGenerator(GeneratorSetup generatorSetup, MCPath hwcPath) {
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    this.setup = generatorSetup;
    this.hwcPath = hwcPath;
  }

  /**
   * Check whether a handwritten version of this generated file already exists
   */
  public static boolean existsHandwrittenFile(MCPath targetPath, String hwFile) {
    Optional<URL> hwFilePath = targetPath.find(hwFile);
    boolean result = hwFilePath.isPresent();
    if (result) {
      Optional<Path> hwPath = MCPath.toPath(hwFilePath.get());
      hwPath.ifPresent(path -> Reporting.reportUseHandwrittenCodeFile(path, Paths.get(hwFile)));
    }
    Reporting.reportHWCExistenceCheck(targetPath, Paths.get(hwFile), hwFilePath);
    return result;
  }

  private static Map<String, String> findNextLevelModuleForAppConfig(String path) {
    Map<String, String> modules = new HashMap<>();

    File folder = new File(path);
    File[] listOfFiles = folder.listFiles();

    if (listOfFiles != null) {
      for (File file : listOfFiles) {
        if (file.isDirectory()) {
          String moduleName = file.getName().toUpperCase().charAt(0) + file.getName().substring(1);
          if (new File(file.getPath() + "/" + moduleName + ROUTES_FILE + ".ts").exists()) {
            modules.put(moduleName, file.getName());
          }
        }
      }
    }
    return modules;
  }

  private static Map<String, String> findSubroutesForPackageConfig(Map.Entry<String, ModuleRoutingInfo> packageInfo,
      Map<String, ModuleRoutingInfo> routingInfoMap, MCPath targetPath) {
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
        String name = routingInfoMap.get(info).getRoutingFileNameBase();
        String path = routingInfoMap.get(info).getGeneratedFileDir().substring(packageInfo.getValue().getGeneratedFileDir().length());
        if (existsHandwrittenFile(targetPath, routingInfoMap.get(info).getGeneratedFileDir() + "/" + name + ROUTES_FILE + "." + TS_FILE_EXTENSION)) {
          routes.put(name + "Route", "@src/" + routingInfoMap.get(info).getGeneratedFileDir() + "/" + name + ROUTES_FILE);
        }
        else {
          routes.put(name + "Route", "." + path + "/" + name + ROUTES_FILE);
        }
      }
    }
    return routes;
  }

  public List<String> generateAppConfigFiles() {
    GeneratorEngine genEngine = new GeneratorEngine(setup);

    boolean existHWCRoutes = existsHandwrittenFile(hwcPath, "App" + ROUTES_FILE + "." + TS_FILE_EXTENSION);

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
    Boolean existedModuleConfig = existsHandwrittenFile(hwcPath, "App" + MODULES_FILE + "Config" + "." + TS_FILE_EXTENSION);

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

  public String generateComponentsRouting(
      Map.Entry<String, ModuleRoutingInfo> packageInfo,
      Map<String, ModuleRoutingInfo> routesMap
  ) {
    Path generatedRoutingFile = getGeneratedRoutingFile(packageInfo.getValue());
    List<ComponentRouting> packageComponentRoutings = getComponentRoutings(packageInfo.getValue());
    Optional<ComponentRouting> defaultRouting = getDefaultRouting(packageComponentRoutings);
    Map<String, String> subRoutes = findSubroutesForPackageConfig(packageInfo, routesMap, hwcPath);

    GeneratorEngine genEngine = new GeneratorEngine(setup);
    genEngine.generateNoA("umlp.gui2ts.TSPackageRoutes", generatedRoutingFile,
        packageComponentRoutings, defaultRouting.orElse(null), subRoutes);

    return generatedRoutingFile.toString();
  }

  protected Path getGeneratedRoutingFile(ModuleRoutingInfo routing) {
    String dir = routing.getGeneratedFileDir();
    String name = routing.getRoutingFileNameBase();

    boolean existHWCRoutes = existsHandwrittenFile(
        hwcPath, dir + "/" + name + ROUTES_FILE + "." + TS_FILE_EXTENSION);

    return Paths.get(dir, name + ROUTES_FILE + (existHWCRoutes ? "TOP" : "") + "." + TS_FILE_EXTENSION);
  }

  protected List<ComponentRouting> getComponentRoutings(ModuleRoutingInfo routing) {
    List<ComponentRouting> packageComponentRoutings = new ArrayList<>();

    for (ASTGUICompilationUnit component : routing.getComponents()) {
      if (!component.getGUIComponentDeclaration().isPage()) {
        continue;
      }
      Optional<String> filePath = util.getRelativeImportPath(
          hwcPath, setup.getOutputDirectory().toPath(),
          component.getComponentQName(), "ts"
      );
      boolean existHWC = filePath.isPresent();
      String componentFilePath;
      if (existHWC) {
        componentFilePath = filePath.get();
      }
      else {
        componentFilePath = "./" + component.getComponentName() + "Component";
      }
      List<String> idStrings = util.getRoutingIds(component);

      ComponentRouting componentRouting = new ComponentRouting(
          component,
          existHWC,
          idStrings,
          util.removeExtension(componentFilePath),
          routing.getRoutingPath()
      );
      packageComponentRoutings.add(componentRouting);
    }

    return packageComponentRoutings;
  }

  protected Optional<ComponentRouting> getDefaultRouting(List<ComponentRouting> routings) {
    for (ComponentRouting routing : routings) {
      if (!routing.getHasIds()) {
        return Optional.of(routing);
      }
    }
    return Optional.empty();
  }

}
