/* (c) https://github.com/MontiCore/monticore */
import {JsonDeSer} from '../command/JsonDeSer';

export interface GemIdentifiable {
  getGemId(): number;

  /**
   * The DeSer for the concrete runtime type of this object. Generated domain
   * classes provide this; serialization uses it to keep the concrete type
   * across the wire (monticore#5062). Optional so handwritten implementors
   * do not break; callers must fall back to the declared type when absent.
   * JSweet transpiles the generated implementations as async, hence the union.
   */
  getDeSer?(): Promise<JsonDeSer<any>> | JsonDeSer<any>;
}
