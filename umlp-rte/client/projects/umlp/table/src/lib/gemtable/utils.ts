export class Utils {
  /**
   * Merge second object into the first one
   * @param obj1 Object that is merged into
   * @param obj2 Object to merge into obj1
   * @param overwrite Whether values in obj1 should get overridden by values from obj2. Default is false.
   */
  // tslint:disable-next-line:ban-types
  public static mergeIn(
    obj1: Object,
    obj2: Object,
    overwrite: boolean = false
  ): Object {
    if (obj1 === undefined) {
      obj1 = obj2;
      return obj2;
    }
    if (!obj2) {
      return obj1;
    }
    const keys1 = Object.keys(obj1);
    const keys2 = Object.keys(obj2);
    if (!keys2.length) {
      return obj1;
    }
    keys2.forEach((key) => {
      if (keys1.indexOf(key) === -1 || overwrite) {
        obj1[key] = obj2[key];
      } else {
        if (
          Utils.isNonNullObject(obj1[key]) &&
          Utils.isNonNullObject(obj2[key])
        ) {
          Utils.mergeIn(obj1[key], obj2[key]);
        }
      }
    });
    return obj1;
  }

  public static containsKeyValues(obj1: any, obj2: any): boolean {
    if (obj2 === obj1) {
      return true;
    }
    if (obj1 == null || obj2 == null) {
      return false;
    }
    const keysGroupExtension = Object.keys(obj2);
    const keysExpansionExtension = Object.keys(obj1);
    if (keysGroupExtension.length !== keysExpansionExtension.length) {
      return false;
    }
    for (let i = 0; i < keysGroupExtension.length; ++i) {
      const keyGroup = keysGroupExtension[i];
      const keyExpansion = keysExpansionExtension[i];
      if (keyGroup !== keyExpansion || obj2[keyGroup] !== obj1[keyGroup]) {
        return false;
      }
    }
    return true;
  }

  private static isNonNullObject(obj: any): boolean {
    return typeof obj === "object" && obj !== null;
  }
}
