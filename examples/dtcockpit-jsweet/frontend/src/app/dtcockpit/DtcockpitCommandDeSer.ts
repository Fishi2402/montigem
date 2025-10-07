/* (c) https://github.com/MontiCore/monticore */

import {DtcockpitCommandDeSerTOP} from "dtcockpit/DtcockpitCommandDeSerTOP";
import {GetCurrentUserRequestDeSer} from "../security/GetCurrentUserRequestDeSer";

export class DtcockpitCommandDeSer extends DtcockpitCommandDeSerTOP  {
  public constructor() {
    super();
    this.deSerMap.set("security.GetCurrentUserRequest", new GetCurrentUserRequestDeSer());
  }
}
