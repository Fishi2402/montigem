package login_module;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.gui.gentypescript.config.PackageRoute;

import java.util.Arrays;

@SuppressWarnings("unused")
public class PackagePathDecorator {
  public PackagePathDecorator(GlobalExtensionManagement glex) {

  }

  /**
   * Join a modules route into the route-tree
   *
   * @param packageRoute
   * @param packageName  the qualified package name, such as "gui", "loginmodule"
   */
  public void addRoute(PackageRoute packageRoute, String... packageName) {
    PackageRoute newRoute = new PackageRoute(Arrays.asList(packageName));
    packageRoute.addIfSubroute(newRoute);
  }
}
