/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript;

import com.google.common.collect.Iterables;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
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

  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  protected final GeneratorEngine generatorEngine;
  protected GeneratorSetup setup;
  protected MCPath hwcPath;
  public final static String ROUTES_FILE = "GemRouting";
  public final static String MODULES_FILE = "GemModule";
  public final static String TS_FILE_EXTENSION = "ts";

  public GUI2TypeScriptGenerator(GeneratorSetup generatorSetup, MCPath hwcPath) {
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    this.setup = generatorSetup;
    this.hwcPath = hwcPath;
  }

  public List<String> generateAppConfigfile() {
    this.setup.setTracing(false);
    GeneratorEngine genEngine = new GeneratorEngine(setup);

    List<ASTGUICompilationUnit> astguiCompilationUnits = new ArrayList<>();

    boolean existHWCRoutes = existsHandwrittenFile(hwcPath, "app.routing.ts");
    // TODO AGe: change default app files path
    Path generatedRoutingFiles = Paths.get("../../src/app",
        "app.routing" + (existHWCRoutes ? "TOP" : "") + "." + TS_FILE_EXTENSION);
    genEngine.generateNoA("umlp.gui2ts.TSAppRoutes", generatedRoutingFiles,
        astguiCompilationUnits);

    Path generatedModuleFiles = Paths.get("../../src/app",
        "app.module." + TS_FILE_EXTENSION);
    Boolean existedModuleConfig = existsHandwrittenFile(hwcPath, "app.module.config.ts");

    String pathHWRoutes = "./app.routing.ts";
    if (existHWCRoutes) {
      Optional<String> filePath = util.getRelativeImportPathForAppConfigFile(
          hwcPath, setup.getOutputDirectory().toPath(),
          "app", "routing.ts");
      pathHWRoutes = filePath.get();
    }
    String pathModuleConfig = "";
    if (existedModuleConfig) {
      Optional<String> filePath = util.getRelativeImportPathForAppConfigFile(
          hwcPath, setup.getOutputDirectory().toPath(),
          "app", "module.config.ts");
      pathModuleConfig = filePath.get();
    }

    Optional<String> filePath = util.getRelativeImportPathForAppConfigFile(
        hwcPath, setup.getOutputDirectory().toPath(),
        "app", "component.ts");
    boolean existAppComponent = filePath.isPresent();
    String pathAppComponent = "";
    if (existAppComponent) {
      pathAppComponent = filePath.get();
    }

    genEngine.generateNoA("umlp.gui2ts.TSAppModule", generatedModuleFiles, existedModuleConfig, existHWCRoutes,
        existAppComponent,
        util.removeExtension(pathHWRoutes), util.removeExtension(pathModuleConfig), util.removeExtension(pathAppComponent));
    return Arrays.asList(generatedRoutingFiles.toString(), generatedModuleFiles.toString());
  }

  public List<String> generateConfigFileForPackages(Map.Entry<String, ModuleRoutingInfo> astGuiCompilationUnitList) {
    this.setup.setTracing(false);
    GeneratorEngine genEngine = new GeneratorEngine(setup);

    if (!astGuiCompilationUnitList.getValue().getComponents().isEmpty()) {
      return new ArrayList<>();
    }
    List<String> paths = new ArrayList<>();
    for (var ast : astGuiCompilationUnitList.getValue().getComponents()) {
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
    }

    List<List<String>> ids = new ArrayList<>();
    int fullPathIndex = -1;
    for (int index = 0; index < astGuiCompilationUnitList.getValue().getComponents().size(); index++) {
      ASTGUICompilationUnit ast = astGuiCompilationUnitList.getValue().getComponents().get(index);
      List<String> idStrings = util.getRoutingIds(ast);
      if (idStrings.isEmpty() && ast.getGUIComponentDeclaration().isPage()) {
        fullPathIndex = index;
      }
      ids.add(idStrings);
    }

    List<String> packageParts = astGuiCompilationUnitList.getValue().getPackageParts();
    String pathName = String.join("/", packageParts);
    String name = StringTransformations.capitalize(Iterables.getLast(packageParts));

    boolean existHWCRoutes = existsHandwrittenFile(hwcPath, pathName + "/" + name + ROUTES_FILE + "." + TS_FILE_EXTENSION);
    Path generatedRoutingFile = Paths.get(pathName,
        name + ROUTES_FILE + (existHWCRoutes ? "TOP" : "") + "." + TS_FILE_EXTENSION);

    // List<> modules = astGuiCompilationUnitList.getValue().getSubmodules();
    genEngine.generateNoA("umlp.gui2ts.TSPackageRoutes", generatedRoutingFile,
        astGuiCompilationUnitList, ids, fullPathIndex, paths, modules);

    boolean existHWCModules = existsHandwrittenFile(hwcPath, pathName + "/" + name + ".Module.Config." + TS_FILE_EXTENSION);
    String pathHWModule = "";
    if (existHWCModules) {
      Optional<String> filePath = util.getRelativeImportPath(
          hwcPath, setup.getOutputDirectory().toPath(), pathName + "." +
              name, "Module.Config.ts");
      if (filePath.isPresent())
        pathHWModule = filePath.get();
    }


    String pathHWRoutes = "./" + name + ".Routing.ts";
    if (existHWCRoutes) {
      Optional<String> filePath = util.getRelativeImportPath(
          hwcPath, setup.getOutputDirectory().toPath(), pathName + "." +
              name, "Routing.ts");
      if (filePath.isPresent())
        pathHWRoutes = filePath.get();
    }


    Path generatedModuleFiles = Paths.get(pathName, name + "." +
        MODULES_FILE + "." + TS_FILE_EXTENSION);
    String compDecl = name;


    Map<String, String> imports = new HashMap<>();
    for (ASTGUICompilationUnit ast : astGuiCompilationUnitList) {
      for (ASTMCImportStatement imp : ast.getMCImportStatementList()) {
        String moduleClassName = util.getModuleClassName(imp.getMCQualifiedName().getParts(imp.getMCQualifiedName().sizeParts() - 1));
        if (util.isGuiImport(imp) && !util.isCreatedTSComponent(moduleClassName)) {
          imports.put(moduleClassName, util.java2tsModule(imp.getQName()));
        }
        else if (util.isGuiImport(imp) && util.isCreatedTSComponent(moduleClassName) && !(compDecl + "_Module").equals(util.getPackageModuleName(imp))) {
          imports.put(util.getPackageModuleName(imp), util.getPackageModulePath(imp));
        }
      }
    }


    genEngine.generateNoA("umlp.gui2ts.TSPackageModule", generatedModuleFiles,
        astGuiCompilationUnitList, ids, compDecl, paths, existHWCModules, util.removeExtension(pathHWModule),
        util.removeExtension(pathHWRoutes), imports, modules);


    return Arrays.asList(generatedRoutingFile.toString(), generatedModuleFiles.toString());
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
   * Check whether a handwritten version of this generated file already exists and shall be integrated
   * (e.g. to apply TOP mechanism)
   */
  public static boolean existsHandwrittenFile(MCPath targetPath, String hwFile) {
    Optional<URL> hwFilePath = targetPath.find(hwFile);
    boolean result = hwFilePath.isPresent();
    if (result) {
      Optional<Path> hwPath = MCPath.toPath(hwFilePath.get());
      hwPath.ifPresent(path -> Reporting.reportUseHandwrittenCodeFile(path, Paths.get(hwFile)));
      Reporting.reportHWCExistenceCheck(targetPath, Paths.get(hwFile), hwFilePath);
    }
    return result;
  }

  public static Map<String, String> findNextLevelModule(String path) {
    Map<String, String> modules = new HashMap<>();

    File folder = new File(path);
    File[] listOfFiles = folder.listFiles();

    if (listOfFiles != null) {
      for (int i = 0; i < listOfFiles.length; i++) {
        if (listOfFiles[i].isDirectory()) {
          String moduleName = listOfFiles[i].getName().toUpperCase().charAt(0) + listOfFiles[i].getName().substring(1);
          if (new File(listOfFiles[i].getPath() + "/" + moduleName + ".Module.ts").exists()) {
            modules.put(moduleName, listOfFiles[i].getName());
          }
        }
      }
    }
    return modules;

  }


}
