/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.config;

import de.monticore.guidsl._ast.ASTGUICompilationUnit;

import java.util.List;

public class ComponentRouting {
  protected final String importPath;
  protected final String importedSymbolName;
  protected final String urlPrefix;
  protected final String url;
  protected final boolean hasIds;

  public String getImportedSymbolName() {
    return importedSymbolName;
  }

  public String getImportPath() {
    return importPath;
  }

  public String getUrlPrefix() {
    return urlPrefix;
  }

  public String getUrl() {
    return url;
  }

  public boolean getHasIds() {
    return hasIds;
  }

  public ComponentRouting(
      ASTGUICompilationUnit ast,
      boolean existsHWC,
      List<String> ids,
      String importPath,
      String urlPrefix
  ) {
    this.importPath = importPath;
    this.urlPrefix = urlPrefix;

    String importedSymbolName = ast.getComponentName();
    if (!existsHWC) {
      importedSymbolName += "Component";
    }
    this.importedSymbolName = importedSymbolName;

    String url = urlPrefix;
    if (!urlPrefix.isEmpty()) {
      url += "/";
    }
    url += ast.getComponentName();
    if (!ids.isEmpty()) {
      url += "/:" + String.join("/:", ids);
    }
    this.url = url;

    this.hasIds = !ids.isEmpty();
  }
}
