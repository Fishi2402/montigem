import {DateDeSer} from "../deser/DateDeser";
import {Log} from "../logging";

export class LocalDateDeSer extends DateDeSer {

  override serialize(date: Date): Promise<string> {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return Promise.resolve(`${year}-${month}-${day}`);
  }

  override deserialize(str: string): Promise<Date> {
    const pattern = /^\d{4}-\d{2}-\d{2}$/;
    if (!pattern.test(str)) {
      Log.warn("LocalDate must be deserialized from json string");
      return null;
    }

    const parts = str.split('-');
    const [year, month, day] = [+parts[0], +parts[1], +parts[2]];

    return Promise.resolve(new Date(year, month - 1, day));
  }
}
