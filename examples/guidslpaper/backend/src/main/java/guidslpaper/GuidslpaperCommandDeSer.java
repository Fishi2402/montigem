/* (c) https://github.com/MontiCore/monticore */
package guidslpaper;

import security.GetCurrentUserRequestDeSer;

public class GuidslpaperCommandDeSer extends GuidslpaperCommandDeSerTOP {
  public GuidslpaperCommandDeSer() {
    super();
    deSerMap.put("security.GetCurrentUserRequest", new GetCurrentUserRequestDeSer());
  }
}
