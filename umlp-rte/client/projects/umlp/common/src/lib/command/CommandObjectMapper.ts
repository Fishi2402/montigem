/* (c) https://github.com/MontiCore/monticore */

import { Command } from "./Command";
import { CommandResult } from "./CommandResult";

// RTE-P

// Serialisiert und deserialisiert Commands und CommandResults
export abstract class CommandObjectMapper {
  protected static instance: CommandObjectMapper;

  protected static getInstance(): CommandObjectMapper {
    if (CommandObjectMapper.instance == null) {
      console.error("CommandObjectMapper is not initialized!");
    }
    return CommandObjectMapper.instance;
  }

  public static serializeCommand(command: Command<any>): string {
    try {
      return CommandObjectMapper.getInstance()._serializeCommand(command);
    } catch (e) {
      let msg: string = `0xS0002 Could not serialize command: ${command.getCommandType()}\n${e}`;
      console.error(msg);
      return null;
      // return CommandObjectMapper.getInstance()._serializeCommand(new Error(e));
    }
  }

  protected abstract _serializeCommand(command: Command<any>): string;


  public static deserializeCommand(cmdStr: string): Command<any> {
    try {
      return CommandObjectMapper.getInstance()._deserializeCommand(cmdStr);
    } catch (e) {
      let msg: string = `0xS0002 Could not deserialize command: ${cmdStr}\n${e}`;
      console.error(msg);
      return null;
    }
  }

  protected abstract _deserializeCommand(cmdStr: string): Command<any>;

  public static serializeResponse(result: CommandResult<any>): string {
    try {
      return CommandObjectMapper.getInstance()._serializeResponse(result);
    } catch (e) {
      let msg: string = `0xS0001 Could not serialize command: ${result.getTypeParameter()}\n${e}`;
      console.error(msg);
      // return CommandObjectMapper.getInstance()._serializeResponse(StatusResult.error(result.getCommandId()));
      return null;
    }
  }

  public abstract _serializeResponse(result: CommandResult<any>): string;

  public static deserializeResponse(responseStr: string): CommandResult<any> {
    try {
      return CommandObjectMapper.getInstance()._deserializeResponse(responseStr);
    } catch (e) {
      let msg: string = `0xS0002 Could not deserialize command: ${responseStr}\n${e}`;
      console.error(msg);
      return null;
    }
  }

  protected abstract _deserializeResponse(responseStr: string): CommandResult<any>;
}
