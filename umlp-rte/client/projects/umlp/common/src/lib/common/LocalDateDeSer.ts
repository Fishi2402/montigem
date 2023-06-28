/* (c) https://github.com/MontiCore/monticore */

import { DeSer } from "../command/DeSer";

// TODO AGe: moment
export class LocalDateDeSer implements DeSer<Date> {

  public serialize(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
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
