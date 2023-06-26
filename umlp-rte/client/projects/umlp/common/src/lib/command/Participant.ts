/* (c) https://github.com/MontiCore/monticore */

import { Command } from "./Command";
import { ServerCommandCommunication } from "./ServerCommandCommunication";

// Ausbaustufe: nicht alle Commands versenden, nur für Objekte die der Client geladen hat
export class Participant {
  protected gemIds: Set<number> = new Set();
  protected commandCommunication: ServerCommandCommunication;

  public Participant(commandCommunication: ServerCommandCommunication) {
    this.commandCommunication = commandCommunication;
  }

  public sendCommand<T> (command: Command<T>): void {
    this.commandCommunication.sendCommandToParticipant(command);
  }

  // Überprüft, ob dieser Client updates zum Datensatz mit der übergebenen gemId erhalten muss.
  public isSubscribedTo(gemId: number): boolean {
    return this.gemIds.has(gemId);
  }

  public subscribe(gemIds: number[]): void {
    for (const id of gemIds) {
      this.gemIds.add(id);
    }
  }
}
