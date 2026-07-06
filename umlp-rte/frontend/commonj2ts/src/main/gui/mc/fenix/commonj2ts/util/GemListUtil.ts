import {IUMLPList} from "../common";
import {Collection} from "../javautil";

export class GemListUtil {
  // TODO is this ever used? Remove
  public static toArray<T>(arr: Collection<T> | Array<T>): Array<T> {
    return (arr === undefined || arr === null) ? [] : (arr instanceof Array ? arr : arr.toArrayDeep());
  }

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
