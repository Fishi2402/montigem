/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { JsonPrinter } from '../mcjson/JsonPrinter';
import { Log } from '../logging/Log';
import {CommandResult} from "./CommandResult";
import {StatusResult} from "./StatusResult";
import {BaseClassObjectMapper} from "../common";
import {JsonDeSer} from "./JsonDeSer";

export class CommandResultDeSer implements JsonDeSer<CommandResult<any>> {
    /* Default method injected from umlp.backendrte.command.JsonDeSer */
    serialize$java_lang_Object(obj: any): Promise<string> {
        const printer: JsonPrinter = new JsonPrinter(true);
        this.buildJson(obj, printer);
        return Promise.resolve(printer.getContent());
    }
    public buildJson$umlp_backendrte_command_CommandResult$de_monticore_symboltable_serialization_JsonPrinter(result: CommandResult<any>, printer: JsonPrinter) {
        printer.member("commandId", result.getCommandId());
        const tp: string = result.getTypeParameter();
        printer.member("typeParameter", tp);
        printer.member("statusResult", result.isStatusResult());
        printer.member("simpleResult", result.isSimpleResult());
        if (result.isStatusResult()){
            printer.member("successful", result.asStatusResult().wasSuccessful());
        } else {
            if (BaseClassObjectMapper.canDeSer(tp)){
                const v: string = BaseClassObjectMapper.serialize(result.asSimpleResult().get(), tp);
                if (/* isPresent */(v != null)){
                    printer.memberJson("value", /* get */(v => { if (v == null) throw new Error('value is null'); return v; })(v));
                } else {
                    Log.warn("Error while serializing type " + tp);
                }
            }
        }
    }

    public buildJson(result?: any, printer?: any) {
        if (((result != null && result instanceof <any>CommandResult) || result === null) && ((printer != null && printer instanceof <any>JsonPrinter) || printer === null)) {
            return <any>this.buildJson$umlp_backendrte_command_CommandResult$de_monticore_symboltable_serialization_JsonPrinter(result, printer);
        } else throw new Error('invalid overload');
    }

    public serialize$umlp_backendrte_command_CommandResult(command: CommandResult<any>): Promise<string> {
        const printer: JsonPrinter = new JsonPrinter(true);
        printer.beginObject();
        this.buildJson$umlp_backendrte_command_CommandResult$de_monticore_symboltable_serialization_JsonPrinter(command, printer);
        printer.endObject();
        return Promise.resolve(printer.getContent());
    }

    /**
     *
     * @param {CommandResult} command
     * @return {string}
     */
    public serialize(command?: any): Promise<string> {
        return Promise.resolve(<any>this.serialize$umlp_backendrte_command_CommandResult(command));
    }

    public fromJson$de_monticore_symboltable_serialization_json_JsonObject(jsonObject: any): Promise<CommandResult<any>> {
        if (/* orElse */((v, v2) => v == null ? v2 : v)(jsonObject.getBooleanMemberOpt("statusResult"),false)){
            return Promise.resolve((v => { if (v == null) throw new Error('value is null'); return v; })(new StatusResult(jsonObject.getLongMember("commandId"), jsonObject.getBooleanMember("successful"))));
        }
        return Promise.resolve(null);
    }

    public fromJson(jsonObject?: any): Promise<CommandResult<any>> {
        return Promise.resolve(<any>this.fromJson$de_monticore_symboltable_serialization_json_JsonObject(jsonObject));
    }

    public fromJson$de_monticore_symboltable_serialization_json_JsonElement(jsonElement: any): Promise<CommandResult<any>> {
        return this.fromJson$de_monticore_symboltable_serialization_json_JsonObject(jsonElement.getAsJsonObject());
    }

    public deserialize(json: string): Promise<CommandResult<any>> {
        return Promise.resolve(this.fromJson$de_monticore_symboltable_serialization_json_JsonObject(JSON.parse(json)));
    }

    constructor() {
    }
}
CommandResultDeSer["__class"] = "umlp.backendrte.command.CommandResultDeSer";
CommandResultDeSer["__interfaces"] = ["umlp.backendrte.command.JsonDeSer","umlp.backendrte.command.DeSer"];

