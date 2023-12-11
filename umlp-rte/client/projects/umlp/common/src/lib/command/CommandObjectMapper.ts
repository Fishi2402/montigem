/* (c) https://github.com/MontiCore/monticore */

import {Command} from "./Command";
import {CommandResult} from "./CommandResult";
// import {ServerError} from "./ServerError"; // Leads to errors in packed js

// RTE-P

// Serialisiert und deserialisiert Commands und CommandResults
export abstract class CommandObjectMapper {
  protected static instance: CommandObjectMapper;

  protected static setInstance(instance: CommandObjectMapper): void {
    CommandObjectMapper.instance = instance;
  }

  protected static getInstance(): CommandObjectMapper {
    if (CommandObjectMapper.instance == null) {
      console.error("CommandObjectMapper is not initialized!");
    }
    return CommandObjectMapper.instance;
  }

  public static serializeCommand(command: Command<any>): string {
    let err: Error | null = null;

    try {
      const result = CommandObjectMapper.getInstance()._serializeCommand(command);
      if (result !== null && result !== undefined) {
        return result;
      } else {
        err = new Error("Instance returned null or undefined");
      }
    } catch (e) {
      err = e;
    }

    const msg: string = `0xS0004 Could not serialize command: ${command.getCommandType()}\n${err.message}`;
    console.error(msg);

    // Leads to errors in packed js
    //const serverError = new ServerError(err);
    //return CommandObjectMapper.getInstance()._serializeCommand(serverError) as string;
    return null;
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
