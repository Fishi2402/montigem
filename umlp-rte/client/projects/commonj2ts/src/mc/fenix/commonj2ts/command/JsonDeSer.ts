/* (c) https://github.com/MontiCore/monticore */
import {JsonPrinter} from '../mcjson/JsonPrinter';
import {DeSer} from './DeSer';

export interface JsonDeSer<T> extends DeSer<T> {
  fromJson(jsonElement: any): Promise<T>;

  deserialize(json: string): Promise<T>;

  buildJson(obj: T, printer: JsonPrinter);

  serialize(obj: T): Promise<string>;
}

