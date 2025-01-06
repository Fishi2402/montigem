package umlp.generator.gui.gentypescript.config;

import com.google.common.collect.Iterables;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.se_rwth.commons.StringTransformations;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PackageRoute {

  public final static String ROUTE_FILE_SUFFIX = "GemRouting";
  public final static String TS_FILE_EXTENSION = "ts";

  protected boolean existsHW = false;
  protected final List<String> packageParts;
  protected final List<ASTGUICompilationUnit> components = new ArrayList<>();
  protected final List<PackageRoute> children = new ArrayList<>();
  protected Optional<PackageRoute> parent = Optional.empty();
  protected Optional<ComponentRoute> defaultRoute = Optional.empty();

  public PackageRoute(List<String> packageParts) {
    this.packageParts = packageParts;
  }

  public void setExistsHW(boolean existsHW) {
    this.existsHW = existsHW;
  }

  public void add(ASTGUICompilationUnit comp) {
    components.add(comp);
  }

  public List<ASTGUICompilationUnit> getComponents() {
    return components;
  }

  public void addIfSubroute(PackageRoute route) {
    List<PackageRoute> remove = new ArrayList<>();
    boolean addSub = true;

    for (PackageRoute r : children) {
      if (route.isParentRouteOf(r) && this.isParentRouteOf(route)) {
        remove.add(r);
      }
      if (r.isParentRouteOf(route)) {
        addSub = false;
      }
    }

    children.removeAll(remove);
    if (addSub) {
      children.add(route);
      route.setParent(this);
    }
  }

  public List<PackageRoute> getChildren() {
    return children;
  }

  protected void setParent(PackageRoute route) {
    this.parent = Optional.of(route);
  }

  public Optional<PackageRoute> getParent() {
    return parent;
  }

  public void setDefaultRoute(Optional<ComponentRoute> defaultRoute) {
    this.defaultRoute = defaultRoute;
  }

  public Optional<ComponentRoute> getDefaultRoute() {
    return defaultRoute;
  }

  public String getURLSegment() {
    return Iterables.getFirst(packageParts, "");
  }

  public String getRoutingFileNameBase() {
    return StringTransformations.capitalize(Iterables.getLast(packageParts));
  }

  public String getAlias() {
    return getRoutingFileNameBase() + "Routes";
  }

  public String getFileNameNoExt() {
    return getRoutingFileNameBase() + ROUTE_FILE_SUFFIX;
  }

  public String getImportPath() {
    return getImportDir() + "/" + getRoutingFileNameBase() + ROUTE_FILE_SUFFIX;
  }

  public String getPathFromRoot() {
    return getImportDir() + "/" + getRoutingFileNameBase() + ROUTE_FILE_SUFFIX + "." + TS_FILE_EXTENSION;
  }

  public Path getGenPath() {
    String dir = getImportDir();
    String name = getRoutingFileNameBase();
    return Paths.get(dir, name + ROUTE_FILE_SUFFIX + (existsHW ? "TOP" : "") + "." + TS_FILE_EXTENSION);
  }

  public String getPathStringNoExt() {
    return packageParts.size() > 1
        ? String.join("/", packageParts.subList(1, packageParts.size()))
        : "";
  }

  public String getImportDir() {
    return String.join("/", packageParts);
  }

  public boolean isParentRouteOf(PackageRoute route) {
    if (route.packageParts.size() <= packageParts.size()) {
      return false;
    }
    for (int i = 0; i < packageParts.size(); ++i) {
      String ourPart = packageParts.get(i);
      String theirPart = route.packageParts.get(i);
      if (!ourPart.equals(theirPart)) {
        return false;
      }
    }
    return true;
  }
}
