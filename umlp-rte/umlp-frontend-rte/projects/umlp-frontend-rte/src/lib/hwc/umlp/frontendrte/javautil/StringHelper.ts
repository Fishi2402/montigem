export class StringHelper {
  public static format(str: string, ...val: string[]): string {
    let res = str;
    for (let v of val) {
      res = str.replace("%s", v);
    }
    return res;
  }
}
