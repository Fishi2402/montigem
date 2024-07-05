/* (c) https://github.com/MontiCore/monticore */

import {FinanzverwaltungCommandDeSerTOP} from "@src/finanzverwaltung/FinanzverwaltungCommandDeSerTOP";
import {GetCurrentUserRequestDeSer} from "../security/GetCurrentUserRequestDeSer";

export class FinanzverwaltungCommandDeSer extends FinanzverwaltungCommandDeSerTOP  {
  public constructor() {
    super();
    this.deSerMap.set("security.GetCurrentUserRequest", new GetCurrentUserRequestDeSer());
  }
}
