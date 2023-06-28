/* (c) https://github.com/MontiCore/monticore */

import { DeSer } from "../command/DeSer";

export class BooleanDeSer implements DeSer<boolean> {
  
  public serialize(obj: boolean): string  {
    return obj.toString();
  }

  public deserialize(str: string): boolean  {
    let e: any = JSON.parse(str);
    if (typeof e !== 'boolean') {
      return null;
    }

    return e;
  }
}
