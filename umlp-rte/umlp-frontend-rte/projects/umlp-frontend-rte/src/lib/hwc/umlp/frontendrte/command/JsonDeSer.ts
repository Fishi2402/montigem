/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import {JsonPrinter} from '../mcjson/JsonPrinter';
import {DeSer} from './DeSer';

export interface JsonDeSer<T> extends DeSer<T> {
  fromJson(jsonElement: any): Promise<T>;

  deserialize(json: string): Promise<T>;

  buildJson(obj: T, printer: JsonPrinter);

  serialize(obj: T): Promise<string>;
}

