/* (c) https://github.com/MontiCore/monticore */

import {GuidslpaperCommandDeSerTOP} from "guidslpaper/GuidslpaperCommandDeSerTOP";
import {GetCurrentUserRequestDeSer} from "../security/GetCurrentUserRequestDeSer";

export class GuidslpaperCommandDeSer extends GuidslpaperCommandDeSerTOP  {
  public constructor() {
    super();
    this.deSerMap.set("security.GetCurrentUserRequest", new GetCurrentUserRequestDeSer());
  }
}
