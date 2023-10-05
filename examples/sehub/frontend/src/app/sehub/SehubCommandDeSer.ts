/* (c) https://github.com/MontiCore/monticore */

import {SehubCommandDeSerTOP} from "@src/sehub/SehubCommandDeSerTOP";
import {GetCurrentUserRequestDeSer} from "../security/GetCurrentUserRequestDeSer";

export class SehubCommandDeSer extends SehubCommandDeSerTOP  {
  public constructor() {
    super();
    this.deSerMap.set("security.GetCurrentUserRequest", new GetCurrentUserRequestDeSer());
  }
}
