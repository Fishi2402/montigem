/* (c) https://github.com/MontiCore/monticore */

import { DeSer } from "../command/DeSer";
import { Conversion } from "../util/DateFormat";

// TODO AGe: moment
export class LocalDateDeSer implements DeSer<Date> {

  public serialize(date: Date): string {
    return Conversion.date2String(date);
  }

  public deserialize(str: string): Date {
    const pattern = /^\d{4}-\d{2}-\d{2}$/;
    if (!pattern.test(str)) {
      console.warn("LocalDate must be deserialized from json string");
      return null;
    }

    const parts = str.split('-');
    const [year, month, day] = [+parts[0], +parts[1], +parts[2]];

    return new Date(year, month - 1, day);
  }
}
