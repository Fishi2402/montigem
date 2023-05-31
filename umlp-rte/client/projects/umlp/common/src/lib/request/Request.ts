/* (c) https://github.com/MontiCore/monticore */

import { Command } from "../command/Command";

// RTE-P

// Wird vom Client zum Server geschickt um Informationen abzufragen.
// Hat keinen Effekt auf die Daten.
// Nutzt zur einfacheren Umsetzung dieselbe Infrastruktur wie Commands
export abstract class Request<T> extends Command<T> {
  public constructor(commandType: string) {
    super(commandType);
    return this;
  }

  public override isRequest(): boolean {
    return true;
  }
}
