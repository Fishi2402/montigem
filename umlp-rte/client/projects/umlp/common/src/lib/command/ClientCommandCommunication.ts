/* (c) https://github.com/MontiCore/monticore */

import { Result } from "../common/Result";
import { Command } from "./Command";
import { CommandReceiveObserver } from "./CommandReceiveObserver";
import { CommandResult } from "./CommandResult";
import { CommandResultReceiveObserver } from "./CommandResultReceiveObserver";

export interface ClientCommandCommunication {

  /**
   * Sendet ein Command an den Server und wartet auf das Ergebnis
   */
  sendCommandToServerAndReceiveResult<T>(command: Command<T>): CommandResult<T> ;

  /**
   * Initiiert die Verbindung und liefert die vom Server verteilte Participant Id
   */
  init(): Result<number> ;

  onCommandReceived(observer: CommandReceiveObserver): ClientCommandCommunication;

  onCommandResultReceived(observer: CommandResultReceiveObserver): ClientCommandCommunication;
}
