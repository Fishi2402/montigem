import {IUMLPList} from "../common";

export class GemListUtil {
  public static toArray<T>(arr: IUMLPList<T> | Array<T>): Array<T> {
    return (arr === undefined || arr === null) ? [] : (arr instanceof Array ? arr : arr.toArrayDeep());
  }

  public static async toArraySync<T>(arr: IUMLPList<T> | Array<T>): Promise<Array<T>> {
    if (arr === undefined || arr === null) {
      return [];
    } else if (arr instanceof Array) {
      return arr;
    } else {
      return await arr.toArrayDeepSync();
    }
  }
}
