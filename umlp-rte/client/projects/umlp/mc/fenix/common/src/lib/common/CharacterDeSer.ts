/* (c) https://github.com/MontiCore/monticore */

import { DeSer } from "../command/DeSer";

export class CharacterDeSer implements DeSer<string> {

  public serialize(obj: string): string {
    return obj;
  }

  public deserialize(str: string): string {
    return str;
  }
}
