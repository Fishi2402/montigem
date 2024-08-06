/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.config;

import de.monticore.guidsl._ast.ASTGUICompilationUnit;

import java.util.List;

public class ComponentRoute {
  protected final String importPathNoExt;
  protected final String importedSymbolName;
  protected final String urlPrefix;
  protected final String url;
  protected final boolean hasIds;

  public ComponentRoute(
      ASTGUICompilationUnit ast,
      boolean existsHWC,
      List<String> ids,
      String importPathNoExt,
      String urlPrefix
  ) {
    this.importPathNoExt = importPathNoExt;
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

  public String getImportedSymbolName() {
    return importedSymbolName;
  }

  public String getImportPathNoExt() {
    return importPathNoExt;
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

}
