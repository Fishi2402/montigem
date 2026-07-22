/* (c) https://github.com/MontiCore/monticore */
import {JsonPrinter} from '../mcjson/JsonPrinter';
import {DeSer} from './DeSer';

export interface JsonDeSer<T> extends DeSer<T> {
  fromJson(jsonElement: any): Promise<T>;

  deserialize(json: string): Promise<T>;

  buildJson(obj: T, printer: JsonPrinter);

  /**
   * Wire identifier of the type this DeSer handles, e.g. "cdbase.ConcreteBaseClass".
   * Must match the typeParameter keys used by the CommandResultDeSers. Generated
   * domain DeSers provide this; absent means the DeSer has no wire identity.
   */
  getTypeIdentifier?(): string;

  serialize(obj: T): Promise<string>;
}

