import {DeSer} from "../command/DeSer";

export class DateDeSer implements DeSer<Date> {
  serialize(obj: Date): Promise<string> {
    return Promise.resolve('"' + obj.toLocaleDateString() + '"');
  }

  deserialize(str: string): Promise<Date> {
    return Promise.resolve(new Date(str.slice(1, str.length - 1)));
  }
}
