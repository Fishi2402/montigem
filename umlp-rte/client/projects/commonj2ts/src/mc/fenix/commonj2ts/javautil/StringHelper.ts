export class StringHelper {
  public static format(str: string, ...val: string[]): string {
    let res = str;
    for (let v of val) {
      res = str.replace("%s", v);
    }
    return res;
  }

  public static join(delimiter: string, strs: string[]): string {
    return strs.join(delimiter);
  }
}
