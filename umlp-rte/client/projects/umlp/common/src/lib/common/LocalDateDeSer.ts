/* (c) https://github.com/MontiCore/monticore */

import { DeSer } from "../command/DeSer";

export class LocalDateDeSer implements DeSer<Date> {

  public serialize(obj: Date): string {
    return obj.toISOString();
  }

  public deserialize(str: string): Date {
    if (Number.isNaN(Date.parse(str))) {
      console.warn("LocalDate must be deserialized from json string");
      return null;
    }

    return new Date(Date.parse(str));
  }
}
