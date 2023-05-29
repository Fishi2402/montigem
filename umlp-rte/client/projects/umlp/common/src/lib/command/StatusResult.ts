/* (c) https://github.com/MontiCore/monticore */

import { CommandResult } from "./CommandResult";

// RTE-P

// Repräsentiert das Ergebnis eines ausgeführten Commands ohne zusätzliche Rückgabe, z.B. eines set Commands für ein
// Attribut. Der Sender des Commands muss trotzdem wissen, ob dieser erfolgreich ausgeführt wurde.
export class StatusResult extends CommandResult<void> {
  protected successful: boolean;
  protected StatusResult(id: number, successful: boolean): StatusResult {
    // TODO AGe: what?
    super.CommandResult(id, "java.lang.Void");
    this.successful = successful;
    return this;
  }

  public static ok(id: number): StatusResult {
    return new StatusResult().StatusResult(id, true);
  }

  public static error(id: number): StatusResult {
    return new StatusResult().StatusResult(id, false);
  }

  public wasSuccessful(): boolean {
    return this.successful;
  }

  public override isStatusResult(): boolean  {
    return true;
  }
}
