/* (c) https://github.com/MontiCore/monticore */

import {DtcockpitCommandDeSerTOP} from "@src/dtcockpit/DtcockpitCommandDeSerTOP";
import {GetCurrentUserRequestDeSer} from "../security/GetCurrentUserRequestDeSer";
import {StartMontiVerseLitePipelineDeSer} from "../command/StartMontiVerseLitePipelineDeSer";

export class DtcockpitCommandDeSer extends DtcockpitCommandDeSerTOP  {
  public constructor() {
    super();
    this.deSerMap.set("security.GetCurrentUserRequest", new GetCurrentUserRequestDeSer());
    this.deSerMap.set("dtcockpit.commands.StartMontiVerseLitePipeline", new StartMontiVerseLitePipelineDeSer());
  }
}
