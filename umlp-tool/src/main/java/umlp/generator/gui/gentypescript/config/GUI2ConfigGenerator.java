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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GUI2ConfigGenerator {

  public final static String APP_FILE_PREFIX = "App";
  public final static String ROUTE_FILE_SUFFIX = "GemRouting";
  public final static String MODULE_FILE_SUFFIX = "GemModule";
  public final static String TS_FILE_EXTENSION = "ts";

  public final static String APP_ROUTE_FILE = APP_FILE_PREFIX + ROUTE_FILE_SUFFIX + "." + TS_FILE_EXTENSION;
  public final static String APP_ROUTE_TOP_FILE = APP_FILE_PREFIX + ROUTE_FILE_SUFFIX + "TOP" + "." + TS_FILE_EXTENSION;

  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();
  protected final GeneratorEngine generatorEngine;
  protected GeneratorSetup setup;
  protected MCPath hwcPath;

  public GUI2ConfigGenerator(GeneratorSetup generatorSetup) {
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    this.setup = generatorSetup;
    this.hwcPath = generatorSetup.getHandcodedPath();
  }

  public List<String> generateConfig(Collection<PackageRoute> allRoutes) {
    // fill route objects with extra info
    addExtraInfo(allRoutes);

    List<String> res = new ArrayList<>();
    // generate config for each route and add info to the route objects for the next step
    for (PackageRoute route : allRoutes) {
      res.add(generateRouteFiles(route));
    }
    // generate global config
    res.addAll(generateAppConfigFiles(allRoutes));
    return res;
  }

  /**
   * Fill PackageRoute objects with extra info, such as if handwritten route exists and which
   * routes are subroutes of other routes.
   *
   * @param allRoutes All available routes.
   */
  protected void addExtraInfo(Collection<PackageRoute> allRoutes) {
    // preparation
    for (PackageRoute r1 : allRoutes) {
      // set if HW file exists
      String dir = r1.getImportDir();
      String name = r1.getRoutingFileNameBase();
      boolean existHWRoute = util.existsHandwrittenFile(
          hwcPath, dir + "/" + name + ROUTE_FILE_SUFFIX + "." + TS_FILE_EXTENSION);
      r1.setExistsHW(existHWRoute);

      // set subroutes for each route
      for (PackageRoute r2 : allRoutes) {
        if (r1.isParentRouteOf(r2)) {
          r1.addIfSubroute(r2);
        }
      }
    }
  }

  protected String generateRouteFiles(PackageRoute packageRoute) {
    Path generatedRoutingFile = packageRoute.getGenPath();
    List<ComponentRoute> componentRoutes = getPackageRoutes(packageRoute);
    Optional<ComponentRoute> defaultRoute = getDefaultRoute(componentRoutes);
    packageRoute.setDefaultRoute(defaultRoute);

    GeneratorEngine genEngine = new GeneratorEngine(setup);
    genEngine.generateNoA("umlp.gui2ts.TSPackageRoutes", generatedRoutingFile,
        packageRoute, componentRoutes, defaultRoute);

    return generatedRoutingFile.toString();
  }

  protected List<String> generateAppConfigFiles(Collection<PackageRoute> allRoutes) {
    GeneratorEngine genEngine = new GeneratorEngine(setup);

    String pathHWRoutes = "./" + APP_ROUTE_FILE;
    Optional<String> appRoutePath = util.getRelativeImportPathForAppConfigFile(
        hwcPath, setup.getOutputDirectory().toPath(),
        APP_FILE_PREFIX + ROUTE_FILE_SUFFIX, TS_FILE_EXTENSION);
    boolean existHWRoute = appRoutePath.isPresent();
    if (existHWRoute) {
      pathHWRoutes = util.removeExtension(appRoutePath.get());
    }

    List<PackageRoute> routes = findTopMostRoutes(allRoutes);
    String defaultRouting = "";
    for (PackageRoute r : routes) {
      if (r.getDefaultRoute().isPresent()) {
        defaultRouting = r.getURLSegment();
      }
    }
    Path genAppRoutePath = Paths.get(existHWRoute ? APP_ROUTE_TOP_FILE : APP_ROUTE_FILE);
    genEngine.generateNoA("umlp.gui2ts.TSAppRoutes",
        genAppRoutePath, getAppPath2RouteMap(routes), defaultRouting);

    Path generatedModuleFiles = Paths.get(APP_FILE_PREFIX + MODULE_FILE_SUFFIX + "." + TS_FILE_EXTENSION);

    String pathModuleConfig = "";
    Optional<String> appModulePath = util.getRelativeImportPathForAppConfigFile(
        hwcPath, setup.getOutputDirectory().toPath(),
        APP_FILE_PREFIX + MODULE_FILE_SUFFIX + "Config", TS_FILE_EXTENSION);
    boolean existHWModuleConfig = appModulePath.isPresent();
    if (existHWModuleConfig) {
      pathModuleConfig = util.removeExtension(appModulePath.get());
    }

    genEngine.generateNoA("umlp.gui2ts.TSAppModule",
        generatedModuleFiles, existHWModuleConfig, existHWRoute, pathHWRoutes, pathModuleConfig);

    return Arrays.asList(genAppRoutePath.toString(), generatedModuleFiles.toString());
  }

  /**
   * Collect route configurations for a specific package. For example, given pages
   * "foo/bar/MyPage.gui" and "foo/OtherPage.gui", package "foo.bar" gets routing information of
   * "MyPage.gui", package "foo" gets routing information of "OtherPage.gui".
   *
   * @param route - routing information of a package.
   * @return list of component routing information belonging to the package.
   */
  protected List<ComponentRoute> getPackageRoutes(PackageRoute route) {
    List<ComponentRoute> packageRoutes = new ArrayList<>();

    for (ASTGUICompilationUnit component : route.getComponents()) {
      Optional<String> filePath = util.getRelativeImportPathForClass(
          hwcPath, setup.getOutputDirectory().toPath(),
          component.getComponentQName()
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

      ComponentRoute componentRouting = new ComponentRoute(
          component,
          existHWC,
          idStrings,
          util.removeExtension(componentFilePath),
          route.getPathStringNoExt()
      );
      packageRoutes.add(componentRouting);
    }

    return packageRoutes;
  }

  /**
   * Select a default component to navigate to for a package.
   *
   * @param routes - a list of component routing information
   * @return default component for routing (navigation) when URL is underspecified
   */
  protected Optional<ComponentRoute> getDefaultRoute(List<ComponentRoute> routes) {
    for (ComponentRoute route : routes) {
      if (!route.getHasIds()) {
        return Optional.of(route);
      }
    }
    return Optional.empty();
  }

  protected List<PackageRoute> findTopMostRoutes(Collection<PackageRoute> allRoutes) {
    List<PackageRoute> routes = new ArrayList<>();

    for (PackageRoute r : allRoutes) {
      if (r.getParent().isEmpty()) {
        routes.add(r);
      }
    }
    return routes;
  }

  protected Map<String, PackageRoute> getAppPath2RouteMap(Collection<PackageRoute> routes) {
    Map<String, PackageRoute> routeMap = new HashMap<>();
    for (PackageRoute route : routes) {
      String relativePath = "./" + route.getPathFromRoot();
      Path appRouteFolder = setup.getOutputDirectory().toPath();
      String hwFile = route.getPathFromRoot();

      Optional<URL> hwFilePath = hwcPath.find(hwFile);
      Optional<String> res = Optional.empty();
      if (hwFilePath.isPresent()) {
        Optional<Path> hwPath = MCPath.toPath(hwFilePath.get());
        if (hwPath.isPresent()) {
          res = Optional.of(appRouteFolder
              .toAbsolutePath()
              .normalize()
              .relativize(hwPath.get().toAbsolutePath())
              .toString()
              .replaceAll("\\\\", "/"));

          Reporting.reportUseHandwrittenCodeFile(hwPath.get(), Paths.get(hwFile));
        }
      }
      Reporting.reportHWCExistenceCheck(hwcPath, Paths.get(hwFile), hwFilePath);

      if (res.isPresent()) {
        relativePath = res.get();
      }
      routeMap.put(util.removeExtension(relativePath), route);
    }
    return routeMap;
  }

}
