/* (c) https://github.com/MontiCore/monticore */

import { LogUtil } from "../util/LogUtil";
import { Command } from "./Command";
import { CommandObjectMapper } from "./CommandObjectMapper";
import { CommandResult } from "./CommandResult";

export class CommandManager {
  protected participantId: number = null;

  protected constructor() {
  }

  protected static instance: CommandManager;

  public static getParticipantId(): number  {
    return CommandManager.getInstance()._getParticipantId();
  }

  protected _getParticipantId(): number {
    return this.participantId;
  }

  public static init(): void {
    this.instance = new CommandManager();
  }

  public static initMe(commandManager: CommandManager): void {
    this.instance = commandManager;
  }

  public static getInstance(): CommandManager  {
    if (this.instance == null) {
      console.error("0x10000 Command manager was not initialized");
    }
    return this.instance;
  }

  public static executeCommand<T>(command: Command<T>): Promise<CommandResult<T>>  {
    console.log("Exec command " + command.getId() + " " + command.getCommandType() + " from " + command.getParticipantId(), LogUtil.getLoggerName());
    console.log(CommandObjectMapper.serializeCommand(command), LogUtil.getLoggerName());
    return CommandManager.getInstance()._executeCommand(command);
  }

  protected _executeCommand<T> (command: Command<T>): Promise<CommandResult<T>>  {
    // TODO: error handling if doRun fails
    return command.doRun();
  }

  protected freeCommandId: number = 0;

  public static getFreeCommandId(): number {
    return CommandManager.getInstance()._getFreeCommandId();
  }

  protected _getFreeCommandId(): number  {
    return this.freeCommandId++;
  }
}
