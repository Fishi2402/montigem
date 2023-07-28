/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import {Log} from '../logging/Log';
import {StringHelper} from '../javautil/StringHelper';

export abstract class CommandObjectMapper {
  static instance: CommandObjectMapper = null;

  static getInstance(): CommandObjectMapper {
    return <any>(/* requireNonNull */(obj => {
      if (obj == undefined || obj == null) {
        throw Object.defineProperty(new Error("CommandObjectMapper is not initialized!"), '__classes', {
          configurable: true,
          value: ['java.lang.Throwable', 'java.lang.Object', 'java.lang.RuntimeException', 'java.lang.NullPointerException', 'java.lang.Exception']
        })
      } else {
        return obj
      }
    })(CommandObjectMapper.instance));
  }

  public static async serializeCommand(command: Command<any>): Promise<string> {
    try {
      return await CommandObjectMapper.getInstance()._serializeCommand(command);
    } catch (e) {
      const msg: string = StringHelper.format("%s Could not serialize command: %s\n%s", "0xS0004", command.getCommandType(), e.message);
      Log.error(msg);
      // return await CommandObjectMapper.getInstance()._serializeCommand(new ServerError(e));
      return null;
    }
  }

  abstract _serializeCommand(command: Command<any>): Promise<string>;

  public static async deserializeCommand(cmdStr: string): Promise<Command<any>> {
    try {
      return await CommandObjectMapper.getInstance()._deserializeCommand(cmdStr);
    } catch (e) {
      const msg: string = StringHelper.format("%s Could not deserialize command: %s\n%s", "0xS0003", cmdStr, e.message);
      Log.error(msg);
      return null;
    }
  }

  abstract _deserializeCommand(cmdStr: string): Promise<Command<any>>;

  public static async serializeResponse(result: CommandResult<any>): Promise<string> {
    try {
      return await CommandObjectMapper.getInstance()._serializeResponse(result);
    } catch (e) {
      const msg: string = StringHelper.format("%s Could not serialize result: %s\n%s", "0xS0001", result.getTypeParameter(), e.message);
      Log.error(msg);
      return await CommandObjectMapper.getInstance()._serializeResponse(StatusResult.error(result.getCommandId()));
    }
  }

  public abstract _serializeResponse(result: CommandResult<any>): Promise<string>;

  public static async deserializeResponse(responseStr: string): Promise<CommandResult<any>> {
    try {
      return await CommandObjectMapper.getInstance()._deserializeResponse(responseStr);
    } catch (e) {
      const msg: string = StringHelper.format("%s Could not deserialize result : %s\n%s", "0xS0002", responseStr, e.message);
      Log.error(msg);
      return null;
    }
  }

  abstract _deserializeResponse(responseStr: string): Promise<CommandResult<any>>;
}

CommandObjectMapper["__class"] = "umlp.backendrte.command.CommandObjectMapper";


import {StatusResult} from './StatusResult';


import {CommandResult} from './CommandResult';


// import {ServerError} from './ServerError';


import {Command} from './Command';

