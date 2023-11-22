/* (c) https://github.com/MontiCore/monticore */
package sehub;

import security.GetCurrentUserRequestDeSer;

public class SehubCommandDeSer extends SehubCommandDeSerTOP {
  public SehubCommandDeSer() {
    super();
    deSerMap.put("security.GetCurrentUserRequest", new GetCurrentUserRequestDeSer());
  }
}
