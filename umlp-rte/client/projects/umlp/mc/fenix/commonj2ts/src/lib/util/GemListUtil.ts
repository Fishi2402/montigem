import {IUMLPList} from "../common";

export class GemListUtil {
  public static toArray<T>(list: IUMLPList<T>): Array<T> {
    if (list !== null && list !== undefined) {
      return list.toArray();
    } else {
      return [];
    }
  }
}
