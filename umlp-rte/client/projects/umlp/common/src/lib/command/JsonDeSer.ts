/* (c) https://github.com/MontiCore/monticore */

import { DeSer } from "./DeSer";

export interface JsonDeSer<T> extends DeSer<T> {
  fromJson(jsonElement: any): T;
  buildJson(obj: T): void ;
  /*
  default Optional<T> deserialize(String json){
    return fromJson(JsonParser.parse(json));
  }

  default String serialize(T obj){
    JsonPrinter printer = new JsonPrinter(true);
    buildJson(obj, printer);
    return printer.getContent();
  }
  /**/
}
