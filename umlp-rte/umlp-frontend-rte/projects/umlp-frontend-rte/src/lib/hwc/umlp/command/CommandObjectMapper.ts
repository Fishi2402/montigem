/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
import {Command, CommandResult} from "@umlp-rte/target/command";

export abstract class CommandObjectMapper {
    static instance: CommandObjectMapper = null;

    static getInstance(): CommandObjectMapper {
        if (CommandObjectMapper.instance == null) {
  throw new Error('cannot be null');
}
return CommandObjectMapper.instance;
    }

    public static serializeCommand(command: Command<any>): string {
        return CommandObjectMapper.getInstance()._serializeCommand(command);
    }

    abstract _serializeCommand(command: Command<any>): string;

    public static deserializeCommand(cmdStr: string): Command<any> {
        return CommandObjectMapper.getInstance()._deserializeCommand(cmdStr);
    }

    abstract _deserializeCommand(cmdStr: string): Command<any>;

    public static serializeResponse(result: CommandResult<any>): string {
        return CommandObjectMapper.getInstance()._serializeResponse(result);
    }

    public abstract _serializeResponse(result: CommandResult<any>): string;

    public static deserializeResponse(responseStr: string): CommandResult<any> {
        return CommandObjectMapper.getInstance()._deserializeResponse(responseStr);
    }

    abstract _deserializeResponse(responseStr: string): CommandResult<any>;
}
CommandObjectMapper["__class"] = "umlp.command.CommandObjectMapper";


