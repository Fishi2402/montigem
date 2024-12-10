/* (c) https://github.com/MontiCore/monticore */

import { DeSer } from "../command/DeSer";

export class IntegerDeSer implements DeSer<number> {

  public serialize(obj: number): string {
    return obj.toString();
  }

  public deserialize(str: string): number {
    let e: any = JSON.parse(str);
    if (typeof e !== 'number') {
      return null;
    }

    return <number> e;
  }
}
