/* (c) https://github.com/MontiCore/monticore */
package dtcockpit;

import security.GetCurrentUserRequestDeSer;

public class DtcockpitCommandDeSer extends DtcockpitCommandDeSerTOP {
  public DtcockpitCommandDeSer() {
    super();
    deSerMap.put("security.GetCurrentUserRequest", new GetCurrentUserRequestDeSer());
  }
}
