import {DateDeSer} from "../deser/DateDeser";
import {Log} from "../logging";
import {Conversion} from "../util/Conversion";

export class LocalDateDeSer extends DateDeSer {

  override serialize(date: Date): Promise<string> {
    return Promise.resolve(Conversion.date2String(date));
  }

  override deserialize(str: string): Promise<Date> {
    const pattern1 = /^\d{4}-\d{2}-\d{2}$/;
    const pattern2 = /^\"\d{4}-\d{2}-\d{2}\"$/;
    if (!pattern1.test(str) && !pattern2.test(str)) {
      Log.warn("LocalDate must be deserialized from json string");
      return null;
    }
    if (pattern2.test(str)) {
      str = str.substring(1, str.length - 1)
    }

    const parts = str.split('-');
    const [year, month, day] = [+parts[0], +parts[1], +parts[2]];

    return Promise.resolve(new Date(year, month - 1, day));
  }
}
