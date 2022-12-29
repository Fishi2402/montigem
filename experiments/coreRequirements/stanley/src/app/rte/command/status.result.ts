/* (c) https://github.com/MontiCore/monticore */
// Repräsentiert das Ergebnis eines ausgeführten Commands ohne zusätzliche Rückgabe, z.B. eines set Commands für ein
// Attribut. Der Sender des Commands muss trotzdem wissen, ob dieser erfolgreich ausgeführt wurde.
import {CommandResult} from "./command.result";

export class StatusResult extends CommandResult<void> {
  protected successful: boolean;

  protected constructor(id: number, successful: boolean) {
    super(id, "java.lang.Void");
    this.successful = successful;
  }

  public static ok(id: number): StatusResult {
    return new StatusResult(id, true);
  }

  public static error(id: number): StatusResult {
    return new StatusResult(id, false);
  }

  public wasSuccessful(): boolean {
    return this.successful;
  }

  public override isStatusResult(): boolean {
    return true;
  }
}
