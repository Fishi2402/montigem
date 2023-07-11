import {DeSer} from "@umlp-rte/target/command/DeSer";

export class DateDeSer implements DeSer<Date> {
  serialize(obj: Date): string {
    return '"' + obj.toLocaleDateString() + '"';
  }

  deserialize(str: string): Date {
    return new Date(str.slice(1, str.length - 1));
  }
}
