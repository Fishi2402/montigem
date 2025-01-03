/* (c) https://github.com/MontiCore/monticore */

import { JsonDeSer, JsonPrinter } from '@umlp/commonj2ts';
import { GemTextInputValidity } from '.';
import { GemTextInputValidityBuilder } from '.';

export class GemTextInputValidityDeSer implements JsonDeSer<GemTextInputValidity> {
    serializeStatus(obj: GemTextInputValidity, printer: JsonPrinter) {
        printer.member("status", obj.getStatus());
    }

    deserializeStatus(json: any): string {
        return /* getValue *//* getAsJsonString *//* getMember */json["status"];
    }

    serializeMessage(obj: GemTextInputValidity, printer: JsonPrinter) {
        printer.member("message", obj.getMessage());
    }

    deserializeMessage(json: any): string {
        return /* getValue *//* getAsJsonString *//* getMember */json["message"];
    }

    public async serialize(obj: GemTextInputValidity): Promise<string> {
        const jsonPrinter: JsonPrinter = new JsonPrinter(true);
        jsonPrinter.beginObject();
        this.buildJson(obj, jsonPrinter);
        jsonPrinter.endObject();
        return jsonPrinter.toString();
    }

    public async deserialize(json: any): Promise<GemTextInputValidity> {
        const builder: GemTextInputValidityBuilder = new GemTextInputValidityBuilder();
        builder.status(this.deserializeStatus(json)).message(this.deserializeMessage(json));
        return builder.build();
    }

    public buildJson(obj: GemTextInputValidity, jsonPrinter: JsonPrinter) {
        this.serializeStatus(obj, jsonPrinter);
        this.serializeMessage(obj, jsonPrinter);
    }

    public async fromJson(jsonElement: any): Promise<GemTextInputValidity> {
        return await this.deserialize(/* getAsJsonObject */jsonElement);
    }

    constructor() {
    }
}
GemTextInputValidityDeSer["__class"] = "mc.fenix.input.gemtextinputtypes.GemTextInputValidityDeSer";
GemTextInputValidityDeSer["__interfaces"] = ["umlp.backendrte.command.JsonDeSer","umlp.backendrte.command.DeSer"];