import {Collection} from "../javautil";

export class GemListUtil {

  public static async toArraySync<T>(arr: Collection<T> | Array<T>): Promise<Array<T>> {
    if (arr === undefined || arr === null) {
      return [];
    } else if (arr instanceof Array) {
      return arr;
    } else {
      return await arr.toArrayDeepSync();
    }
  }
}
